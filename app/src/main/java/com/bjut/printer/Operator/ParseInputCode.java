package com.bjut.printer.Operator;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.Log;

import com.bjut.printer.Consts.PublicConsts;
import com.bjut.printer.application.MyApplication;
import com.bjut.printer.bean.WorkLog;
import com.bjut.printer.services.IOService;
import com.bjut.printer.utils.ProcessCharacterUtil;
import com.bjut.printer.utils.TimeUtils;

import java.io.IOException;
import java.io.OutputStream;


/**
 * Created by johnsmac on 9/11/16.
 */
public class ParseInputCode {

    private MyApplication mApplication;
    private SharedPreferences.Editor shareData;
    private IOService context;


    public ParseInputCode(IOService mContext, MyApplication application, SharedPreferences.Editor mShareData) {

        this.mApplication = application;
        this.shareData = mShareData;
        this.context = mContext;

    }

    public boolean saveArray(MyApplication mApplication) {
        shareData.putInt("Status_size", mApplication.configManager.printlist.size());
        for (int i = 0; i < mApplication.configManager.printlist.size(); i++) {
            shareData.remove("Status_" + i); //NO NEED ?
            shareData.putString("Status_" + i, mApplication.configManager.printlist.get(i));
        }
        return shareData.commit();
    }

    public void ProcessReceived(String result) {
        if (result == null || result.trim().equals(""))
            return;
        String printLog = "接收  " + TimeUtils.getNowTime() + "  " + result + "\r\n";
        mApplication.loglist.add(printLog);
        if (mApplication.loglist.size() == 1000) {
            mApplication.loglist.clear();
        }
        Log.w("parseResult :", result);


        /**
         *
         * ADD ONE
         * */
        if (result.contains("BLOCKING_MATERIAL")) {
             mApplication.fromFaults=true;
//            savePresentPrintInfo();
//            Pause();
            SendBroadCast.sendModifyUI(context, PublicConsts.PAUSE_PRINT, null);
            mApplication.configManager.printlist.clear();
            //Alert
            SendBroadCast.sendWarning(context, PublicConsts.WARNING_ACTION, "发生堵料，请检查挤出机及喷头是否正常!");
            SendBroadCast.sendModifyUI(context, PublicConsts.MECHANICAL_STATUS, "发生堵料故障，打印已暂停");
//            text_filename.setText("发生堵料故障，打印已暂停");send handler
            mApplication.BlockMaterialFaults = true;
        }

        if (result.contains("POWER_ERROR")) {
            /**
             *
             * ADD 2
             * */
            mApplication.fromFaults=true;
            SendBroadCast.sendModifyUI(context, PublicConsts.TURN_FAN_OFF, null);
            savePresentPrintInfo();
//            Pause();
            SendBroadCast.sendModifyUI(context, PublicConsts.PAUSE_PRINT, null);
            mApplication.configManager.printlist.clear();
            //Alert
            SendBroadCast.sendWarning(context, PublicConsts.WARNING_ACTION, "检测到断电,启用备用电源,已保持当前状态。请在来电后,开始或继续打印!");

            SendBroadCast.sendModifyUI(context, PublicConsts.MECHANICAL_STATUS, "电源已断开，无法继续打印");
//            text_filename.setText("电源已断开，无法继续打印");
            mApplication.hasPowerFaults = true;
        }
        if (result.contains("POWER_RECOVERY")) {
            mApplication.hasPowerFaults = false;
            mApplication.fromFaults=false;
            SendBroadCast.sendModifyUI(context, PublicConsts.TURN_FAN_ON, null);
            getPreviousPrintInfo();
//            continuetoPrint();
            SendBroadCast.sendWarning(context, PublicConsts.WARNING_CONTINUE_PRINT_ACTION, "供电已正常");
//            text_filename.setText("电源已恢复，继续打印");
            SendBroadCast.sendModifyUI(context, PublicConsts.MECHANICAL_STATUS, "电源已恢复");
        }

        /**
         * add 3
         * */
        if (result.contains("LEVELING_HIGH")) {

            SendBroadCast.sendWarning(context, PublicConsts.ACTION_LEVELING_HIGH,
                    "平台过高，请旋转螺母降低平台，然后重新选取测试点");

        }
        if (result.contains("LEVELING_OK")) {

            SendBroadCast.sendWarning(context, PublicConsts.ACTION_LEVELING_OK,
                    "该点已经调整完毕");
        }
        if (result.contains("LEVELING_NOTHING")) {

            SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_LEVELING_NOTHING, null);
        }

        if (result.contains("LEVELING_LOW")) {

            SendBroadCast.sendWarning(context, PublicConsts.ACTION_LEVELING_LOW,
                    "平台过低，请旋转螺母提高平台，然后重新选取测试点");
        }


        if (result.contains("SUPPLY_OVER")) {
            mApplication.fromFaults=true;
//            savePresentPrintInfo();
//            Pause();
            SendBroadCast.sendModifyUI(context, PublicConsts.PAUSE_PRINT, null);
            mApplication.configManager.printlist.clear();
            //  text_filename.setText("耗材用尽，请及时更换耗材");
            SendBroadCast.sendModifyUI(context, PublicConsts.MECHANICAL_STATUS, "耗材用尽，请及时更换耗材");
            //Alert
            SendBroadCast.sendWarning(context, PublicConsts.WARNING_ACTION, "检测到断料，请检查耗材");
            mApplication.hasSupplyFaults = true;

        }
        if (result.contains("SUPPLY_READY")) {
            mApplication.fromFaults=false;
            mApplication.hasSupplyFaults = false;
            getPreviousPrintInfo();
//            text_filename.setText("已完成耗材更换");
            SendBroadCast.sendModifyUI(context, PublicConsts.MECHANICAL_STATUS, "已完成耗材更换");
//            continuetoPrint();
            SendBroadCast.sendWarning(context, PublicConsts.WARNING_CONTINUE_PRINT_ACTION, "耗材已恢复,请开始或继续打印");

        }
        if (result.contains("ok OPENED ok")) {
//            gatestatus = false;
            SendBroadCast.sendGateOption(context, PublicConsts.GATE_STATUS, false);

        }
        if (result.contains("ok CLOSED ok")) {
//            gatestatus = true;
            mApplication.closedDoor=true;
            SendBroadCast.sendGateOption(context, PublicConsts.GATE_STATUS, true);
        }
        if (result.contains("SAFE_GATE OPEN")) {
//            gatestatus = false;
            SendBroadCast.sendGateOption(context, PublicConsts.GATE_STATUS, false);
        }
        if (result.contains("SAFE_GATE CLOSE")) {
//            gatestatus = true;
            mApplication.closedDoor=true;
            SendBroadCast.sendGateOption(context, PublicConsts.GATE_STATUS, true);
        }
        if (result.contains("SUPPLY_OVER")) {
//            myHandler.sendEmptyMessage(7); [PressPlay()]
            SendBroadCast.sendGateOption(context, PublicConsts.GATE_PLAY, null);
        }
        if (result.contains("T0:") && result.contains("T1:") && result.contains("B:") && result.contains("@:")
                && result.contains("B@:")) {
            Log.e("tag", result);

            if (result.contains("T0:") && result.contains("T1:")) {
                result = result.replaceAll(" ", "").replaceAll(":", "/").replaceAll("B", "").replaceAll("T0", " ").replaceAll("T1", " ");
                result = result.replaceAll(" ", "");
                result = result.replaceAll("@", "");
                String[] temp = result.split("\\/");

                if (temp.length == 9) {
                    if (ProcessCharacterUtil.isNumber(temp[3].trim())) {
                        mApplication.configManager.heater_current_a = temp[3].trim();
                    }
                    if (ProcessCharacterUtil.isNumber(temp[4].trim())) {
                        mApplication.heater_dest_a = temp[4].trim();
                    }


                    if (ProcessCharacterUtil.isNumber(temp[1].trim())) {
                        mApplication.bed_current = temp[1].trim();
                    }
                    if (ProcessCharacterUtil.isNumber(temp[2].trim())) {
                        mApplication.bed_dest = temp[2].trim();
                    }
                    if (ProcessCharacterUtil.isNumber(temp[5].trim())) {
                        mApplication.configManager.heater_current_b = temp[5].trim();
//                        text_heater_current_b.setText(mApplication.configManager.heater_current_b);
                        SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_HEATER_CURRENT_B, mApplication.configManager.heater_current_b);
                    }
                    if (ProcessCharacterUtil.isNumber(temp[6].trim())) {
                        mApplication.heater_dest_b = temp[6].trim();
//                        text_heater_target_b.setText(mApplication.heater_dest_b);
                        SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_HEATER_TARGET_B, mApplication.heater_dest_b);
                    }

//                    myHandler.sendEmptyMessage(10);
                    SendBroadCast.sendModifyUI(context, PublicConsts.PROGRESS_LABEL, null);

                }


            }


        }

        if (result.contains("T:") && result.contains("E:") && result.contains("W:")) {
            String tempresult = result.replaceAll("E", "");
            String[] temp = tempresult.split("\\:");
            if (result.contains("E:0")) {
                if (temp.length > 1) {
                    String num = temp[1].trim();
                    boolean isnum = ProcessCharacterUtil.isNumber(num);
                    if (isnum) {

                        mApplication.configManager.heater_current_a = num;

                        SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_HEATER_CURRENT_PROGRESS, mApplication.configManager.heater_current_a);
//                        float heater_temperature_f = Float.valueOf(mApplication.configManager.heater_current_a);
//                        int heater_temperature_i = (int) heater_temperature_f;
//                        progress_heater.setProgress(heater_temperature_i);
//                        text_heater_current.setText(mApplication.configManager.heater_current_a); //question

                    }
                } else {


                }
            }
            if (result.contains("E:1")) {
                if (temp.length > 1) {
                    boolean isnum = ProcessCharacterUtil.isNumber(temp[1].trim());
                    if (isnum) {
                        mApplication.configManager.heater_current_b = temp[1].trim();
//                        text_heater_current_b.setText(mApplication.configManager.heater_current_b); //question
                        SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_HEATER_CURRENT_PROGRESS_B, mApplication.configManager.heater_current_b);
//                        float heater_temperature_f = Float.valueOf(mApplication.configManager.heater_current_b);
//                        int heater_temperature_i = (int) heater_temperature_f;
//                        progress_heater_b.setProgress(heater_temperature_i);
                    }


                }
            }


        }

        if (mApplication.playstatus) {
            if (result.contains("SAFE_GATE OPEN") || result.contains("SAFE_GATE CLOSE")) {
//                myHandler.sendEmptyMessage(7);
                SendBroadCast.sendGateOption(context, PublicConsts.GATE_PLAY, null);
            }

            if (mApplication.workloglist.size() > 0) {
                if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 8) {
//                    text_heater_target.setText(target_heater_temperature_a);
//                    text_heater_target_b.setText(target_heater_temperature_b);

                    SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_MULTIPLE_HEATER_TARGET_TEXT,
                            mApplication.target_heater_temperature_a + "," + mApplication.target_heater_temperature_b);


                } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 9) {
//                    text_bed_target.setText(target_bed_temperature);

                    SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_BET_TARGET, mApplication.target_bed_temperature);

                } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 10) {
//                    text_speed.setText(speed + "%");
                    SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_TEXT_SPEED, mApplication.speed);
                }
            }

            if (result.contains("ok")) {
                String[] temp1 = result.split("\\\n");
                if (temp1.length > 1) {
                    for (int i = 0; i < temp1.length - 1; i++) {
//                        myHandler.sendEmptyMessage(4);
                        SendBroadCast.sendModifyUI(context, PublicConsts.PARSE_TEMP, null);
                    }
                }
                if (mApplication.pausestatus && !mApplication.pauseplace) {
                    result = result.replaceAll(" ", "").replaceAll("Y", "").replaceAll("Z", "").replaceAll("E", "")
                            .replaceAll("C", ":");
                    String[] temp = result.split("\\:");
                    Log.e("tag", result);
                    if (temp.length > 4) {
                        if (ProcessCharacterUtil.isNumber(temp[1].trim())) {
                            mApplication.pause_x = temp[1].trim();
                        }


                        if (ProcessCharacterUtil.isNumber(temp[2].trim())) {
                            mApplication.pause_y = temp[2].trim();
                        }
                        if (ProcessCharacterUtil.isNumber(temp[3].trim())) {
                            mApplication.pause_z = temp[3].trim();
                        }
                        if (ProcessCharacterUtil.isNumber(temp[4].trim())) {
                            mApplication.pause_e = temp[4].trim();
                        }

                        boolean isnum = ProcessCharacterUtil.isNumber(mApplication.pause_e);
                        if (isnum) {
                            mApplication.current_e = Float.valueOf(mApplication.pause_e);
                        } else {
                            String num = "";
                            if (mApplication.pause_e != null && !mApplication.pause_e.trim().equals("")) {
                                for (int i = 0; i < mApplication.pause_e.length(); i++) {
                                    if (!Character.isDigit(mApplication.pause_e.charAt(i))) {

                                        break;
                                    } else {
                                        num += mApplication.pause_e.charAt(i);
                                    }
                                }
                                isnum = ProcessCharacterUtil.isNumber(num);
                                if (isnum)
                                    mApplication.current_e = Float.valueOf(num);
                            }

                        }
                        if (ProcessCharacterUtil.isNumber(mApplication.pause_z)) {
                            float target_z_f = Float.valueOf(mApplication.pause_z) + (float) 20;
                            mApplication.pause_target_z = String.valueOf(target_z_f);

                        }
                        if (ProcessCharacterUtil.isNumber(mApplication.pause_e)) {
                            float target_e_f = Float.valueOf(mApplication.pause_e) - (float) 6;
                            String pause_target_e = String.valueOf(target_e_f);
                            String G1 = "G1 F1500 Z" + mApplication.pause_target_z + " E" + pause_target_e + "\n";
                            String GE = "G1 E" + mApplication.pause_e;
                            try {
//                                context.writeTOSerialPort(G1);
//                                context.writeTOSerialPort("G28 XY\n");
//                                context.writeTOSerialPort(GE);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }


                        mApplication.pauseplace = true;
                    }
                } else if (mApplication.pausestatus && mApplication.pauseplace) {
//                    text_x.setText("0.0");
//                    text_y.setText("0.0");
//                    text_z.setText(pause_target_z);

                    SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_TEXT_XYZ,
                            "0.0" + "," + "0.0" + "," +
                                    mApplication.pause_target_z
                    );


                    mApplication.pauseplace = false;
                } else if (mApplication.goonstatus) {
                    mApplication.goonstatus = false;
                    mApplication.configManager.gcodelength = mApplication.configManager.gcodelength - 2;

                    SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_BEGIN_PRINT, null);
//                    BeginPrint();
//                    WriteLog();
                } else {
//                    myHandler.sendEmptyMessage(4);
                    SendBroadCast.sendModifyUI(context, PublicConsts.PARSE_TEMP, null);
                }
            }
        } else {
            if (result.contains("X:") && result.contains("Y:") && result.contains("Z:") && result.contains("E:")
                    && result.contains("Count")) {
                result = result.replaceAll(" ", "").replaceAll("Y", "").replaceAll("Z", "").replaceAll("E", "")
                        .replaceAll("C", ":");
                String[] temp = result.split("\\:");
                if (temp.length == 8) {

                    if (ProcessCharacterUtil.isNumber(temp[1].trim())) {
                        mApplication.current_x = temp[1].trim();
//                        text_x.setText(mApplication.current_x);
                        SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_TEXT_X, mApplication.current_x);
                    }
                    if (ProcessCharacterUtil.isNumber(temp[2].trim())) {
                        mApplication.current_y = temp[2].trim();
//                        text_y.setText(mApplication.current_y);
                        SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_TEXT_Y, mApplication.current_y);
                    }
                    if (ProcessCharacterUtil.isNumber(temp[3].trim())) {
                        mApplication.current_z = temp[3].trim();
//                        text_z.setText(mApplication.current_z);
                        SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_TEXT_Z, mApplication.current_z);

                    }

                }
            }
            if (result.length() > 1) {
                if (result.substring(0, 2).equals("ok")) {
                    if (mApplication.workloglist.size() == 0) {
                        return;
                    }
                    ReceiveCommand(result);
                    if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 1) {
                        mApplication.current_x = mApplication.workloglist.get(mApplication.worklog_no).getValue();
//                        text_x.setText(mApplication.current_x);
                        SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_TEXT_X, mApplication.current_x);
                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 2) {
                        mApplication.current_y = mApplication.workloglist.get(mApplication.worklog_no).getValue();
//                        text_y.setText(mApplication.current_y);
                        SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_TEXT_Y, mApplication.current_y);
                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 3) {
                        mApplication.current_z = mApplication.workloglist.get(mApplication.worklog_no).getValue();
//                        text_z.setText(mApplication.current_z);
                        SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_TEXT_Z, mApplication.current_z);
                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 4) {
                        mApplication.current_x = mApplication.workloglist.get(mApplication.worklog_no).getValuex();
//                        text_x.setText(mApplication.current_x);
                        mApplication.current_y = mApplication.workloglist.get(mApplication.worklog_no).getValuey();
//                        text_y.setText(mApplication.current_y);
                        mApplication.current_z = mApplication.workloglist.get(mApplication.worklog_no).getValuez();
//                        text_z.setText(mApplication.current_z);

                        SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_TEXT_XYZ,
                                mApplication.current_x + "," + mApplication.current_y + "," +
                                        mApplication.current_z);


                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 5) {
                        if (mApplication.initial == 1) {
                            mApplication.initial = 0;
                            SendCommand("M114\n");
                            WorkLog worklog1 = new WorkLog();
                            mApplication.worklog_no++;
                            worklog1.setId(mApplication.worklog_no);
                            worklog1.setSenddata("M114\n");
                            worklog1.setType(6);
                            mApplication.workloglist.add(worklog1);
                        }
                        result = result.replaceAll(" ", "").replaceAll(":", "/").replaceAll("B", "");
                        String[] temp = result.split("\\/");
                        String heater_current = temp[1];
                        String bed_current = temp[3];

                        if (ProcessCharacterUtil.isNumber(heater_current) && ProcessCharacterUtil.isNumber(bed_current)) {


//                            text_heater_current.setText(heater_current);
//                            text_heater_current_b.setText(heater_current);///question
//                            text_bed_current.setText(bed_current);
//
//                            float heater_temperature_f = Float.parseFloat(heater_current);
//                            int heater_temperature_i = (int) heater_temperature_f;
//                            progress_heater.setProgress(heater_temperature_i);
//                            progress_heater_b.setProgress(heater_temperature_i); ///question

//                            float bed_temperature_f = Float.parseFloat(bed_current);
//                            int bed_temperature_i = (int) bed_temperature_f;
//                            progress_bed.setProgress(bed_temperature_i);

                            SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_BUNDLE_DISPLAY,
                                    heater_current + "," + heater_current + "," + bed_current + "," +
                                            heater_current + "," + heater_current + "," + bed_current);


                        }


                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 6) {
                        result = result.replaceAll(" ", "").replaceAll("Y", "").replaceAll("Z", "").replaceAll("E", "")
                                .replaceAll("C", ":");
                        String[] temp = result.split("\\:");
                        if (temp.length > 1) {
                            if (ProcessCharacterUtil.isNumber(temp[1].trim())) {
                                mApplication.current_x = temp[1].trim();
//                                text_x.setText(mApplication.current_x);
                                SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_TEXT_X, mApplication.current_x);
                            }
                            if (ProcessCharacterUtil.isNumber(temp[2].trim())) {
                                mApplication.current_y = temp[2].trim();
//                                text_y.setText(mApplication.current_y);
                                SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_TEXT_Y, mApplication.current_y);
                            }
                            if (ProcessCharacterUtil.isNumber(temp[3].trim())) {
                                mApplication.current_z = temp[3].trim();
//                                text_z.setText(mApplication.current_z);
                                SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_TEXT_Z, mApplication.current_z);
                            }


                        }
                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 7) {
                        if (ProcessCharacterUtil.isNumber(mApplication.workloglist.get(mApplication.worklog_no)
                                .getValue())) {
                            mApplication.current_e = Float.valueOf(mApplication.workloglist.get(mApplication.worklog_no)
                                    .getValue());
                        }

                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 10) {
//                        text_speed.setText(mApplication.speed + "%");

                        SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_TEXT_SPEED, mApplication.speed);

                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 11) {
                        String M140 = "M140 S" + mApplication.target_bed_temperature + "\n";
                        SendCommand(M140);
                        WorkLog worklog = new WorkLog();
                        mApplication.worklog_no++;
                        worklog.setId(mApplication.worklog_no);
                        worklog.setSenddata(M140);
                        mApplication.workloglist.add(worklog);

//                        text_bed_target.setText(target_bed_temperature);

                        SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_BET_TARGET, mApplication.target_bed_temperature);

                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 13) {

                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 15) {
                        result = result.replaceAll(" ", "").replaceAll("Y", "").replaceAll("Z", "").replaceAll("E", "")
                                .replaceAll("C", ":");
                        String[] temp = result.split("\\:");
                        if (temp.length > 3) {
                            if (ProcessCharacterUtil.isNumber(temp[1].trim())) {
                                mApplication.current_x = temp[1].trim();
//                                text_x.setText(mApplication.current_x);
                                SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_TEXT_X, mApplication.current_x);
                            }
                            if (ProcessCharacterUtil.isNumber(temp[2].trim())) {
                                mApplication.current_y = temp[2].trim();
//                                text_y.setText(mApplication.current_y);
                                SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_TEXT_Y, mApplication.current_y);
                            }

                            String tempstr = temp[3].trim();
                            boolean isnum = ProcessCharacterUtil.isNumber(tempstr);
                            if (isnum) {
                                mApplication.current_z = tempstr;
                            } else {
                                String num = "";
                                for (int i = 0; i < tempstr.length(); i++) {
                                    if (!Character.isDigit(tempstr.charAt(i))) {

                                        break;
                                    } else {
                                        num += tempstr.charAt(i);
                                    }
                                }
                                isnum = ProcessCharacterUtil.isNumber(num);
                                if (isnum)
                                    mApplication.current_z = num;

                            }


                            if (ProcessCharacterUtil.isNumber(mApplication.current_z)) {
//                                text_z.setText(mApplication.current_z);
                                SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_TEXT_Z, mApplication.current_z);
                                float target_z_f = Float.valueOf(mApplication.current_z) + (float) 50;
                                String target_z = String.valueOf(target_z_f);
                                String G1Z = "G1 Z" + target_z + "\n";
                                SendCommand(G1Z);
                                WorkLog worklog = new WorkLog();
                                mApplication.worklog_no++;
                                worklog.setId(mApplication.worklog_no);
                                worklog.setSenddata(G1Z);
                                worklog.setType(3);
                                worklog.setValue(target_z);
                                mApplication.workloglist.add(worklog);
                            }

                            //mApplication.current_z = temp[3];

                        }

                        SendBroadCast.sendModifyUI(context, PublicConsts.MODIFY_DISPLAY_ACTION, null);

                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 16) {
                        result = result.replaceAll(" ", "").replaceAll("Y", "").replaceAll("Z", "").replaceAll("E", "")
                                .replaceAll("C", ":");
                        String[] temp = result.split("\\:");
                        if (temp.length > 1) {
                            if (ProcessCharacterUtil.isNumber(temp[1].trim())) {
                                mApplication.current_x = temp[1].trim();
//                                text_x.setText(mApplication.current_x);
                                SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_TEXT_X, mApplication.current_x);
                            }
                            if (ProcessCharacterUtil.isNumber(temp[2].trim())) {
                                mApplication.current_y = temp[2].trim();
//                                text_y.setText(mApplication.current_y);
                                SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_TEXT_Y, mApplication.current_y);
                            }
                            if (ProcessCharacterUtil.isNumber(temp[3].trim())) {
                                mApplication.current_z = temp[3].trim();
//                                text_z.setText(mApplication.current_z);
                                SendBroadCast.sendModifyUI(context, PublicConsts.ACTION_TEXT_Z, mApplication.current_z);
                                float target_z_f = Float.valueOf(mApplication.current_z) + (float) 20;
                                String target_z = String.valueOf(target_z_f);
                                String G1Z = "G1 Z" + target_z + "\n";
                                SendCommand(G1Z);
                                WorkLog worklog = new WorkLog();
                                mApplication.worklog_no++;
                                worklog.setId(mApplication.worklog_no);
                                worklog.setSenddata(G1Z);
                                worklog.setType(3);
                                worklog.setValue(target_z);
                                mApplication.workloglist.add(worklog);
                            }

                        }
                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 21) {
                        mApplication.settinglist.get(0).setValue(
                                mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.putString("retract_length", mApplication.workloglist.get(mApplication.worklog_no)
                                .getValue());
                        shareData.commit();
                        mApplication.mSettingFragment.myadapter.notifyDataSetChanged();
                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 22) {
                        mApplication.settinglist.get(1).setValue(
                                mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.putString("retract_feedrate", mApplication.workloglist.get(mApplication.worklog_no)
                                .getValue());
                        shareData.commit();
                        mApplication.mSettingFragment.myadapter.notifyDataSetChanged();
                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 23) {
                        mApplication.settinglist.get(2).setValue(
                                mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.putString("Z_lift", mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.commit();
                        mApplication.mSettingFragment.myadapter.notifyDataSetChanged();
                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 24) {
                        mApplication.settinglist.get(3).setValue(
                                mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.putString("accel_retract", mApplication.workloglist.get(mApplication.worklog_no)
                                .getValue());
                        shareData.commit();
                        mApplication.mSettingFragment.myadapter.notifyDataSetChanged();
                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 26) {
                        mApplication.settinglist.get(5).setValue(
                                mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.putString("extruder_test_feedrate",
                                mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.commit();
                        mApplication.mSettingFragment.myadapter.notifyDataSetChanged();
                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 27) {
                        mApplication.settinglist.get(6).setValue(
                                mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.putString("pid_p", mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.commit();
                        mApplication.mSettingFragment.myadapter.notifyDataSetChanged();
                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 28) {
                        mApplication.settinglist.get(7).setValue(
                                mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.putString("pid_i", mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.commit();
                        mApplication.mSettingFragment.myadapter.notifyDataSetChanged();
                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 29) {
                        mApplication.settinglist.get(8).setValue(
                                mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.putString("pid_d", mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.commit();
                        mApplication.mSettingFragment.myadapter.notifyDataSetChanged();
                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 30) {
                        mApplication.settinglist.get(9).setValue(
                                mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.putString("flow", mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.commit();
                        mApplication.mSettingFragment.myadapter.notifyDataSetChanged();
                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 31) {
                        mApplication.settinglist.get(10).setValue(
                                mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.putString("accel", mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.commit();
                        mApplication.mSettingFragment.myadapter.notifyDataSetChanged();
                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 32) {
                        mApplication.settinglist.get(11).setValue(
                                mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.putString("v_x", mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.commit();
                        mApplication.mSettingFragment.myadapter.notifyDataSetChanged();
                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 33) {
                        mApplication.settinglist.get(12).setValue(
                                mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.putString("v_y", mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.commit();
                        mApplication.mSettingFragment.myadapter.notifyDataSetChanged();
                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 34) {
                        mApplication.settinglist.get(13).setValue(
                                mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.putString("v_z", mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.commit();
                        mApplication.mSettingFragment.myadapter.notifyDataSetChanged();
                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 35) {
                        mApplication.settinglist.get(14).setValue(
                                mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.putString("v_e", mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.commit();
                        mApplication.mSettingFragment.myadapter.notifyDataSetChanged();
                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 36) {
                        mApplication.settinglist.get(15).setValue(
                                mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.putString("a_x", mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.commit();
                        mApplication.mSettingFragment.myadapter.notifyDataSetChanged();
                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 37) {
                        mApplication.settinglist.get(16).setValue(
                                mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.putString("a_y", mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.commit();
                        mApplication.mSettingFragment.myadapter.notifyDataSetChanged();
                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 38) {
                        mApplication.settinglist.get(17).setValue(
                                mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.putString("a_z", mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.commit();
                        mApplication.mSettingFragment.myadapter.notifyDataSetChanged();
                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 39) {
                        mApplication.settinglist.get(18).setValue(
                                mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.putString("a_e", mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.commit();
                        mApplication.mSettingFragment.myadapter.notifyDataSetChanged();
                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 40) {
                        mApplication.settinglist.get(19).setValue(
                                mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData
                                .putString("steps_x", mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.commit();
                        mApplication.mSettingFragment.myadapter.notifyDataSetChanged();
                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 41) {
                        mApplication.settinglist.get(20).setValue(
                                mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData
                                .putString("steps_y", mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.commit();
                        mApplication.mSettingFragment.myadapter.notifyDataSetChanged();
                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 42) {
                        mApplication.settinglist.get(21).setValue(
                                mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData
                                .putString("steps_z", mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.commit();
                        mApplication.mSettingFragment.myadapter.notifyDataSetChanged();
                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 43) {
                        mApplication.settinglist.get(22).setValue(
                                mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData
                                .putString("steps_e", mApplication.workloglist.get(mApplication.worklog_no).getValue());
                        shareData.commit();
                        mApplication.mSettingFragment.myadapter.notifyDataSetChanged();
                    } else if (mApplication.workloglist.get(mApplication.worklog_no).getType() == 44) {
                        mApplication.current_e_b = Float.valueOf(mApplication.workloglist.get(mApplication.worklog_no)
                                .getValue());
                    }
                }
            }
        }
    }


    private void SendCommand(final String message) {
        try {

            context.writeTOSerialPort(message);


        } catch (Exception e) {
            e.printStackTrace();
        }
        String printlog = "发送  " + message;
        mApplication.loglist.add(printlog);
        if (mApplication.loglist.size() == 1000) {
            mApplication.loglist.clear();
        }
    }

    private void ReceiveCommand(String message) {
        mApplication.workloglist.get(mApplication.worklog_no).setBackdata(message);
        mApplication.workloglist.get(mApplication.worklog_no).setSenddata(TimeUtils.getNowTime());
    }

    private void savePresentPrintInfo() {

        shareData.putInt("current_print", mApplication.configManager.current_print);
        shareData.putInt("gcodelength", mApplication.configManager.gcodelength);
        shareData.putString("unfilename", mApplication.configManager.unfilename);
        shareData.putString("unfilepath", mApplication.configManager.unfilepath);
        shareData.putString("percent", mApplication.configManager.percent);
        shareData.putString("heater_current_a", mApplication.configManager.heater_current_a);
        shareData.putString("heater_current_b", mApplication.configManager.heater_current_b);
        shareData.putInt("list_line", mApplication.list_line);
        saveArray(mApplication);
        shareData.commit();

    }


    private void getPreviousPrintInfo() {

        SharedPreferences sharedataRead = context.getSharedPreferences("data", 0);

        mApplication.list_line = sharedataRead.getInt("list_line", mApplication.list_line);
        mApplication.configManager.current_print = sharedataRead.getInt("current_print", mApplication.configManager.current_print);
        mApplication.configManager.gcodelength = sharedataRead.getInt("gcodelength", mApplication.configManager.gcodelength);
        mApplication.configManager.unfilename = sharedataRead.getString("unfilename", mApplication.configManager.unfilename);
        mApplication.configManager.unfilepath = sharedataRead.getString("unfilepath", mApplication.configManager.unfilepath);
        mApplication.configManager.percent = sharedataRead.getString("percent", mApplication.configManager.percent);
        mApplication.configManager.heater_current_a = sharedataRead.getString("heater_current_a", mApplication.configManager.heater_current_a);
        mApplication.configManager.heater_current_b = sharedataRead.getString("heater_current_b", mApplication.configManager.heater_current_b);


    }


}
