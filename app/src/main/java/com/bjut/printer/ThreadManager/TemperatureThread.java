package com.bjut.printer.ThreadManager;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.bjut.printer.Operator.SendBroadCast;

/**
 * Created by johnsmac on 9/16/16.
 */
public class TemperatureThread extends Thread {


    private boolean temperaturestatus, isDown;
    private Handler UIHandler;
    private Context context;

    public TemperatureThread(Context mContext, Handler handler, Boolean status) {

        this.temperaturestatus = status;
        this.UIHandler = handler;
        this.context = mContext;

    }

    @Override
    public void run() {
        Log.i("MainActivity","TemperatureThread from ServerSocketService is running");
        do {
            try {
                if (temperaturestatus) {
                    UIHandler.sendEmptyMessage(8);
                }
                Thread.sleep(9000);
            } catch (InterruptedException e) {
                isDown = true;
                SendBroadCast.sendError(context, e.getLocalizedMessage());
            }

        } while (!isDown);

    }
}