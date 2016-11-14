package com.bjut.printer.BroadCast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.bjut.printer.Consts.PublicConsts;

public class LevelingReceiver extends BroadcastReceiver {

    private Handler handler;

    public LevelingReceiver() {
    }

    public LevelingReceiver(Handler mHandler) {

        this.handler = mHandler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (PublicConsts.ACTION_LEVELING_HIGH.equals(action)) {
            Message message=handler.obtainMessage();
            message.what=PublicConsts.LEVELING_HIGH_WHAT;
            message.obj=intent.getStringExtra(PublicConsts.ACTION_LEVELING_HIGH);
            handler.sendMessage(message);

        } else if (PublicConsts.ACTION_LEVELING_OK.equals(action)) {

            Message message=handler.obtainMessage();
            message.what=PublicConsts.LEVELING_OK_WHAT;
            message.obj=intent.getStringExtra(PublicConsts.ACTION_LEVELING_OK);
            handler.sendMessage(message);

        } else if (PublicConsts.ACTION_LEVELING_NOTHING.equals(action)) {

            handler.sendEmptyMessage(PublicConsts.LEVELING_NOTHING_WHAT);

        } else if (PublicConsts.ACTION_LEVELING_LOW.equals(action)) {
            Message message=handler.obtainMessage();
            message.what=PublicConsts.LEVELING_LOW_WHAT;
            message.obj=intent.getStringExtra(PublicConsts.ACTION_LEVELING_LOW);
            handler.sendMessage(message);

        }
    }
}
