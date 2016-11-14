package com.bjut.printer.BroadCast;

import android.content.IntentFilter;

import com.bjut.printer.Consts.PublicConsts;

/**
 * Created by johnsmac on 9/17/16.
 */
public class RegisterBroadCastManager {

    public static IntentFilter getMechanicalStatusFilter() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PublicConsts.MECHANICAL_STATUS);
        intentFilter.addAction(PublicConsts.GATE_STATUS);
        intentFilter.addAction(PublicConsts.GATE_PLAY);
        intentFilter.addAction(PublicConsts.PROGRESS_LABEL);
        intentFilter.addAction(PublicConsts.ACTION_HEATER_CURRENT_B);
        intentFilter.addAction(PublicConsts.ACTION_HEATER_TARGET_B);
        intentFilter.addAction(PublicConsts.ACTION_HEATER_TARGET);
        intentFilter.addAction(PublicConsts.ACTION_TEXT_SPEED);
        intentFilter.addAction(PublicConsts.ACTION_BET_TARGET);

        intentFilter.addAction(PublicConsts.ACTION_MULTIPLE_HEATER_TARGET_TEXT);
        intentFilter.addAction(PublicConsts.ACTION_HEATER_CURRENT_PROGRESS);
        intentFilter.addAction(PublicConsts.ACTION_HEATER_CURRENT_PROGRESS_B);
        intentFilter.addAction(PublicConsts.ACTION_TEXT_X);
        intentFilter.addAction(PublicConsts.ACTION_TEXT_Y);

        intentFilter.addAction(PublicConsts.ACTION_TEXT_Z);
        intentFilter.addAction(PublicConsts.ACTION_TEXT_XYZ);
        intentFilter.addAction(PublicConsts.ACTION_BUNDLE_DISPLAY);

        intentFilter.addAction(PublicConsts.MODIFY_DISPLAY_ACTION);
        intentFilter.addAction(PublicConsts.ACTION_TEXT_VERSION);

        intentFilter.addAction(PublicConsts.TURN_FAN_OFF);
        intentFilter.addAction(PublicConsts.TURN_FAN_ON);
        intentFilter.addAction(PublicConsts.ACTION_BEGIN_PRINT);
        intentFilter.addAction(PublicConsts.PAUSE_PRINT);

        return intentFilter;

    }
}
