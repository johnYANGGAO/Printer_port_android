package com.bjut.printer.BroadCast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.bjut.printer.Consts.PublicConsts;
import com.bjut.printer.Operator.SaveEnergy;

/**
 * Created by johnsmac on 9/14/16.
 * <p/>
 * <p/>
 * here's receiver that showed in main act
 */

public class MechanicalStatusReceiver extends BroadcastReceiver {

    private Handler handler;//from activity

    public MechanicalStatusReceiver(Handler mHandler) {
        this.handler = mHandler;
    }

    public MechanicalStatusReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (PublicConsts.START_SERVERSOCKETSERVICE_ACTION.equals(action)) {
            handler.sendEmptyMessage(PublicConsts.START_SOCKETSERVICE);

        } else if (PublicConsts.MECHANICAL_STATUS.equals(action)) {
            // for text_filename
            modifyText(PublicConsts.MECHANICAL_STATUS,
                    PublicConsts.MECHANICAL_STATUS_MSG_WHAT,
                    intent);

        } else if (PublicConsts.GATE_STATUS.equals(action)) {

            Message msg = handler.obtainMessage();
            msg.what = PublicConsts.GATE_STATUS_WHAT;
            msg.obj = intent.getBooleanExtra(PublicConsts.GATE_STATUS, false);
            handler.sendMessage(msg);

        } else if (PublicConsts.GATE_PLAY.equals(action)) {

            Message msg = handler.obtainMessage();
            msg.what = PublicConsts.GATE_PLAY_WHAT;
            handler.sendMessage(msg);

        } else if (PublicConsts.PROGRESS_LABEL.equals(action)) {

            Message msg = handler.obtainMessage();
            msg.what = PublicConsts.PROGRESS_LABEL_WHAT;
            handler.sendMessage(msg);

        } else if (PublicConsts.ACTION_HEATER_CURRENT_B.equals(action)) {
            //for text_heater_current_b

            modifyText(PublicConsts.ACTION_HEATER_CURRENT_B,
                    PublicConsts.HEATER_CURRENT_B_WHAT,
                    intent);

        } else if (PublicConsts.ACTION_HEATER_TARGET_B.equals(action)) {

            //for text_heater_target_b
            modifyText(PublicConsts.ACTION_HEATER_TARGET_B,
                    PublicConsts.HEATER_TARGET_B_WHAT,
                    intent);

        } else if (PublicConsts.ACTION_HEATER_TARGET.equals(action)) {
            //for text_heater_target
            modifyText(PublicConsts.ACTION_HEATER_TARGET,
                    PublicConsts.HEATER_TARGET_WHAT,
                    intent);

        } else if (PublicConsts.ACTION_TEXT_SPEED.equals(action)) {
            //for text_speed
            modifyText(PublicConsts.ACTION_TEXT_SPEED, PublicConsts.TEXT_SPEED_WHAT, intent);

        } else if (PublicConsts.ACTION_BET_TARGET.equals(action)) {
            //for text_bed_target
            modifyText(PublicConsts.ACTION_BET_TARGET, PublicConsts.BET_TARGET_WHAT, intent);

        } else if (PublicConsts.ACTION_MULTIPLE_HEATER_TARGET_TEXT.equals(action)) {
            //for text_heater_target and text_heater_target_b  ACTIVITY HANDLE THESE MSG AND PARSE
            modifyText(PublicConsts.ACTION_MULTIPLE_HEATER_TARGET_TEXT, PublicConsts.HEATER_TARGET_ALL_WHAT, intent);

        } else if (PublicConsts.ACTION_HEATER_CURRENT_PROGRESS.equals(action)) {
            //for text_heater_current and progress_heater ,In fact, both values are the same.

            modifyText(PublicConsts.ACTION_HEATER_CURRENT_PROGRESS, PublicConsts.HEATER_CURRENT_PROGRESS_WHAT, intent);

        } else if (PublicConsts.ACTION_HEATER_CURRENT_PROGRESS_B.equals(action)) {
            //for text_heater_current_b and progress_heater_b

            modifyText(PublicConsts.ACTION_HEATER_CURRENT_PROGRESS_B, PublicConsts.HEATER_CURRENT_PROGRESS_B_WHAT, intent);

        } else if (PublicConsts.ACTION_TEXT_X.equals(action)) {
            //for text_x;

            modifyText(PublicConsts.ACTION_TEXT_X, PublicConsts.TEXT_X_WHAT, intent);


        } else if (PublicConsts.ACTION_TEXT_Y.equals(action)) {
            //for text_y;

            modifyText(PublicConsts.ACTION_TEXT_Y, PublicConsts.TEXT_Y_WHAT, intent);


        } else if (PublicConsts.ACTION_TEXT_Z.equals(action)) {
            //for text_z;

            modifyText(PublicConsts.ACTION_TEXT_Z, PublicConsts.TEXT_Z_WHAT, intent);


        } else if (PublicConsts.ACTION_TEXT_XYZ.equals(action)) {
            //for text_z /x / y
            modifyText(PublicConsts.ACTION_TEXT_XYZ, PublicConsts.TEXT_XYZ_WHAT, intent);


        } else if (PublicConsts.ACTION_BUNDLE_DISPLAY.equals(action)) {
            /** just be careful parse according to line
             * for text_heater_current text_heater_current_b text_bed_current
             progress_heater progress_heater_b progress_bed */
            modifyText(PublicConsts.ACTION_BUNDLE_DISPLAY, PublicConsts.BUNDLE_DISPLAY_WHAT, intent);

        } else if (PublicConsts.MODIFY_DISPLAY_ACTION.equals(action)) {

            handler.sendEmptyMessage(PublicConsts.MODIFY_DISPLAY);
        } else if (PublicConsts.ACTION_TEXT_VERSION.equals(action)) {

            modifyText(PublicConsts.ACTION_TEXT_VERSION, PublicConsts.TEXT_VERSION_WHAT, intent);
        } else if (PublicConsts.PARSE_TEMP.equals(action)) {
            handler.sendEmptyMessage(PublicConsts.PARSE_TEMP_WHAT);
        } else if (PublicConsts.ACTION_BEGIN_PRINT.equals(action)) {

            handler.sendEmptyMessage(PublicConsts.BEGIN_PRINT);

        } else if (PublicConsts.PAUSE_PRINT.equals(action)) {

            handler.sendEmptyMessage(PublicConsts.PAUSE_PRINT_WHAT);
        } else if (PublicConsts.TURN_FAN_OFF.equals(action)) {
            handler.sendEmptyMessage(PublicConsts.FAN_OFF_WHAT);

        } else if (PublicConsts.TURN_FAN_ON.equals(action)) {
            handler.sendEmptyMessage(PublicConsts.FAN_ON_WHAT);

        }

    }

    private void modifyText(String key, int mWhat, Intent intent) {

        Message msg = handler.obtainMessage();
        msg.obj = intent.getStringExtra(key);
        msg.what = mWhat;
        handler.sendMessage(msg);
    }
}
