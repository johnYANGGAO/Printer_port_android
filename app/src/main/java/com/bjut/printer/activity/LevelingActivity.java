package com.bjut.printer.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bjut.printer.BroadCast.LevelingReceiver;
import com.bjut.printer.Consts.PublicConsts;
import com.bjut.printer.Views.MyDialog;
import com.bjut.printer.application.MyApplication;
import com.bjut.printer.services.IOService;
import com.bjut.printer2.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by johnsmac on 9/16/16.
 */
public class LevelingActivity extends BaseActivity implements View.OnClickListener {
    private TextView textBack;
    private TextView rb_a, rb_b, rb_c, rb_d;
    //    private Animation animation;
    private Dialog dialog;
    private String which;
    private LevelingReceiver levelingReceiver;
    private int Z_NUMBER;
    private Boolean isInteraction;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case PublicConsts.LEVELING_HIGH_WHAT:
                    isInteraction = false;
                    myDialog.getNotifiCationDialog(SweetAlertDialog.WARNING_TYPE, "Waning", msg.obj.toString()
                    ).show();

                    break;
                case PublicConsts.LEVELING_OK_WHAT:
                    isInteraction = false;
                    Z_NUMBER = -1;//?
                    myDialog.getNotifiCationDialog(SweetAlertDialog.SUCCESS_TYPE, "note", msg.obj.toString());

                    break;
                case PublicConsts.LEVELING_NOTHING_WHAT:
                    isInteraction = true;
                    if (Z_NUMBER <= -1) {

                        return;
                    }
                    Z_NUMBER--;

                    sendGCommand("G1 Z" + Z_NUMBER + "\n");

                    break;

                case PublicConsts.LEVELING_LOW_WHAT:
                    isInteraction = false;
                    myDialog.getNotifiCationDialog(SweetAlertDialog.WARNING_TYPE, "Waning", msg.obj.toString()
                    ).show();
                    break;
            }
            if (!isInteraction) {
                stopLoading();
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leveling);
        Log.i("BaseActivity", "onCreate---->LevelingActivity 测试 重写方法 是否会被子忽略");
        rb_a = (TextView) findViewById(R.id.rb_a);
        rb_b = (TextView) findViewById(R.id.rb_b);
        rb_c = (TextView) findViewById(R.id.rb_c);
        rb_d = (TextView) findViewById(R.id.rb_d);

        findViewById(R.id.text_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.down).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rb_a.setOnClickListener(this);
        rb_b.setOnClickListener(this);
        rb_c.setOnClickListener(this);
        rb_d.setOnClickListener(this);

        registerLevelingBroadcast();

        myDialog.getClickDialog(SweetAlertDialog.WARNING_TYPE, "note", "请打开安全门，确保平台停止移动，再安装调平模块", "已满足以上条件", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                Log.i("LevelingActivity", ioService == null ? "ioService is null" : "ioService has value");
                sendGCommand("G1 Z0 \n");
                sweetAlertDialog.dismiss();
            }
        }).show();


    }

    private void sendGCommand(String command) {

        ioService.writeTOSerialPort(command);

    }

    private void registerLevelingBroadcast() {

        levelingReceiver = new LevelingReceiver(mHandler);

        IntentFilter filter = new IntentFilter();
        filter.addAction(PublicConsts.ACTION_LEVELING_OK);
        filter.addAction(PublicConsts.ACTION_LEVELING_HIGH);
        filter.addAction(PublicConsts.ACTION_LEVELING_NOTHING);
        filter.addAction(PublicConsts.ACTION_LEVELING_LOW);

        registerReceiver(levelingReceiver, filter);
    }


//    private void startAnimator(View button) {
//
//        if (animation != null) {
//
//            animation = null;
//        }
//        animation = AnimationUtils.loadAnimation(LevelingActivity.this, R.anim.rotate);
//        button.setAnimation(animation);
//
//    }
//
//    private void endAnimator() {
//        if (animation != null) {
//            animation.cancel();
//            animation = null;
//        }
//
//    }

    private void startLoading() {
        dialog = myDialog.getLoadingDialog(which + "正在调整中...");
    }

    private void stopLoading() {

        if (dialog != null) {

            dialog.cancel();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rb_a:
                myDialog.getOptionDialog(SweetAlertDialog.NORMAL_TYPE, "note", "调平A点", "OK", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sendGCommand("G1 X50 Y30 Z5 \n");
                        startLoading();
                        sweetAlertDialog.cancel();
                        Z_NUMBER = 5;

                    }
                }).show();

                which = "A :";

                break;
            case R.id.rb_b:
                myDialog.getOptionDialog(SweetAlertDialog.NORMAL_TYPE, "note", "调平B点", "OK", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sendGCommand("G1 X290 Y30 Z5 \n");
                        startLoading();
                        sweetAlertDialog.cancel();
                        Z_NUMBER = 5;

                    }
                }).show();

                which = "B :";
                break;
            case R.id.rb_c:
                myDialog.getOptionDialog(SweetAlertDialog.NORMAL_TYPE, "note", "调平C点", "OK", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sendGCommand("G1 X290 Y160 Z5 \n");
                        startLoading();
                        sweetAlertDialog.cancel();
                        Z_NUMBER = 5;

                    }
                }).show();
                which = "C:";
                break;
            case R.id.rb_d:
                myDialog.getOptionDialog(SweetAlertDialog.NORMAL_TYPE, "note", "调平D点", "OK", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sendGCommand("G1 X50 Y160 Z5 \n");

                        startLoading();
                        sweetAlertDialog.cancel();
                        Z_NUMBER = 5;

                    }
                }).show();
                which = "D :";
                break;

        }
    }

    @Override
    protected void onDestroy() {

        if (levelingReceiver != null) {
            unregisterReceiver(levelingReceiver);
            levelingReceiver = null;
        }
        super.onDestroy();

    }
}
