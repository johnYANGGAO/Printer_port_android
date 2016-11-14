package com.bjut.printer.ThreadManager;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.bjut.printer.Operator.SendBroadCast;
import com.bjut.printer.UpdateApk.UpdateConsts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by johnsmac on 9/15/16.
 */
public class DownloadAPKThread extends Thread {

    private Handler handler;
    private Context context;
    private boolean isDown = false;
    private String downloadUrl;

    public DownloadAPKThread(Context mContext, Handler mHandler, String url) {

        this.handler = mHandler;
        this.context = mContext;
        this.downloadUrl = url;

    }

    /**
     * this function we made is intended for Remote control,
     * which lets the user cancel participation in the task at any time
     *
     */
    public void cancelDownload(Boolean cancel) {
        this.isDown = cancel;
    }

    @Override
    public void run() {
        super.run();
        Log.i("SerialPort","CheckOrDownloadIntent Service--->DownloadApkThread is running ");
        if (downloadUrl == null) {
            SendBroadCast.sendError(context, "APK下载无路径");
            return;
        }
        while (!isDown) {
            try {
                URL url = new URL(downloadUrl);
                int progress;
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                if (200 != conn.getResponseCode()) {

                    isDown = true;
                    SendBroadCast.sendError(context, "APK下载连接失败");

                } else {

                    int length = conn.getContentLength();
                    InputStream is = conn.getInputStream();

                    File file = new File(UpdateConsts.savePath);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    String apkFile = UpdateConsts.saveFileName;
                    File ApkFile = new File(apkFile);
                    FileOutputStream fos = new FileOutputStream(ApkFile);

                    int count = 0;
                    byte buf[] = new byte[1024];

                    do {
                        int num = is.read(buf);
                        count += num;
                        progress = (int) (((float) count / length) * 100);
                        /**
                         WE WILL SEND BROADCAST TO NOTICE MAIN ACTIVITY LET HANDLER HANDLE PROGRESS
                         why ? 'cause need progressbar */

                        SendBroadCast.sendProgressRate(context, progress);

                        if (num <= 0) {
                            //notify ending this thread in CheckOrDownloadIntentService
                            handler.sendEmptyMessage(UpdateConsts.DOWN_OVER);
                            break;
                        }
                        fos.write(buf, 0, num);
                    } while (!isDown);//TODO it should be called when user cancel

                    fos.close();
                    is.close();
                    isDown=true;
                }
            } catch (MalformedURLException e) {
                isDown = true;
                SendBroadCast.sendError(context, e.getLocalizedMessage());
            } catch (IOException e) {
                isDown = true;
                SendBroadCast.sendError(context, e.getLocalizedMessage());
            }

        }

    }


}
