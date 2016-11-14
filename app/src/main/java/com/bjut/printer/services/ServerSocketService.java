package com.bjut.printer.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;

import com.bjut.printer.ThreadManager.TemperatureThread;
import com.bjut.printer.ThreadManager.TimeThread;
import com.bjut.printer.ThreadManager.USBstatusThread;
import com.bjut.printer.application.MyApplication;

import java.io.File;

public class ServerSocketService extends Service {
//    private ServerSocketWifi serverSocketWifi = null;//never used ?
    MyApplication mApplication;
    String savePath = null;
    LocalBinder mBinder = new LocalBinder();

    private String getSDPath() {
        File sdDir;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            sdDir = Environment.getExternalStorageDirectory();
        } else {
            return null;
        }

        return sdDir.toString();

    }

    @Override
    public IBinder onBind(Intent arg0) {

        return mBinder;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        mApplication = (MyApplication) getApplication();
        savePath = getSDPath() + "/Gcode";//yes ,here you are ,remember you in MyApplication?
//        new Thread() {
//            public void run() {
//                UtilSendAndReceiveFile.mApplication = mApplication;
//                UtilSendAndReceiveFile.savePath = savePath;
//                serverSocketWifi = new ServerSocketWifi();
//            }
//        }.start();

    }

    public class LocalBinder extends Binder {
        public ServerSocketService getService() {
            return ServerSocketService.this;
        }
    }


//    public ServerSocketWifi getServerSocketWifi() {
//        return serverSocketWifi;
//    }
//
//    public void setServerSocketWifi(ServerSocketWifi serverSocketWifi) {
//        this.serverSocketWifi = serverSocketWifi;
//    }

    public void startStatusThread(Context context, Handler handler, boolean timeStatus, boolean temperatureStatus) {
        usBstatusThread = new USBstatusThread(handler, context);
        timeThread = new TimeThread(context, timeStatus, handler);
        temperatureThread = new TemperatureThread(context, handler, temperatureStatus);

        usBstatusThread.start();
        timeThread.start();
        temperatureThread.start();


    }

    private TemperatureThread temperatureThread;
    private TimeThread timeThread;
    private USBstatusThread usBstatusThread;

    @Override
    public boolean onUnbind(Intent intent) {

        if (usBstatusThread != null) {
            usBstatusThread.interrupt();
            usBstatusThread = null;
        }
        if (timeThread != null) {
            timeThread.interrupt();
            timeThread = null;
        }
        if (temperatureThread != null) {
            temperatureThread.interrupt();
            temperatureThread = null;
        }

        return super.onUnbind(intent);
    }
}
