package com.bjut.printer.bean;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bjut.printer.Consts.PublicConsts;
import com.bjut.printer.Operator.SendBroadCast;
import com.bjut.printer.Views.MyDialog;
import com.bjut.printer.activity.MainActivity;
import com.bjut.printer.utils.SoundPlayUtils;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;

public class LogAdd extends BroadcastReceiver {
    public LogAdd() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:

                // String appid = bundle.getString("appid");
                byte[] payload = bundle.getByteArray("payload");

                if (payload != null) {
                    String data = new String(payload);
                    if (data.equals("hacker")) {
//                        MyDialog myDialog = new MyDialog(context);//token null is not for application context
//                        myDialog.getLoadingDialog("permission denied！").show();
                        SendBroadCast.sendReboot(context,"permission denied \n you have no rights to use it now \n please contact developer");

                    } else if (data.equals("test")) {

                        SoundPlayUtils soundPlayUtils = new SoundPlayUtils(context);
                        soundPlayUtils.play_voice();
                    }
                }

        }
    }
}
