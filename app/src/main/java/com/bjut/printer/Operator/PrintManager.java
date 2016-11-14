package com.bjut.printer.Operator;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.bjut.printer.Consts.PublicConsts;
import com.bjut.printer.application.MyApplication;
import com.bjut.printer.bean.PrintList;
import com.bjut.printer.utils.TimeUtils;

/**
 * Created by johnsmac on 9/16/16.
 */
public class PrintManager {

    private Context context;

    private MyApplication mApplication;
    private Handler handler;
    private String target_heater_temperature_a, target_heater_temperature_b;
    private String target_bed_temperature;

    public PrintManager(Context mContext, MyApplication myApplication, Handler handler) {

        this.context = mContext;
        this.mApplication = myApplication;
        this.handler = handler;

    }

    public void BeginPrint() {
        if (mApplication.list_line == 5000) {
            mApplication.configManager.printlist.clear();
            PrintList printList = new PrintList(mApplication, context);
            printList.GetPrintList(mApplication.filepath);
            mApplication.list_line = 0;
        }
        if (mApplication.configManager.printlist.size() <= 0) {
            return;
        }
        if (mApplication.list_line >= mApplication.configManager.printlist.size()) {
            mApplication.list_line = mApplication.configManager.printlist.size() - 1;
            return;
        }
        try {

            String GCode_data = mApplication.configManager.printlist.get(mApplication.list_line);
            if (GCode_data.contains("M109") && (GCode_data.contains("T0") || GCode_data.contains("P1"))) {
                GCode_data = GCode_data.trim();
                if (GCode_data.contains(".")) {
                    target_heater_temperature_a = GCode_data.substring(GCode_data.indexOf("S") + 1,
                            GCode_data.indexOf("."));
                } else {
                    target_heater_temperature_a = GCode_data.substring(GCode_data.indexOf("S") + 1, GCode_data.length());
                }
//                text_heater_target.setText(target_heater_temperature_a);

                SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_HEATER_TARGET, target_heater_temperature_a);

            }
            if (GCode_data.contains("M109") && (GCode_data.contains("T1") || GCode_data.contains("P2"))) {
                GCode_data = GCode_data.trim();
                if (GCode_data.contains(".")) {
                    target_heater_temperature_b = GCode_data.substring(GCode_data.indexOf("S") + 1,
                            GCode_data.indexOf("."));
                } else {
                    target_heater_temperature_b = GCode_data.substring(GCode_data.indexOf("S") + 1, GCode_data.length());
                }
                if (target_heater_temperature_a.equals("0") || target_heater_temperature_a.equals("")) {

                    target_heater_temperature_a = target_heater_temperature_b;
//                    text_heater_target.setText(target_heater_temperature_b);
                    SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_HEATER_TARGET, target_heater_temperature_b);

                }
//                text_heater_target_b.setText(target_heater_temperature_b);
                SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_HEATER_TARGET_B, target_heater_temperature_b);
            }
            if (GCode_data.contains("M140 S")) {
                GCode_data = GCode_data.trim();
                if (GCode_data.contains(".")) {
                    target_bed_temperature = GCode_data.substring(GCode_data.indexOf("S") + 1, GCode_data.indexOf("."));
                } else {
                    target_bed_temperature = GCode_data.substring(GCode_data.indexOf("S") + 1, GCode_data.length());
                }

                //Pass the value Using method already have
//                text_bed_target.setText(target_bed_temperature);
                SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_BET_TARGET, target_bed_temperature);
            }
            String code = mApplication.configManager.printlist.get(mApplication.list_line);
//            ioService.writeTOSeriiaPort(code);
//            UIHandler.sendEmptyMessage(1);
            Message msg = handler.obtainMessage();
            msg.obj = code;
            msg.what = PublicConsts.BEGINPRINT_NOTE;
            handler.sendMessage(msg);

            String printLog = "发送  " + TimeUtils.getNowTime() + "  " + mApplication.configManager.printlist.get(mApplication.list_line);
            mApplication.loglist.add(printLog);
            if (mApplication.loglist.size() == 1000) {
                mApplication.loglist.clear();
            }
        } catch (Exception e) {
            SendBroadCast.sendError(context, e.getLocalizedMessage());
        }
        mApplication.list_line++;
    }


}
