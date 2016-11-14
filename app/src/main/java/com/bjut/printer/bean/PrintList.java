package com.bjut.printer.bean;

import android.content.Context;

import com.bjut.printer.Operator.SendBroadCast;
import com.bjut.printer.application.MyApplication;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by johnsmac on 9/16/16.
 */
public class PrintList {

    private MyApplication mApplication;
    private Context context;

    public PrintList(MyApplication application,Context mContext) {
        this.mApplication = application;
        this.context=mContext;
    }

    public void GetPrintList(String filepath) {

        String Lrc_data;
        boolean only = true;
        String onlyOne = "";
        int no = 0;

        File file = new File(filepath);
        if (!file.exists()) {
            return;
        }

        InputStream inputStream;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            InputStreamReader mInputStreamReader = null;
            try {
                mInputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }


            BufferedReader mBufferedReader = new BufferedReader(mInputStreamReader);
            int current_line = -1;
            while ((Lrc_data = mBufferedReader.readLine()) != null) {
                current_line++;
                if (current_line == mApplication.get_line) {
                    if (!Lrc_data.trim().equals("")) {
                        if (!Lrc_data.trim().subSequence(0, 1).equals(";")) {
                            if (Lrc_data.contains(";")) {
                                Lrc_data = Lrc_data.substring(0, Lrc_data.indexOf(";"));
                            }
                            Lrc_data = Lrc_data.trim() + "\n";
                            if (mApplication.get_line > 20) {
                                if (only) {
                                    onlyOne = onlyOne + Lrc_data;
                                    no++;
                                    if (no == 3) {
                                        only = false;
                                        mApplication.configManager.printlist.add(onlyOne);
                                    }
                                } else {
                                    if (mApplication.configManager.printlist.size() >= 5000) {
                                        break;
                                    } else {
                                        if (mApplication.configManager.printlist.size() % 500 == 0) {
                                            mApplication.configManager.printlist.add("M105\n");
                                        }
                                        mApplication.configManager.printlist.add(Lrc_data);
                                    }
                                }
                            } else {
                                if (mApplication.configManager.printlist.size() == 0) {
                                    mApplication.configManager.printlist.add("M105\n");
                                }
                                mApplication.configManager.printlist.add(Lrc_data);
                            }
                        }
                    }
                    mApplication.get_line++;
                }
            }
            inputStream.close();
            mBufferedReader.close();
            mInputStreamReader.close();
        } catch (IOException e) {

            SendBroadCast.sendError(context,"failed when getting print List");
        }


    }

}
