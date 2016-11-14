package com.bjut.printer.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;

import com.bjut.printer.BroadCast.ErrorBroadCastReceiver;
import com.bjut.printer.BroadCast.UpdateAPKReceiver;
import com.bjut.printer.Consts.PublicConsts;
import com.bjut.printer.Operator.SendBroadCast;
import com.bjut.printer.UpdateApk.UpdateDialogManager;
import com.bjut.printer.Views.MyDialog;
import com.bjut.printer.application.MyApplication;
import com.bjut.printer.services.CheckOrDownloadIntentService;
import com.bjut.printer.services.IOService;
import com.bjut.printer.services.UsbStatusReceiver;
import com.bjut.printer.utils.NetWorkUtil;
import com.bjut.printer2.R;
import com.igexin.sdk.PushManager;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by johnsmac on 9/12/16.
 */
//register broadcast here
public class BaseActivity extends Activity {

    protected MyDialog myDialog;
    protected ErrorBroadCastReceiver errorBroadCastReceiver;
    protected UpdateAPKReceiver updateAPKReceiver;
    protected UsbStatusReceiver usbStatusReceiver;
    protected Dialog dialog;

    protected IOService ioService;
    protected boolean mBound;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            IOService.IOBinder binder = (IOService.IOBinder) service;
            ioService = binder.getService();
            Log.i("IO", "Activity bind service  success");
            ioService.writeTOSerialPort("M880\n");
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.e("IO", "levelingActivity bind service failed");
            if (ioService != null) {
                ioService = null;
            }
            mBound = false;
        }
    };
    protected Handler apkHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PublicConsts.DOWNLOAD_DIALOG_WHAT:
                    dialog = new Dialog(BaseActivity.this, R.style.Theme_loadingDialog);

                    UpdateDialogManager.initUpdateDialog(BaseActivity.this, dialog, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //parameter not yet
                            CheckOrDownloadIntentService.startActionDownload(BaseActivity.this, "", "");
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.cancel();
                            CheckOrDownloadIntentService.CancelDownloading();
                        }
                    });
                    UpdateDialogManager.setTitle("download");
                    Log.i("BaseActivity",msg.obj.toString()==null?"descriptor is null":msg.obj.toString());
                    UpdateDialogManager.setContent(msg.obj.toString());
                    dialog.show();
                    break;
                case PublicConsts.PROGRESS_RATE_WHAT:
                    // update the progress rate;
                    int progress = msg.arg1;
                    if (dialog.isShowing()) {
                        UpdateDialogManager.setPrgb(progress);
                    }

                    break;
                case PublicConsts.DOWNLOAD_OVER_WHAT:

                    if (dialog.isShowing()) {
                        dialog.cancel();
                    }
            }
        }
    };
    Handler usbHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case UsbStatusReceiver.USB_STATE_ON:

                    myDialog.getNotifiCationDialog(SweetAlertDialog.SUCCESS_TYPE, "note", "发现 USB 存储设备 ");
                    break;
                case UsbStatusReceiver.USB_STATE_OFF:
                    myDialog.getNotifiCationDialog(SweetAlertDialog.WARNING_TYPE, "note", "USB 设备被移除");

                    break;


            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        PushManager.getInstance().initialize(this.getApplicationContext());
        myDialog=new MyDialog(this);
        errorBroadCastReceiver = new ErrorBroadCastReceiver(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                finish();
            }
        });
        updateAPKReceiver = new UpdateAPKReceiver(apkHandler);
        usbStatusReceiver = new UsbStatusReceiver(usbHandler);

        registerReceiver(usbStatusReceiver, usbStatusReceiver.getUsbFilter());
        registerErrorBroad();
        registerAPKUpdateBroad();
        Intent intent = new Intent(this, IOService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onDestroy() {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.cancel();
            }
            dialog = null;
        }
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        unRegisterBroadAll();
        super.onDestroy();
    }

    public void registerErrorBroad() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(PublicConsts.ERROR_ACTION);
        filter.addAction(PublicConsts.ERROR_ACTION_REBOOT);
        registerReceiver(errorBroadCastReceiver, filter);

    }


    public void registerAPKUpdateBroad() {

        IntentFilter filter = new IntentFilter();
        filter.addAction(PublicConsts.PROGRESS_RATE_ACTION);
        filter.addAction(PublicConsts.DOWNLOAD_DIALOG_ACTION);
        filter.addAction(PublicConsts.NOTIFICATION_SUCCESS);
        registerReceiver(updateAPKReceiver, filter);

        Log.i("BaseActivity","has registered apk update broadcast");
    }

    public void unRegisterBroadAll() {
        if (errorBroadCastReceiver != null) {
            unregisterReceiver(errorBroadCastReceiver);
            errorBroadCastReceiver = null;
        }
        if (updateAPKReceiver != null) {
            unregisterReceiver(updateAPKReceiver);
            updateAPKReceiver = null;
        }
        if (usbStatusReceiver != null) {

            unregisterReceiver(usbStatusReceiver);
            usbStatusReceiver = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
