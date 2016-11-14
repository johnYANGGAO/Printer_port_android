package com.bjut.printer.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bjut.printer.Consts.PublicConsts;
import com.bjut.printer.Operator.SendBroadCast;
import com.bjut.printer.ThreadManager.CheckVersionThread;
import com.bjut.printer.ThreadManager.DownloadAPKThread;
import com.bjut.printer.UpdateApk.UpdateConsts;
import com.bjut.printer.application.MyApplication;
import com.bjut.printer.bean.UpdateInfo;
import com.bjut.printer.Views.MyDialog;

import java.io.File;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by johnsmac on 9/15/16.
 */


/**
 * IntentService is used for short tasks (etc) and a service is for long ones
 * So we can make it to check or download new version' apk
 */
public class CheckOrDownloadIntentService extends IntentService {

    private static final String ACTION_CHECK = "check";
    private static final String ACTION_DOWNLOAD = "download";

    private static final String EXTRA_PARAM1 = "checkKey";
    private static final String EXTRA_PARAM2 = "downloadKey";

    private static CheckVersionThread checkVersionThread;
    private static DownloadAPKThread downloadVersionThread;
    private static UpdateInfo updateInfo;

    //pass UpdateInfo here and show dialog to notify start download task
    //and install
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case PublicConsts.UPDATE_INFO_WHAT:

                    Log.i("SerialPort", "CheckOrDownloadIntent Service--->get update info ");
                    /**retrieve the model UpdateInfo and to compare the local version ,
                     which determine show dialog to download or not
                     in the main activity using broadcast*/
//
                    updateInfo = msg.getData().getParcelable(PublicConsts.UPDATE_INFO);
                    Log.i("SerialPort", updateInfo.getDownloadAddress() + "---" + updateInfo.getSoftwareVersion() + "---" + updateInfo.getVersionDesc());
                    if (updateInfo.getSoftwareVersion() == null) {
                        break;
                    }
                    if (isNewVersion(updateInfo.getSoftwareVersion())) {
                        // IF CLICKED THE BUTTON DOWNLOADED START METHOD DOWNLOAD IN THIS SERVICE
                        // SO THERE IS NO NEED INFO TRANSFER .
                        SendBroadCast.sendDownloadDialog(MyApplication.mContext, PublicConsts.DOWNLOAD_DIALOG_ACTION, updateInfo.getVersionDesc());
                        Log.i("SerialPort", "new version has come ");
                    } else {
                        Log.i("SerialPort", "same version ");
                        SendBroadCast.sendSuccess(MyApplication.mContext, "已经是最新版本");
                    }

                    break;

                case UpdateConsts.DOWN_OVER:
                    //show dialog to install,but we need to close the downloading dialog first!
                    SendBroadCast.sendDownloadDialog(MyApplication.mContext, PublicConsts.DOWNLOAD_OVER_ACTION, null);

                    MyDialog myDialog = new MyDialog(CheckOrDownloadIntentService.this);
                    myDialog.getOptionDialog(SweetAlertDialog.SUCCESS_TYPE, "install", "更新版本下载完成，请安装", "确定", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            installApk();
                        }
                    }).show();


                    break;

            }

        }
    };


    public CheckOrDownloadIntentService() {
        super("CheckOrDownloadIntentService");
    }


    public static void startActionCheck(Context context, String param1, String param2) {
        Intent intent = new Intent(context, CheckOrDownloadIntentService.class);
        intent.setAction(ACTION_CHECK);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }


    public static void startActionDownload(Context context, String param1, String param2) {
        Intent intent = new Intent(context, CheckOrDownloadIntentService.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * IntentService can GC when thread is down????
     *
     * */
    public static  void CancelDownloading(){
        Log.i("DownloadThread"," downloadThread IS  null");//ALWAYS NULL
        if (downloadVersionThread!=null){
            Log.i("DownloadThread"," downloadThread IS not null");
            if (downloadVersionThread.isAlive()){
                downloadVersionThread.cancelDownload(true);
                Log.i("DownloadThread","canceled downloading thread");
            }
        }

    }
    @Override
    protected void onHandleIntent(Intent intent) {

        checkVersionThread = new CheckVersionThread(this, handler);


        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CHECK.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionCheck(param1, param2);
            } else if (ACTION_DOWNLOAD.equals(action)) {
                downloadVersionThread = new DownloadAPKThread(this, handler, updateInfo.getDownloadAddress());
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionDownload(param1, param2);
            }
        }
    }

    /**
     * handleActionCheck is also in the provided background thread
     * with the provided  parameters.so there is no need in the
     * another Thread ,but ,here we still use thread for convenient
     */

    private void handleActionCheck(String param1, String param2) {

        checkVersionThread.start();

    }


    private void handleActionDownload(String param1, String param2) {

        downloadVersionThread.start();
    }

    /**
     * apk installation
     */
    private void installApk() {

        File apkFile = new File(UpdateConsts.saveFileName);
        if (!apkFile.exists()) {
            apkFile.mkdir();
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkFile.toString()), "application/vnd.android.package-archive");
        this.startActivity(i);
    }


    private Boolean isNewVersion(String versionStr) {
        String software_version;
        PackageManager manager = MyApplication.mContext.getPackageManager();
        PackageInfo info;
        try {
            info = manager.getPackageInfo(MyApplication.mContext.getPackageName(), 0);
            software_version = info.versionName.trim();

            SendBroadCast.sendModifyUI(this, PublicConsts.ACTION_TEXT_VERSION, software_version);
            // text_version.setText("version:" + software_version);
            float local_version = Float.valueOf(software_version);
            float remote_version = Float.valueOf(versionStr);
            if (remote_version > local_version || versionStr.equals("1")) {

                return true;
            } else {
                return false;
            }

        } catch (PackageManager.NameNotFoundException e) {
            SendBroadCast.sendError(CheckOrDownloadIntentService.this, e.getLocalizedMessage());
        }
        return false;
    }

    public static void endThreadAll() {

        if (checkVersionThread != null) {

            checkVersionThread.interrupt();
            checkVersionThread = null;

        }
        if (downloadVersionThread != null) {

            downloadVersionThread.interrupt();
            downloadVersionThread = null;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        endThreadAll();
    }
}

