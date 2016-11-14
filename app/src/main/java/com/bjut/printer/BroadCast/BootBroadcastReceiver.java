package com.bjut.printer.BroadCast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bjut.printer.activity.MainActivity;

//start our application automatic when boot the device
public class BootBroadcastReceiver extends BroadcastReceiver {

    static final String boot_action = "android.intent.action.BOOT_COMPLETED";

    public BootBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (boot_action.equals(intent.getAction())) {

            Intent starApplication = new Intent(context, MainActivity.class);
            starApplication.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(starApplication);

        }
    }
}
