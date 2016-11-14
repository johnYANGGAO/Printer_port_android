package com.bjut.printer.BroadCast;
/**
 * Created by johnsmac on 9/13/16.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.bjut.printer.Consts.PublicConsts;
import com.bjut.printer.Views.MyDialog;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ErrorBroadCastReceiver extends BroadcastReceiver {

    private SweetAlertDialog.OnSweetClickListener listener;

    public ErrorBroadCastReceiver(SweetAlertDialog.OnSweetClickListener mListener) {

        this.listener = mListener;
    }

    public ErrorBroadCastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (TextUtils.isEmpty(action)) {
            return;
        }
        if (PublicConsts.ERROR_ACTION.equals(action)) {

            MyDialog myDialog = new MyDialog(context);

            myDialog.getNotifiCationDialog(SweetAlertDialog.ERROR_TYPE, "oops",
                    intent.getStringExtra(PublicConsts.ERROR_ACTION)).show();

        } else if (PublicConsts.ERROR_ACTION_REBOOT.equals(action)) {

            MyDialog myDialog = new MyDialog(context);

            myDialog.getClickDialog(SweetAlertDialog.ERROR_TYPE, "reboot",
                    intent.getStringExtra(PublicConsts.ERROR_ACTION_REBOOT),
                    "sure", listener).show();

        }
    }
}
