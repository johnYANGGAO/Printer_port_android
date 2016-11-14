package com.bjut.printer.Operator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bjut.printer.Consts.PublicConsts;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by johnsmac on 9/13/16.
 */
public class SendBroadCast {


    //for progressBar

    public static void sendProgressRate(Context context, int rate) {
        Intent intent = new Intent();
        intent.setAction(PublicConsts.PROGRESS_RATE_ACTION);
        intent.putExtra(PublicConsts.PROGRESS_RATE_ACTION, rate);
        context.sendBroadcast(intent);

    }

    public static void sendDownloadDialog(Context context, String action, String descriptor) {

        Intent intent = new Intent();
        intent.setAction(action);
        if (descriptor != null) {
            intent.putExtra(action, descriptor);
        }
        context.sendBroadcast(intent);
    }

    //for myDialog
    public static void sendError(Context context, String reason) {
        Intent intent = new Intent();
        intent.setAction(PublicConsts.ERROR_ACTION);
        intent.putExtra(PublicConsts.ERROR_ACTION, reason);
        context.sendBroadcast(intent);

    }

    public static void sendWarning(Context context, String action, String reason) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(action, reason);
        context.sendBroadcast(intent);
    }

    public static void sendReboot(Context context, String reason) {

        Intent intent = new Intent();
        intent.setAction(PublicConsts.ERROR_ACTION_REBOOT);
        intent.putExtra(PublicConsts.ERROR_ACTION_REBOOT, reason);
        context.sendBroadcast(intent);
    }

    public static void sendSuccess(Context context, String title) {

        Intent intent = new Intent();
        intent.setAction(PublicConsts.NOTIFICATION_SUCCESS);
        intent.putExtra(PublicConsts.NOTIFICATION_SUCCESS, title);
        context.sendBroadcast(intent);

    }

    //for mission
    public static void sendStartSocketService(Context context) {

        Intent intent = new Intent();
        intent.setAction(PublicConsts.START_SERVERSOCKETSERVICE_ACTION);
        context.sendBroadcast(intent);

    }

    //for handler
    public static void sendModifyUI(Context context, String action, String message) {

        Intent intent = new Intent();
        intent.setAction(action);
        if (message != null) {
            intent.putExtra(action, message);
        }
        context.sendBroadcast(intent);
    }


    public static void sendGateOption(Context context, String action, Boolean status) {

        Intent intent = new Intent();
        intent.setAction(action);
        if (status != null) {
            intent.putExtra(action, status);
        }
        context.sendBroadcast(intent);
    }
}
