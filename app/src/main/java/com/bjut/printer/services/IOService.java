package com.bjut.printer.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.bjut.printer.Operator.IOCallBack;
import com.bjut.printer.Operator.ParseInputCode;
import com.bjut.printer.Operator.SendBroadCast;
import com.bjut.printer.ThreadManager.ReadThread;
import com.bjut.printer.application.MyApplication;
import com.bjut.printer.bean.GlobalEditor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;

/**
 * Created by johnsmac on 9/14/16.
 */
//todo
public class IOService extends Service implements IOCallBack {

    private ReadThread readThread;
    private InputStream inputStream;
    private OutputStream outputStream;
    protected MyApplication mApplication;
    public SerialPort mSerialPort;
    private final IBinder mBinder = new IOBinder();
    private ParseInputCode parseInputCode;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = (MyApplication) getApplication();
        parseInputCode = new ParseInputCode(this, mApplication, GlobalEditor.getSharedataeditorInstance(this));
        try {
            Log.i("IO", mApplication.mMainActivity == null ? "ioService main  is null" : "main with base has value");
            mSerialPort = mApplication.getSerialPort(mApplication.mMainActivity);

            if (mSerialPort == null) {
                Log.i("SerialPort", "IO Service--->mSerialPort is null ");
                SendBroadCast.sendReboot(this, "can't open SerialPort please reboot again");

            } else {

                inputStream = mSerialPort.getInputStream();
                outputStream = mSerialPort.getOutputStream();
                Log.i("SerialPort", "IO Service--->mSerialPort is getting Stream ");
                if (inputStream != null) {

                    readThread = new ReadThread(this, inputStream);
                    readThread.setIOCallBack(this);
                    readThread.start();

                    //  if opening port down we send Broadcast to start ServerSocketService
                    SendBroadCast.sendStartSocketService(this);
                    Log.i("SerialPort", "IO Service--->wake ServerSocketService up by sending broadcast");
                }

            }
        } catch (SecurityException e) {
            SendBroadCast.sendReboot(this, "read/write permission denied");
        } catch (IOException e) {
            SendBroadCast.sendReboot(this, "The serial port can not be opened for an unknown reason");
        } catch (InvalidParameterException e) {
            SendBroadCast.sendReboot(this, "Please configure your serial port first.");
        }

    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    public void onDestroy() {

        if (readThread != null) {
            readThread.interrupt();
        }
        mApplication.closeSerialPort();
        mSerialPort = null;
        super.onDestroy();
    }

    // method bellow to handle  data received.
    // 串口通信 有慢性 导致分包发送现象 所以接收方以 \n 来终结 要做全局变量处理
    private String receiveBufferString;

    @Override
    public void onDataReceived(byte[] buffer, int size) {

        String receiveString = new String(buffer, 0, size);

        receiveBufferString = receiveBufferString + receiveString;

        if (receiveBufferString.contains("\n")) {
            if (receiveBufferString.trim().equals("") == false) {

                String[] temp = receiveBufferString.split("\n");
                int length = temp.length;
                if (temp != null && length > 0) {
                    for (int i = 0; i < length; i++) {
                        String str = temp[i];
                        if (str != null && str.trim().equals("") == false) {

                            parseInputCode.ProcessReceived(str);
                        }

                    }
                    String lastStr = temp[size - 1];

                    if (receiveBufferString.endsWith("\n")) {

                        receiveBufferString = "";

                    } else {
                        receiveBufferString = lastStr;
                    }
                }

            }
        }
    }


    public void writeTOSerialPort(String command) {

        if (outputStream != null) {
            try {
                outputStream.write(command.getBytes("UTF8"));
            } catch (IOException e) {
                SendBroadCast.sendError(this, "failed  order transfer");
            }
        }
    }

    public class IOBinder extends Binder {
        public IOService getService() {
            return IOService.this;
        }
    }
}
