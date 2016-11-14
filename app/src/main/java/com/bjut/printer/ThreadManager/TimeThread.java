package com.bjut.printer.ThreadManager;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.bjut.printer.Operator.SendBroadCast;

/**
 * Created by johnsmac on 9/16/16.
 */
public class TimeThread extends Thread {

    private boolean timestatus, isDown;
    private Handler UIHandler;
    private Context context;

    public TimeThread(Context mContext, Boolean status, Handler handler) {
        this.timestatus = status;
        this.UIHandler = handler;
        this.context = mContext;
    }

    @Override
    public void run() {
        Log.i("MainActivity","TimeThread from ServerSocketService is running");
        do {
            try {
                Thread.sleep(1000);
                if (timestatus) {
                    UIHandler.sendEmptyMessage(3);
                }
            } catch (InterruptedException e) {
                isDown = true;
                SendBroadCast.sendError(context, e.getLocalizedMessage());
            }
        } while (!isDown);
    }
}