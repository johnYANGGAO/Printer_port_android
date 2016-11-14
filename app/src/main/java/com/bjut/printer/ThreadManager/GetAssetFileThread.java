package com.bjut.printer.ThreadManager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.bjut.printer.Consts.PublicConsts;
import com.bjut.printer.Operator.SendBroadCast;
import com.bjut.printer.application.MyApplication;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by johnsmac on 9/16/16.
 */
public class GetAssetFileThread extends Thread {

    private Handler handler;
    private MyApplication myApplication;
    private Context context;
    private String filepath;

    public GetAssetFileThread(Context mContext, Handler mHandler, MyApplication application, String filePath) {

        this.handler = mHandler;
        this.myApplication = application;
        this.context = mContext;
        this.filepath = filePath;
    }


    @Override
    public void run() {
        BufferedReader mBufferedReader;
        InputStream inputStream;
        InputStreamReader mInputStreamReader;

        try {

            int gCodeLength = 0;
            File file = new File(filepath);
            if (!file.exists()) {
                return;
            }

            inputStream = new BufferedInputStream(new FileInputStream(file));
            mInputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            mBufferedReader = new BufferedReader(mInputStreamReader);
            String fileData;

            while ((fileData = mBufferedReader.readLine()) != null) {
                if (!fileData.trim().equals("")) {
                    if (!fileData.trim().subSequence(0, 1).equals(";")) {
                        if (fileData.contains(";")) {
                            fileData = fileData.substring(0, fileData.indexOf(";"));
                        }
                        fileData = fileData.trim();
                        gCodeLength++;
                    }
                }
            }
            if (mBufferedReader != null) {
                mBufferedReader.close();
            }
            if (mInputStreamReader != null) {
                mInputStreamReader.close();
            }
            if (inputStream != null) {
                inputStream.close();

            }
            if (gCodeLength <= 50) {
                //11
                handler.sendEmptyMessage(PublicConsts.GCODE_LENGTH_SHORT);
                return;
            }

            int add_i = gCodeLength / 500;
            myApplication.configManager.gcodelength = gCodeLength + add_i - 2;
            Message message = handler.obtainMessage();
            message.arg1 = (gCodeLength + add_i - 2);
            //5
            message.what = PublicConsts.GCODE_LENGTH;
            handler.sendMessage(message);

        } catch (Exception e) {

            SendBroadCast.sendError(context, e.getLocalizedMessage());
        }
    }
}
