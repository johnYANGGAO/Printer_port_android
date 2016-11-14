package com.bjut.printer.BroadCast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.bjut.printer.Consts.PublicConsts;

/**
 * Created by johnsmac on 9/13/16.
 */

/**
 *This is maybe overkill, since BroadcastReceivers already
 * run on the Main Thread, which is the same as the "GUI thread".
 * So you are basically using a Handler to talk to the same thread.
 * It's absolutely fine to update GUI directly
 * from a receiver in the activity.but here's individual receiver
 * that we can use a Handler to communicate  activity;
 *
 */

public class StartSocketServiceReceiver extends BroadcastReceiver {

    private Handler handler;
    public StartSocketServiceReceiver(Handler mHandler) {
        this.handler=mHandler;
    }
    public StartSocketServiceReceiver(){}
    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
      if (PublicConsts.START_SERVERSOCKETSERVICE_ACTION.equals(action)){
          handler.sendEmptyMessage(PublicConsts.START_SOCKETSERVICE);
          Log.i("SocketService","StartSocketServiceReceiver has received the signal");
      }

    }
}
