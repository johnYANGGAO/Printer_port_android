package com.bjut.printer.ThreadManager;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.bjut.printer.Consts.PublicConsts;
import com.bjut.printer.Operator.SendBroadCast;
import com.bjut.printer.utils.FileUtils;

/**
 * Created by johnsmac on 9/16/16.
 */
public class USBstatusThread extends Thread {

    private Handler UIHandler;
    private Context context;
    private boolean isDown;
    public USBstatusThread(Handler handler,Context mContext) {
        this.UIHandler = handler;
        this.context=mContext;
    }

    @Override
    public void run() {
        Log.i("MainActivity","USBstatusThread from ServerSocketService is running");
        do {
            try {
                Thread.sleep(5000);

                if (FileUtils.GetUsbDiskPath() != null) {

                    UIHandler.sendEmptyMessage(PublicConsts.USB_STATUS_THREAD_ON);
                } else {
                    UIHandler.sendEmptyMessage(PublicConsts.USB_STATUS_THREAD_OFF);
                }


            } catch (InterruptedException e) {
                isDown = true;
                SendBroadCast.sendError(context, e.getLocalizedMessage());
            }
        } while (!isDown);
    }
}
