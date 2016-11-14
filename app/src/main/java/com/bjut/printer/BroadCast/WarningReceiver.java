package com.bjut.printer.BroadCast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bjut.printer.Consts.PublicConsts;
import com.bjut.printer.Views.MyDialog;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by johnsmac on 9/16/16.
 */
//put this receiver into main activity including printing move on or not
public class WarningReceiver extends BroadcastReceiver {

    private SweetAlertDialog.OnSweetClickListener notice;//do stuff continue print

    public WarningReceiver(SweetAlertDialog.OnSweetClickListener mNotice) {

        this.notice = mNotice;
    }
    public WarningReceiver(){}

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            if (PublicConsts.WARNING_ACTION.equals(action)) {

                MyDialog myDialog = new MyDialog(context);

                myDialog.getNotifiCationDialog(SweetAlertDialog.WARNING_TYPE, "Waning",
                        intent.getStringExtra(PublicConsts.WARNING_ACTION)).show();

            } else if (PublicConsts.WARNING_CONTINUE_PRINT_ACTION.equals(action)) {

                MyDialog myDialog = new MyDialog(context);
                myDialog.getOptionDialog(SweetAlertDialog.WARNING_TYPE, "warning",
                        intent.getStringExtra(PublicConsts.WARNING_CONTINUE_PRINT_ACTION), "print", notice).show();

            }

        }
    }
}

