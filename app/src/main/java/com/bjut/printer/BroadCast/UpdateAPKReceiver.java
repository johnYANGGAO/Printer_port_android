package com.bjut.printer.BroadCast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bjut.printer.Consts.PublicConsts;
import com.bjut.printer.Views.MyDialog;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UpdateAPKReceiver extends BroadcastReceiver {

    private Handler handler;

    public UpdateAPKReceiver(Handler mHandler) {
        this.handler = mHandler;
    }

    public UpdateAPKReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (PublicConsts.PROGRESS_RATE_ACTION.equals(action)) {

            Message msg = handler.obtainMessage();
            msg.arg1 = intent.getIntExtra(PublicConsts.PROGRESS_RATE_ACTION, 0);
            msg.what = PublicConsts.PROGRESS_RATE_WHAT;
            handler.sendMessage(msg);
        } else if (PublicConsts.DOWNLOAD_DIALOG_ACTION.equals(action)) {

            Message msg = handler.obtainMessage();
            msg.what = PublicConsts.DOWNLOAD_DIALOG_WHAT;
            msg.obj = intent.getStringExtra(PublicConsts.DOWNLOAD_DIALOG_ACTION);
            handler.sendMessage(msg);

        } else if (PublicConsts.DOWNLOAD_OVER_ACTION.equals(action)) {

            handler.sendEmptyMessage(PublicConsts.DOWNLOAD_OVER_WHAT);

        } else if (PublicConsts.NOTIFICATION_SUCCESS.equals(action)) {
            Log.i("UpdateApkReceiver","get success notification flag");
            MyDialog myDialog = new MyDialog(context);

            myDialog.getNotifiCationDialog(SweetAlertDialog.SUCCESS_TYPE, "note",
                    intent.getStringExtra(PublicConsts.NOTIFICATION_SUCCESS)).show();

        }
    }
}
