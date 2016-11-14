package com.bjut.printer.ThreadManager;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bjut.printer.Consts.PublicConsts;
import com.bjut.printer.Operator.IOCallBack;
import com.bjut.printer.Operator.SendBroadCast;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by johnsmac on 9/13/16.
 */
public class ReadThread extends Thread {

    private InputStream mInputStream;
    private IOCallBack callBack;
    private Context mContext;

    public ReadThread(Context context,InputStream inputStream) {

        this.mContext = context;
        this.mInputStream=inputStream;
    }

    //we put result processing in service
    public void setIOCallBack(IOCallBack ioCallBack) {

        this.callBack = ioCallBack;

    }

    @Override
    public void run() {
        super.run();
        while (!isInterrupted()) {
            Log.i("SerialPort","IO Service--->ReadThread is running ");
            int size;
            try {
                byte[] buffer = new byte[256];
                if (mInputStream == null) {
                    return;
                }
                size = mInputStream.read(buffer);
                if (size > 0) {
                    if (callBack != null) {
                        callBack.onDataReceived(buffer, size);
                    } else {
                        SendBroadCast.sendError(mContext, "Not implement the IOCallBack");
                        return;
                    }
                }
            } catch (IOException e) {
                SendBroadCast.sendError(mContext, e.getLocalizedMessage());
                return;
            }

        }
    }

}
