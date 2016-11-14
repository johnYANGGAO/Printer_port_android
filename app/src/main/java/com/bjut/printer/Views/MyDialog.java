package com.bjut.printer.Views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import com.bjut.printer.activity.FileChooser;
import com.bjut.printer.utils.FileUtils;
import com.bjut.printer2.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by johnsmac on 9/11/16.
 */
public class MyDialog {
    /**
     * three types :two of them for notification the last one for do some specifically thing.
     */
    private Context mContext;

    public MyDialog(Context context) {

        this.mContext = context;

    }

    /**
     * public static final int NORMAL_TYPE = 0;
     * public static final int ERROR_TYPE = 1;
     * public static final int SUCCESS_TYPE = 2;
     * public static final int WARNING_TYPE = 3;
     * public static final int CUSTOM_IMAGE_TYPE = 4;
     * public static final int PROGRESS_TYPE = 5;
     */
    public Dialog getNotifiCationDialog(int type, String title, String content) {

        return new SweetAlertDialog(mContext, type)
                .setTitleText(title)
                .setContentText(content);
    }

    public Dialog getClickDialog(int type, String title, String content, String confirm, SweetAlertDialog.OnSweetClickListener listener) {

        SweetAlertDialog comfirmDialog = new SweetAlertDialog(mContext, type)
                .setTitleText(title)
                .setContentText(content)
                .setConfirmText(confirm)
                .setConfirmClickListener(listener);

        return comfirmDialog;
    }

    public Dialog getOptionDialog(int type, String title, String content, String confirm, SweetAlertDialog.OnSweetClickListener listener) {

        SweetAlertDialog optionDialog = new SweetAlertDialog(mContext, type)
                .setTitleText(title)
                .setContentText(content)
                .setConfirmText(confirm)
                .setConfirmClickListener(listener).showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                });

        return optionDialog;


    }

    public Dialog getCancelOptionDialog(int type, String title, String content, String confirm, SweetAlertDialog.OnSweetClickListener sure, SweetAlertDialog.OnSweetClickListener cancel) {

        SweetAlertDialog cancelandConfirmDialog = new SweetAlertDialog(mContext, type)
                .setTitleText(title)
                .setContentText(content)
                .setConfirmText(confirm)
                .setConfirmClickListener(sure).showCancelButton(true)
                .setCancelClickListener(cancel);

        return cancelandConfirmDialog;

    }

    public Dialog getLoadingDialog(String notifyTitle) {

        SweetAlertDialog pDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(notifyTitle);
        pDialog.setCancelable(false);

        return pDialog;
    }

    public Dialog showUsbDialog(final Activity context){

        return getOptionDialog(SweetAlertDialog.NORMAL_TYPE, "note",
                "系统检测到SD卡插入，需要打开SD卡选择打印文件吗?", "确认", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent intent = new Intent(context, FileChooser.class);
                        intent.putExtra(FileChooser.EXTRA_TITLE, context.getResources().getString(R.string.app_name));
                        intent.putExtra(FileChooser.EXTRA_FILEPATH, FileUtils.GetUsbDiskPath());
                        intent.putExtra(FileChooser.EXTRA_FILTERS, new String[]{".gcode"});
                        context.startActivityForResult(intent, 1);
                    }
                });

    }


}
