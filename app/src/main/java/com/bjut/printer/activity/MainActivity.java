package com.bjut.printer.activity;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.bjut.printer.BroadCast.MechanicalStatusReceiver;
import com.bjut.printer.BroadCast.RegisterBroadCastManager;
import com.bjut.printer.BroadCast.StartSocketServiceReceiver;
import com.bjut.printer.BroadCast.WarningReceiver;
import com.bjut.printer.Consts.PublicConsts;
import com.bjut.printer.Operator.PrintManager;
import com.bjut.printer.Operator.SaveEnergy;
import com.bjut.printer.ThreadManager.GetAssetFileThread;
import com.bjut.printer.Views.MyDialog;
import com.bjut.printer.Views.PopUpNotification;
import com.bjut.printer.Views.RoundProgressBar;
import com.bjut.printer.application.MyApplication;
import com.bjut.printer.bean.GlobalEditor;
import com.bjut.printer.bean.WorkLog;
import com.bjut.printer.services.IOService;
import com.bjut.printer.services.ServerSocketService;
import com.bjut.printer.utils.FileUtils;
import com.bjut.printer.utils.MyUtils;
import com.bjut.printer.utils.ProcessCharacterUtil;
import com.bjut.printer.utils.TimeUtils;
import com.bjut.printer2.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends BaseActivity {
    private ServerSocketService serverSocketService = null;
    private MyApplication mApplication;
    private static SharedPreferences.Editor sharedata;
    private Button btn_heater_mius, btn_heater_plus, btn_heater_mius_b, btn_heater_plus_b, btn_bed_mius, btn_bed_plus;
    private TextView text_heater_current, text_heater_target, text_heater_current_b, text_heater_target_b,
            text_bed_current, text_bed_target, text_version;
    private ProgressBar progress_heater, progress_heater_b, progress_bed;
    private RadioGroup rg_mode, rg_executorselect;
    private RadioButton rb_pla, rb_abs, rb_cd;
    private RadioButton rb_executora, rb_executorb;
    private int CurrentExcutor = 1;
    private int CurrentMode = 1;
    private String ExcutorHeatControlName = "T0";
    private String ExcutorInOutControlName = "T0";
    private String target_heater_temperature_a = "200", target_heater_temperature_b = "200", target_bed_temperature = "50";
    private Button btn_preheat, btn_out, btn_in, btn_inout_a, btn_inout_b;
    private Button btn_x_mius, btn_x_plus, btn_y_mius, btn_y_plus, btn_z_mius, btn_z_plus;
    private TextView text_x, text_y, text_z, text_heater_current_degree_b, text_heater_target_degree_b, text_heater_target_hint_b, text_heater_current_hint_b;
    private RadioGroup radiogroup;
    private RadioButton rb_step1, rb_step2, rb_step3;
    private double step = 1;
    private boolean isConnected;
    private Button btn_backorigin;
    private RoundProgressBar progress_print;
    private SeekBar seekbar_speed;
    private TextView text_speed;
    private String speed;
    private Button btn_play;
    private Button btn_stop;
    private boolean playorpause = false; // 开始之后true
    public boolean playstatus = false;
    private boolean pausestatus = false;
    private boolean pauseorgooon = true;
    private boolean timestatus = false;
    private boolean temperaturestatus = true;
    private boolean iscansend = false;
    private String pause_x = "0.0", pause_y = "0.0", pause_z = "0.0", pause_e = "0.0";
    //    public boolean PC_SEND = false;
    private TextView text_filename;
    private Button btn_file;
    private Button btn_usb;
    private Button btn_setup;
    private Button btn_download;
    private TextView text_used_time;
    private int recLen;
    private Button btn_unfinish;
    public boolean gatestatus = true; // 安全门 开:false关:true
    private ImageView img_heater_b;
    private Dialog USBDialog;
    private PrintManager printManager;
    private PopUpNotification popUpNotification;
    private WarningReceiver warningReceiver;
    private StartSocketServiceReceiver startSocketServiceReceiver;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.main);

        linearLayout = (LinearLayout) findViewById(R.id.layout_main);
        mApplication = (MyApplication) getApplication();
        mApplication.mMainActivity = this;
        mApplication.m_szAndroidID = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
        sharedata = GlobalEditor.getSharedataeditorInstance(this);

        findViews();
        setListener();


        printManager = new PrintManager(this, mApplication, UIHandler);
        popUpNotification = new PopUpNotification(this, UIHandler, mApplication);

        if (!TextUtils.isEmpty(mApplication.configManager.unfilename)) {
//            popUpNotification.PopUpFileUnPrinted(linearLayout, UIHandler);
            popUpNotification.PopUpFileUnPrinted(btn_heater_mius, UIHandler);
//            myDialog.getNotifiCationDialog(SweetAlertDialog.NORMAL_TYPE, "note", "你有未完成的打印文件\n 可以选择续打按钮重新打印").show();

        }

        mechanicalStatusReceiver = new MechanicalStatusReceiver(UIHandler);

        startSocketServiceReceiver = new StartSocketServiceReceiver(UIHandler);

        warningReceiver = new WarningReceiver(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {

                /** //TODO
                 continueToPrint();
                 */
            }
        });
        registerMechanicalStatusBroadCast();
        registerWarningReceiver();
        registerStartSocketServiceReceiver();
    }

    private void registerWarningReceiver() {

        IntentFilter warnFilter = new IntentFilter();
        warnFilter.addAction(PublicConsts.WARNING_ACTION);
        warnFilter.addAction(PublicConsts.WARNING_CONTINUE_PRINT_ACTION);
        registerReceiver(warningReceiver, warnFilter);

    }

    private void registerStartSocketServiceReceiver() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PublicConsts.START_SERVERSOCKETSERVICE_ACTION);
        registerReceiver(startSocketServiceReceiver, intentFilter);

    }

    private ServiceConnection connect = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

            serverSocketService = null;
            isConnected = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("MainActivity", "ServerSocketService is bind");
            serverSocketService = ((ServerSocketService.LocalBinder) service).getService();
            //start status threads
            serverSocketService.startStatusThread(MainActivity.this, UIHandler, timestatus, temperaturestatus);
            isConnected = true;
        }
    };


    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isConnected) {
            unbindService(connect);
        }
        if (mechanicalStatusReceiver != null) {
            unregisterReceiver(mechanicalStatusReceiver);
        }
        if (warningReceiver != null) {
            unregisterReceiver(warningReceiver);
            warningReceiver = null;
        }
        if (startSocketServiceReceiver != null) {
            unregisterReceiver(startSocketServiceReceiver);
            startSocketServiceReceiver = null;
        }
    }

    private void showUsbDialog() {
        USBDialog = myDialog.showUsbDialog(MainActivity.this);
        USBDialog.setCanceledOnTouchOutside(false);
        USBDialog.show();

    }

    private void findViews() {

        btn_heater_mius = (Button) findViewById(R.id.btn_heater_mius);
        btn_heater_plus = (Button) findViewById(R.id.btn_heater_plus);
        btn_heater_mius_b = (Button) findViewById(R.id.btn_heater_mius_b);
        btn_heater_plus_b = (Button) findViewById(R.id.btn_heater_plus_b);
        btn_bed_mius = (Button) findViewById(R.id.btn_bed_mius);
        btn_bed_plus = (Button) findViewById(R.id.btn_bed_plus);
        btn_inout_a = (Button) findViewById(R.id.btn_inout_a);
        btn_inout_b = (Button) findViewById(R.id.btn_inout_b);

        btn_heater_mius.setEnabled(false);
        btn_heater_plus.setEnabled(false);
        btn_heater_mius_b.setEnabled(false);
        btn_heater_plus_b.setEnabled(false);
        btn_bed_mius.setEnabled(false);
        btn_bed_plus.setEnabled(false);
        text_version = (TextView) findViewById(R.id.text_version);
        text_heater_current = (TextView) findViewById(R.id.text_heater_current);
        text_heater_target = (TextView) findViewById(R.id.text_heater_target);
        text_heater_current_b = (TextView) findViewById(R.id.text_heater_current_b);
        text_heater_target_b = (TextView) findViewById(R.id.text_heater_target_b);
        text_bed_current = (TextView) findViewById(R.id.text_bed_current);
        text_bed_target = (TextView) findViewById(R.id.text_bed_target);
        text_heater_current_degree_b = (TextView) findViewById(R.id.text_heater_current_degree_b);
        text_heater_target_degree_b = (TextView) findViewById(R.id.text_heater_target_degree_b);
        text_heater_target_hint_b = (TextView) findViewById(R.id.text_heater_target_hint_b);
        text_heater_current_hint_b = (TextView) findViewById(R.id.text_heater_current_hint_b);
        img_heater_b = (ImageView) findViewById(R.id.img_heater_b);

        progress_heater = (ProgressBar) findViewById(R.id.progress_heater);
        progress_heater_b = (ProgressBar) findViewById(R.id.progress_heater_b);
        progress_bed = (ProgressBar) findViewById(R.id.progress_bed);
        rg_mode = (RadioGroup) findViewById(R.id.rg_mode);
        rb_pla = (RadioButton) findViewById(R.id.rb_pla);
        rb_abs = (RadioButton) findViewById(R.id.rb_abs);
        rb_cd = (RadioButton) findViewById(R.id.rb_cd);
        rb_executora = (RadioButton) findViewById(R.id.rb_executora);
        rb_executorb = (RadioButton) findViewById(R.id.rb_executorb);

        btn_preheat = (Button) findViewById(R.id.btn_preheat);
        btn_out = (Button) findViewById(R.id.btn_out);
        btn_in = (Button) findViewById(R.id.btn_in);
        btn_x_mius = (Button) findViewById(R.id.btn_x_mius);
        text_x = (TextView) findViewById(R.id.text_x);
        btn_x_plus = (Button) findViewById(R.id.btn_x_plus);
        btn_y_mius = (Button) findViewById(R.id.btn_y_mius);
        text_y = (TextView) findViewById(R.id.text_y);
        btn_y_plus = (Button) findViewById(R.id.btn_y_plus);
        btn_z_mius = (Button) findViewById(R.id.btn_z_mius);
        text_z = (TextView) findViewById(R.id.text_z);
        btn_z_plus = (Button) findViewById(R.id.btn_z_plus);
        radiogroup = (RadioGroup) findViewById(R.id.radiogroup);
        rg_executorselect = (RadioGroup) findViewById(R.id.rg_executorselect);
        rb_step1 = (RadioButton) findViewById(R.id.rb_step1);
        rb_step2 = (RadioButton) findViewById(R.id.rb_step2);
        rb_step3 = (RadioButton) findViewById(R.id.rb_step3);

        btn_backorigin = (Button) findViewById(R.id.btn_backorigin);

        progress_print = (RoundProgressBar) findViewById(R.id.progress_print);
        text_used_time = (TextView) findViewById(R.id.text_used_time);

        seekbar_speed = (SeekBar) findViewById(R.id.seekbar_speed);
        text_speed = (TextView) findViewById(R.id.text_speed);
        btn_play = (Button) findViewById(R.id.btn_play);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_unfinish = (Button) findViewById(R.id.btn_unfinish);
        text_filename = (TextView) findViewById(R.id.text_filename);
        btn_file = (Button) findViewById(R.id.btn_file);
        btn_usb = (Button) findViewById(R.id.btn_usb);

        if (FileUtils.GetUsbDiskPath() != null) {
            btn_usb.setEnabled(true);

        } else {
            btn_usb.setEnabled(false);
        }

        btn_setup = (Button) findViewById(R.id.btn_setup);
        btn_download = (Button) findViewById(R.id.btn_download);
    }

    private void addWorkLog(String orderData, int type, String value) {

        WorkLog worklog = new WorkLog();
        mApplication.worklog_no++;
        worklog.setId(mApplication.worklog_no);
        worklog.setSenddata(orderData);
        worklog.setType(type);

        if (value != null) {
            worklog.setValue(value);
        }
        mApplication.workloglist.add(worklog);

    }

    private void setListener() {

        btn_heater_mius.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                String temperature = text_heater_target.getText().toString();
                if (temperature.equals("--") || !ProcessCharacterUtil.isNumber(temperature)) {
                    temperature = "0";
                }

                Float current_heater_temperature = Float.valueOf(temperature);
                if (current_heater_temperature > 1) {
                    target_heater_temperature_a = current_heater_temperature - 1 + "";
                } else {
                    target_heater_temperature_a = "0";
                }
                String M104 = "M104 T0 S" + target_heater_temperature_a + "\n";
                SendCommand(M104);

                addWorkLog(M104, 8, null);

            }
        });

        btn_heater_plus.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                if (text_heater_target.getText().toString().equals("--") || !ProcessCharacterUtil.isNumber(text_heater_target.getText().toString())) {
                    text_heater_target.setText("0");
                }
                Float current_heater_temperature = Float.valueOf(text_heater_target.getText().toString());
                target_heater_temperature_a = current_heater_temperature + 1 + "";
                String M104 = "M104 T0 S" + target_heater_temperature_a + "\n";
                SendCommand(M104);
                addWorkLog(M104, 8, null);
            }
        });

        btn_heater_mius_b.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                String temperature = text_heater_current_b.getText().toString();
                if (temperature.equals("--") || !ProcessCharacterUtil.isNumber(temperature)) {
                    temperature = "0";
                }

                Float current_heater_temperature = Float.valueOf(temperature);
                if (current_heater_temperature > 1) {
                    target_heater_temperature_b = current_heater_temperature - 1 + "";
                } else {
                    target_heater_temperature_b = "0";
                }
                String M104 = "M104 T1 S" + target_heater_temperature_b + "\n";
                SendCommand(M104);
                addWorkLog(M104, 8, null);
            }
        });

        btn_heater_plus_b.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                String str = text_heater_current_b.getText().toString();
                if (str.equals("--") || !ProcessCharacterUtil.isNumber(str)) {
                    str = "0";
                }
                Float current_heater_temperature = Float.valueOf(str);
                target_heater_temperature_b = current_heater_temperature + 1 + "";
                String M104 = "M104 T1 S" + target_heater_temperature_b + "\n";
                SendCommand(M104);
                addWorkLog(M104, 8, null);
            }
        });

        btn_bed_mius.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                String bed_temperature = text_bed_target.getText().toString();
                if (bed_temperature.equals("--") || !ProcessCharacterUtil.isNumber(bed_temperature)) {
                    bed_temperature = "0";
                }

                Float current_bed_temperature = Float.valueOf(bed_temperature);
                if (current_bed_temperature > 0) {
                    target_bed_temperature = current_bed_temperature - 1 + "";
                } else {
                    target_bed_temperature = "0";
                }
                String M140 = "M140 S" + target_bed_temperature + "\n";
                SendCommand(M140);
                addWorkLog(M140, 9, null);
            }
        });

        btn_bed_plus.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                String bed_temperature = text_bed_target.getText().toString();
                if (bed_temperature.equals("--") || !ProcessCharacterUtil.isNumber(bed_temperature)) {
                    bed_temperature = "0";
                }
                Float current_bed_temperature = Float.valueOf(bed_temperature);
                target_bed_temperature = current_bed_temperature + 1 + "";
                String M140 = "M140 S" + target_bed_temperature + "\n";
                SendCommand(M140);
                addWorkLog(M140, 9, null);
            }
        });

        progress_heater.setMax(260);
        progress_heater_b.setMax(260);
        progress_bed.setMax(120);

        rg_mode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (CurrentExcutor == 1) {
                    if (checkedId == rb_pla.getId()) {
                        target_heater_temperature_a = "200";
                        target_bed_temperature = "50";
                        CurrentMode = 1;
                    } else if (checkedId == rb_abs.getId()) {
                        target_heater_temperature_a = "230";
                        target_bed_temperature = "70";
                        CurrentMode = 2;
                    } else if (checkedId == rb_cd.getId()) {
                        target_heater_temperature_a = "0";
                        target_bed_temperature = "0";
                        CurrentMode = 3;
                    }
                }
                if (CurrentExcutor == 2) {

                    if (checkedId == rb_pla.getId()) {
                        target_heater_temperature_b = "200";
                        target_bed_temperature = "50";
                        CurrentMode = 1;
                    } else if (checkedId == rb_abs.getId()) {
                        target_heater_temperature_b = "230";
                        target_bed_temperature = "70";
                        CurrentMode = 2;
                    } else if (checkedId == rb_cd.getId()) {
                        target_heater_temperature_b = "0";
                        target_bed_temperature = "0";
                        CurrentMode = 3;
                    }

                }

            }
        });
        rg_executorselect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == rb_executora.getId()) {
                    ExcutorHeatControlName = "T0";
                    CurrentExcutor = 1;
                    if (CurrentMode == 1) {
                        target_heater_temperature_a = "200";
                        target_bed_temperature = "50";
                    } else if (CurrentMode == 2) {
                        target_heater_temperature_a = "230";
                        target_bed_temperature = "70";
                    } else if (CurrentMode == 3) {
                        target_heater_temperature_a = "0";
                        target_bed_temperature = "0";
                    }
                } else if (checkedId == rb_executorb.getId()) {
                    ExcutorHeatControlName = "T1";
                    CurrentExcutor = 2;
                    if (CurrentMode == 1) {
                        target_heater_temperature_b = "200";
                        target_bed_temperature = "50";
                    } else if (CurrentMode == 2) {
                        target_heater_temperature_b = "230";
                        target_bed_temperature = "70";
                    } else if (CurrentMode == 3) {
                        target_heater_temperature_b = "0";
                        target_bed_temperature = "0";
                    }

                }
            }
        });


        btn_preheat.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                btnPreHeatClick();

            }
        });

        btn_inout_a.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                if (CurrentExcutor == 1) {
                    btn_inout_a.setBackgroundResource(R.drawable.extruder_a_normal);
                    CurrentExcutor = 2;
                    btn_inout_b.setBackgroundResource(R.drawable.extruder_b_pressed);
                } else {

                    btn_inout_a.setBackgroundResource(R.drawable.extruder_a_pressed);
                    CurrentExcutor = 1;
                    btn_inout_b.setBackgroundResource(R.drawable.extruder_b_normal);

                }

            }
        });
        btn_inout_b.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                if (CurrentExcutor == 1) {
                    btn_inout_a.setBackgroundResource(R.drawable.extruder_a_normal);
                    CurrentExcutor = 2;
                    btn_inout_b.setBackgroundResource(R.drawable.extruder_b_pressed);
                } else {


                    btn_inout_a.setBackgroundResource(R.drawable.extruder_a_pressed);
                    CurrentExcutor = 1;
                    btn_inout_b.setBackgroundResource(R.drawable.extruder_b_normal);

                }
            }
        });
        btn_out.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                btnOutClick();
            }
        });

        btn_in.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {


                btnInClick();

            }
        });

        btn_x_mius.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                String str = text_x.getText().toString();
                if (str.equals("--")) {
                    return;
                }

                double num = Double.valueOf(text_x.getText().toString());
                num = num - step;
                if (num <= 0) {
                    num = 0;
                }
                String target_x = String.format("%.1f", num);
                String G1X = "G1 X" + target_x + "\n";

                SendCommand("G90\n" + G1X);

                addWorkLog(G1X, 1, target_x);

            }
        });

        btn_x_plus.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                String str = text_x.getText().toString().trim();
                if (str.equals("--") || !ProcessCharacterUtil.isNumber(str)) {
                    return;
                }
                double num = Double.valueOf(text_x.getText().toString());
                num = num + step;
                if (num >= 200) {
                    num = 200;
                }
                String target_x = String.format("%.1f", num);
                String G1X = "G1 X" + target_x + "\n";
                SendCommand("G90\n" + G1X);

                addWorkLog(G1X, 1, target_x);
            }
        });

        btn_y_mius.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                String str = text_y.getText().toString().trim();
                if (str.equals("--") || !ProcessCharacterUtil.isNumber(str)) {
                    return;
                }
                double num = Double.valueOf(str);
                num = num - step;
                if (num <= 0) {
                    num = 0;
                }
                String target_y = String.format("%.1f", num);
                String G1Y = "G1 Y" + target_y + "\n";
                SendCommand("G90\n" + G1Y);

                addWorkLog(G1Y, 2, target_y);
            }
        });

        btn_y_plus.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                String str = text_y.getText().toString().trim();
                if (str.equals("--") || !ProcessCharacterUtil.isNumber(str)) {
                    return;
                }
                double num = Double.valueOf(str);
                num = num + step;
                if (num >= 200) {
                    num = 200;
                }
                String target_y = String.format("%.1f", num);
                String G1Y = "G1 Y" + target_y + "\n";
                SendCommand("G90\n" + G1Y);

                addWorkLog(G1Y, 2, target_y);
            }
        });

        btn_z_mius.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                btnZMiusClick();
            }
        });

        btn_z_plus.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                btnZPlusClick();
            }
        });

        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == rb_step1.getId()) {
                    step = 0.1;
                } else if (checkedId == rb_step2.getId()) {
                    step = 1;
                } else if (checkedId == rb_step3.getId()) {
                    step = 10;
                }
            }

        });

        btn_backorigin.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                popUpNotification.showPopUp(v, text_x.getText().toString(),
                        text_y.getText().toString(), text_z.getText().toString());
            }
        });

        seekbar_speed.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //yhp-2016-05-30 why set text_speed=""
                if (fromUser) {
                    speed = progress + 10 + "";
                    text_speed.setText(speed + "%");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {

                SendCommand("M220 S" + speed + "\n");

                addWorkLog("M220 S" + speed + "\n", 10, null);

            }
        });

        btn_play.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                PressPlay();
            }
        });

        btn_stop.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                if (playstatus) {

                    myDialog.getOptionDialog(SweetAlertDialog.NORMAL_TYPE, "note", "确定要停止打印吗?", "确认", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            btnStopSure();
                        }
                    }).show();

                } else {
                    myDialog.getNotifiCationDialog(SweetAlertDialog.NORMAL_TYPE, "note", "打印机没有打印").show();
                }
            }
        });

        btn_unfinish.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                if (TextUtils.isEmpty(mApplication.configManager.unfilename) || TextUtils.isEmpty(mApplication.configManager.percent)) {
                    myDialog.getNotifiCationDialog(SweetAlertDialog.NORMAL_TYPE, "note", "没有未打印的文件！").show();
                } else {
                    popUpNotification.PopUpFileUnPrinted(v, UIHandler);
                }
            }
        });

        btn_file.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, FileChooser.class);
                intent.putExtra(FileChooser.EXTRA_TITLE, getResources().getString(R.string.app_name));
                intent.putExtra(FileChooser.EXTRA_FILEPATH, MyUtils.getSDPath() + "/Gcode");
                intent.putExtra(FileChooser.EXTRA_FILTERS, new String[]{".gcode"});
                startActivityForResult(intent, 1);
            }
        });

        btn_usb.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, FileChooser.class);
                intent.putExtra(FileChooser.EXTRA_TITLE, getResources().getString(R.string.app_name));
                intent.putExtra(FileChooser.EXTRA_FILEPATH, FileUtils.GetUsbDiskPath());
                intent.putExtra(FileChooser.EXTRA_FILTERS, new String[]{".gcode"});
                startActivityForResult(intent, 1);
            }
        });

        btn_setup.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, SetUpActivity.class);
                startActivity(intent);
            }
        });

        btn_download.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
                startActivity(intent);
            }
        });

    }

    private void btnPreHeatClick() {
        String target_temp = "200";
        if (CurrentExcutor == 1) {
            target_temp = target_heater_temperature_a;
        }
        if (CurrentExcutor == 2) {
            target_temp = target_heater_temperature_b;
        }
        String M104 = "M104 " + ExcutorHeatControlName + " S" + target_temp + "\n";
        SendCommand(M104);

        addWorkLog(M104, 11, null);
        //Sleeping in the UI thread is almost always wrong.
        // Use postDelayed of the handler instead.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String M140 = "M140 S" + target_bed_temperature + "\n";
                SendCommand(M140);
                addWorkLog(M140, 9, null);
            }
        }, 500);

    }

    private void btnZMiusClick() {

        String str = text_z.getText().toString().trim();
        if (str.equals("--") || !ProcessCharacterUtil.isNumber(str)) {
            return;
        }
        double num = Double.valueOf(str);
        num = num - step;
        if (num <= 0) {
            num = 0;
        }
        String target_z = String.format("%.1f", num);
        String G1Z = "G1 Z" + target_z + "\n";
        SendCommand("G90\n" + G1Z);

        addWorkLog(G1Z, 3, target_z);

    }

    private void btnOutClick() {
        if (CurrentExcutor == 1) {
            float current_e = mApplication.current_e - Float.valueOf(mApplication.configManager.extruder_test_step);
            if (current_e <= 0) {
                current_e = 0;
            }

            String ExchangeExcutor = "T0 \n";
            SendCommand(ExchangeExcutor);

            addWorkLog(ExchangeExcutor, 7, String.valueOf(current_e));

            String G1E = "G1 E" + current_e + "\n";
            SendCommand(G1E);

            addWorkLog(G1E, 7, String.valueOf(current_e));

        }
        if (CurrentExcutor == 2) {
            float current_e_b = mApplication.current_e_b - Float.valueOf(mApplication.configManager.extruder_test_step);
            if (current_e_b <= 0) {
                current_e_b = 0;
            }

            String ExchangeExcutor = "T1 \n";
            SendCommand(ExchangeExcutor);

            addWorkLog(ExchangeExcutor, 44, String.valueOf(current_e_b));

            String G1E = "G1 E" + current_e_b + "\n";
            SendCommand(G1E);

            addWorkLog(G1E, 44, String.valueOf(current_e_b));

        }

    }

    private void btnZPlusClick() {
        String str = text_z.getText().toString().trim();
        if (str.equals("--") || !ProcessCharacterUtil.isNumber(str)) {
            return;
        }
        double num = Double.valueOf(str);
        num = num + step;
        if (num >= 200) {
            num = 200;
        }
        mApplication.current_z = String.format("%.1f", num);
        String G1Z = "G1 Z" + mApplication.current_z + "\n";
        SendCommand("G90\n" + G1Z);

        addWorkLog(G1Z, 3, mApplication.current_z);

    }

    private void btnInClick() {

        if (CurrentExcutor == 1) {
            float current_e = mApplication.current_e + Float.valueOf(mApplication.configManager.extruder_test_step);

            String ExchangeExcutor = "T0 \n";
            SendCommand(ExchangeExcutor);

            addWorkLog(ExchangeExcutor, 7, String.valueOf(current_e));

            String G1E = "G1 E" + current_e + "\n";
            SendCommand(G1E);

            addWorkLog(G1E, 7, String.valueOf(current_e));
        }
        if (CurrentExcutor == 2) {
            float current_e_b = mApplication.current_e_b + Float.valueOf(mApplication.configManager.extruder_test_step);

            String ExchangeExcutor = "T1 \n";
            SendCommand(ExchangeExcutor);

            addWorkLog(ExchangeExcutor, 44, String.valueOf(current_e_b));

            String G1E = "G1 E" + current_e_b + "\n";
            SendCommand(G1E);

            addWorkLog(G1E, 44, String.valueOf(current_e_b));

        }
    }

    private void btnStopSure() {
        pausestatus = false;
        pauseorgooon = true;
        playorpause = false;
        playstatus = false;
        timestatus = false;
        SendCommand("M114\n");

        addWorkLog("M114\n", 15, null);

        sharedata.putString("unfilename", "");
        sharedata.putString("unfilepath", "");
        sharedata.commit();

        mApplication.configManager.printlist.clear();
        mApplication.configManager.unfilename = "";
        mApplication.configManager.unfilepath = "";
        mApplication.configManager.percent = "";
        mApplication.filepath = "";
        mApplication.configManager.current_print = 0;
        mApplication.configManager.list_line = 0;
        mApplication.get_line = 0;
        text_filename.setText("打印已被停止");
        btn_file.setEnabled(true);
        btn_setup.setEnabled(true);
        btn_download.setEnabled(true);
    }


    private void continueToPrint() {
        playstatus = true;
        String M109 = "M109 T0 S" + mApplication.configManager.heater_current_a + "\n";

        ioService.writeTOSerialPort(M109);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String M109 = "M109 T1 S" + mApplication.configManager.heater_current_b + "\n";
                ioService.writeTOSerialPort(M109);
                printManager.BeginPrint();

                progress_print.setMax(mApplication.configManager.gcodelength);
                mApplication.filepath = mApplication.configManager.unfilepath;
            }
        }, 1000);


    }

    /**
     * UserInterfaceHandler
     */
    public Handler UIHandler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case PublicConsts.POPUPWINDOW_CONFIRM:

                    SendCommand(msg.obj.toString());

                    break;
                case PublicConsts.START_SOCKETSERVICE:
                    Log.i("SocketService", "mainActivity has received the message of starting SocketService  from handler");
                    MainActivity.this.getApplicationContext().bindService(new Intent(MainActivity.this, ServerSocketService.class), connect, Context.BIND_AUTO_CREATE);

                    break;

                case PublicConsts.PAUSE_PRINT_WHAT:

                    PressPlay();

                    break;
                case PublicConsts.BEGINPRINT_NOTE:

                    SendCommand(msg.obj.toString());
                    progress_print.setProgress(mApplication.configManager.current_print);
                    int a = (mApplication.configManager.current_print * 100 / mApplication.configManager.gcodelength);
                    mApplication.configManager.percent = a + "" + "%";
                    break;
                case 2:
                    printedDown();
                    break;
                case 3:
                    recLen++;
                    text_used_time.setText(TimeUtils.GetUsedTime(recLen));
                    break;
                case PublicConsts.PARSE_TEMP_WHAT:
                    if (!pausestatus && playstatus) {
                        mApplication.configManager.current_print++;
                        if (mApplication.configManager.current_print == mApplication.configManager.gcodelength) {
                            UIHandler.sendEmptyMessage(2);
                            iscansend = false;
                        } else {
                            if (iscansend) {
                                printManager.BeginPrint();
                            }
                        }
                    }
                    break;
                case PublicConsts.GCODE_LENGTH:
                    loadDialog.cancel();
                    getAssetFileThread.interrupt();

                    String filename = mApplication.filepath.substring(mApplication.filepath.lastIndexOf('/') + 1,
                            mApplication.filepath.length());

                    text_filename.setText("当前选择文件:" + filename);
                    mApplication.configManager.unfilename = filename;
                    mApplication.configManager.unfilepath = mApplication.filepath;

                    progress_print.setMax(msg.arg1);
//
//                    if (PC_SEND) {
//                        PC_SEND = false;
//                        PressPlay();
//                    }
                    break;
                case 6:
                    ReadAsset(mApplication.filepath);
                    break;

                case 8:
                    SendCommand("M105\n");
                    break;
                case 9:
                    SendCommand("M300 S300 P2000\n");
                    break;

                case PublicConsts.GCODE_LENGTH_SHORT:

                    myDialog.getNotifiCationDialog(SweetAlertDialog.ERROR_TYPE, " ", "打印文件内容格式错误，请检查文件").show();

                    break;
                case PublicConsts.USB_STATUS_THREAD_ON: {
                    if (mApplication.usbstatus == 0) {
                        mApplication.usbstatus = 1;
                        btn_usb.setEnabled(true);
                        if (USBDialog == null) {
                            showUsbDialog();
                        }
                    }
                }
                break;
                case PublicConsts.USB_STATUS_THREAD_OFF: {

                    mApplication.usbstatus = 0;
                    btn_usb.setEnabled(false);
                    if (USBDialog != null) {
                        USBDialog.dismiss();
                        USBDialog = null;
                    }
                }
                break;
                case PublicConsts.MODIFY_DISPLAY:
                    modifyDisPlay();
                    break;
                case PublicConsts.POPUP_PRINT_WHAT:

                    myDialog.getOptionDialog(SweetAlertDialog.NORMAL_TYPE, "note", "确定要继续打印该文件吗?", "确定", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            continueToPrint();
                        }
                    }).show();

                    break;
                case PublicConsts.FAN_OFF_WHAT:

                    SendCommand("M107 \n");
                    SaveEnergy saveEnergy = new SaveEnergy(MainActivity.this);
                    saveEnergy.wakeOrLock(false);

                    break;
                case PublicConsts.FAN_ON_WHAT:

                    SendCommand("M106 S127 \n");

                    SaveEnergy energy = new SaveEnergy(MainActivity.this);
                    energy.wakeOrLock(true);

                    break;
                case PublicConsts.GATE_STATUS_WHAT:
                    gatestatus = (boolean) msg.obj;
                    break;
                case PublicConsts.MECHANICAL_STATUS_MSG_WHAT:
                    text_filename.setText(msg.obj.toString());
                    break;
                case PublicConsts.GATE_PLAY_WHAT:
                    PressPlay();
                    break;

                case PublicConsts.PROGRESS_LABEL_WHAT:
                    caseTen();
                    break;
                case PublicConsts.HEATER_CURRENT_B_WHAT:
                    text_heater_current_b.setText(msg.obj.toString());
                    break;
                case PublicConsts.HEATER_TARGET_B_WHAT:

                    text_heater_target_b.setText(msg.obj.toString());
                    break;
                case PublicConsts.HEATER_TARGET_WHAT:

                    text_heater_target.setText(msg.obj.toString());
                    break;
                case PublicConsts.TEXT_SPEED_WHAT:
                    text_speed.setText(msg.obj.toString());
                    break;
                case PublicConsts.BET_TARGET_WHAT:
                    text_bed_target.setText(msg.obj.toString());
                    break;

                case PublicConsts.HEATER_TARGET_ALL_WHAT:

                    String[] targetData = msg.obj.toString().split(",");

                    text_heater_target.setText(targetData[0]);
                    text_heater_target_b.setText(targetData[1]);

                    break;
                case PublicConsts.HEATER_CURRENT_PROGRESS_WHAT:

                    float heater_temperature_f = Float.parseFloat(msg.obj.toString());
                    progress_heater.setProgress((int) heater_temperature_f);
                    text_heater_current.setText(msg.obj.toString());

                    break;
                case PublicConsts.HEATER_CURRENT_PROGRESS_B_WHAT:

                    float heater_temperature = Float.parseFloat(msg.obj.toString());
                    progress_heater_b.setProgress((int) heater_temperature);
                    text_heater_current_b.setText(msg.obj.toString());

                    break;
                case PublicConsts.TEXT_X_WHAT:
                    text_x.setText(msg.obj.toString());
                    break;
                case PublicConsts.TEXT_Y_WHAT:
                    text_y.setText(msg.obj.toString());
                    break;
                case PublicConsts.TEXT_Z_WHAT:
                    text_z.setText(msg.obj.toString());
                    break;
                case PublicConsts.TEXT_XYZ_WHAT:
                    String[] data = msg.obj.toString().split(",");
                    text_x.setText(data[0]);
                    text_y.setText(data[1]);
                    text_z.setText(data[2]);

                    break;
                case PublicConsts.BUNDLE_DISPLAY_WHAT:

                    String dataText[] = msg.obj.toString().split(",");
                    text_heater_current.setText(dataText[0]);
                    text_heater_current_b.setText(dataText[1]);
                    text_bed_current.setText(dataText[2]);

                    float temperature = Float.parseFloat(dataText[3]);
                    float bed_temperature_f = Float.parseFloat(dataText[4]);
                    progress_heater.setProgress((int) temperature);
                    progress_heater_b.setProgress((int) temperature);
                    progress_bed.setProgress((int) bed_temperature_f);

                    break;
                case PublicConsts.TEXT_VERSION_WHAT:
                    text_version.setText(msg.obj.toString());
                    break;
                case PublicConsts.BEGIN_PRINT:
                    printManager.BeginPrint();
                    break;
                default:
                    break;
            }
        }

    };

    private void caseTen() {

        if (ProcessCharacterUtil.isNumber(mApplication.configManager.heater_current_a) &&
                ProcessCharacterUtil.isNumber(mApplication.bed_current) &&
                ProcessCharacterUtil.isNumber(mApplication.configManager.heater_current_b)) {
            float heater_temperature_f = Float.valueOf(mApplication.configManager.heater_current_a);
            int heater_temperature_i = (int) heater_temperature_f;
            progress_heater.setProgress(heater_temperature_i);
            float bed_temperature_f = Float.valueOf(mApplication.bed_current);
            int bed_temperature_i = (int) bed_temperature_f;
            progress_bed.setProgress(bed_temperature_i);

            heater_temperature_f = Float.valueOf(mApplication.configManager.heater_current_b);
            heater_temperature_i = (int) heater_temperature_f;
            progress_heater_b.setProgress(heater_temperature_i);

            text_bed_current.setText(mApplication.bed_current);
            text_bed_target.setText(mApplication.bed_dest);
            text_heater_current.setText(mApplication.configManager.heater_current_a);
            text_heater_target.setText(mApplication.heater_dest_a);
        }
    }

    private MechanicalStatusReceiver mechanicalStatusReceiver;

    private void registerMechanicalStatusBroadCast() {

        registerReceiver(mechanicalStatusReceiver, RegisterBroadCastManager.getMechanicalStatusFilter());

    }

    private void modifyDisPlay() {
        temperaturestatus = true;
        progress_print.setProgress(0);
        btn_heater_mius.setEnabled(false);
        btn_heater_plus.setEnabled(false);
        btn_heater_mius_b.setEnabled(false);
        btn_heater_plus_b.setEnabled(false);
        btn_bed_mius.setEnabled(false);
        btn_bed_plus.setEnabled(false);
        btn_in.setEnabled(true);
        btn_out.setEnabled(true);
        text_used_time.setText(TimeUtils.GetUsedTime(0));
        btn_x_mius.setEnabled(true);
        btn_x_plus.setEnabled(true);
        btn_y_mius.setEnabled(true);
        btn_y_plus.setEnabled(true);
        btn_z_mius.setEnabled(true);
        btn_z_plus.setEnabled(true);
        btn_unfinish.setEnabled(true);
        btn_play.setBackgroundResource(R.drawable.btn_play);
        btn_backorigin.setEnabled(true);
        btn_file.setEnabled(true);
        btn_usb.setEnabled(true);
        btn_setup.setEnabled(true);
        btn_download.setEnabled(true);
    }

    private void printedDown() {
        text_filename.setText(mApplication.configManager.unfilename + "已完成打印");
        progress_print.setProgress(mApplication.configManager.gcodelength + 100);
        mApplication.configManager.current_print = 0;
        playorpause = false;
        playstatus = false;
        timestatus = false;
        temperaturestatus = true;


        ioService.writeTOSerialPort("M114\n");
        clearLogList();
        mApplication.configManager.unfilename = "";
        mApplication.configManager.unfilepath = "";
        mApplication.configManager.percent = "";
        mApplication.filepath = "";
        mApplication.list_line = 0;
        mApplication.get_line = 0;
        mApplication.configManager.printlist.clear();
        btn_play.setBackgroundResource(R.drawable.btn_play);
        btn_heater_mius.setEnabled(false);
        btn_heater_plus.setEnabled(false);
        btn_heater_mius_b.setEnabled(false);
        btn_heater_plus_b.setEnabled(false);
        btn_bed_mius.setEnabled(false);
        btn_bed_plus.setEnabled(false);
        btn_in.setEnabled(true);
        btn_out.setEnabled(true);
        btn_x_mius.setEnabled(true);
        btn_x_plus.setEnabled(true);
        btn_y_mius.setEnabled(true);
        btn_y_plus.setEnabled(true);
        btn_z_mius.setEnabled(true);
        btn_z_plus.setEnabled(true);
        btn_unfinish.setEnabled(true);
        btn_backorigin.setEnabled(true);
        btn_file.setEnabled(true);
        btn_usb.setEnabled(true);
        btn_setup.setEnabled(true);
        btn_download.setEnabled(true);

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String filepath = data.getStringExtra(FileChooser.EXTRA_FILEPATH);
                if (filepath != null) {
                    mApplication.configManager.gcodelength = 0;
                    mApplication.filepath = filepath;
                    ReadAsset(filepath);
                    btn_play.setBackgroundResource(R.drawable.btn_play);
                }
            }
        }

    }

    private GetAssetFileThread getAssetFileThread;
    private Dialog loadDialog;

    public void ReadAsset(String filepath) {
        loadDialog = myDialog.getLoadingDialog("正在加载文件，请稍候……");
        loadDialog.show();
        getAssetFileThread = new GetAssetFileThread(this, UIHandler, mApplication, filepath);
        getAssetFileThread.start();
    }

    //  notification from different resource: 1 normal 2 faults
    public void PressPlay() {
        if (TextUtils.isEmpty(mApplication.filepath)) {

            myDialog.getNotifiCationDialog(SweetAlertDialog.WARNING_TYPE, "note", "请先选择需要打印的文件！").show();

        } else {

            if (!playorpause) {  //PLAY FALSE
                if (mApplication.BlockMaterialFaults) {
                    mApplication.fromFaults=!mApplication.fromFaults;
                    mApplication.BlockMaterialFaults =!mApplication.BlockMaterialFaults;
                    return;
                }
                if (!gatestatus) {
                    myDialog.getNotifiCationDialog(SweetAlertDialog.WARNING_TYPE, "note", "请关闭安全门！").show();
                } else {
                    if (mApplication.hasFaults()) {
                        return;
                    }
                    if (mApplication.fromFaults){
                        return;
                    }

                    if(mApplication.closedDoor){
                        mApplication.closedDoor=!mApplication.closedDoor;
                        return;
                    }
                    Play();
                }
            } else {//PLAYING

                if (pauseorgooon) {

                    if (!gatestatus) {
                        myDialog.getNotifiCationDialog(SweetAlertDialog.WARNING_TYPE, "note", "请关闭安全门！").show();
                    }
                    if (mApplication.hasFaults()) {
                        return;
                    }

                    Pause();
                    pauseorgooon = !pauseorgooon;

                } else {
                    if (!gatestatus) {
                        myDialog.getNotifiCationDialog(SweetAlertDialog.WARNING_TYPE, "note", "请关闭安全门！").show();
                    } else {
                        if (mApplication.hasFaults()) {
                            return;
                        }
                        Goon();
                        pauseorgooon = !pauseorgooon;
                    }
                }
            }
        }
    }

    public void Play() {
        UIHandler.sendEmptyMessage(8);
        iscansend = true;
        playorpause = true;
        playstatus = true;
        temperaturestatus = false;
        btn_play.setBackgroundResource(R.drawable.btn_pause);

        //add fan on
        SendCommand("M106 \n");
        text_x.setText("--");
        text_y.setText("--");
        text_z.setText("--");
        text_used_time.setText(TimeUtils.GetUsedTime(0));
        btn_heater_mius.setEnabled(true);
        btn_heater_plus.setEnabled(true);
        btn_heater_mius_b.setEnabled(true);
        btn_heater_plus_b.setEnabled(true);
        btn_bed_mius.setEnabled(true);
        btn_bed_plus.setEnabled(true);
        btn_in.setEnabled(false);
        btn_out.setEnabled(false);
        btn_x_mius.setEnabled(false);
        btn_x_plus.setEnabled(false);
        btn_y_mius.setEnabled(false);
        btn_y_plus.setEnabled(false);
        btn_z_mius.setEnabled(false);
        btn_z_plus.setEnabled(false);
        btn_unfinish.setEnabled(false);
        btn_backorigin.setEnabled(false);
        btn_file.setEnabled(false);
        btn_usb.setEnabled(false);
        btn_setup.setEnabled(false);
        btn_download.setEnabled(false);
        timestatus = true;
        recLen = 0;

        printManager.BeginPrint();
    }

    public void Pause() {
        btn_play.setBackgroundResource(R.drawable.btn_continue);
        pausestatus = true;
        timestatus = false;
        ioService.writeTOSerialPort("M114\n");
        SendCommand("M114 \n");

        String printlog = "发送  " + TimeUtils.getNowTime() + "  " + "pauseM114\n";

        mApplication.loglist.add(printlog);
        //add fan_off
        SendCommand("M107 \n");

        if (mApplication.loglist.size() == 1000) {
            clearLogList();
        }
        btn_x_mius.setEnabled(true);
        btn_x_plus.setEnabled(true);
        btn_y_mius.setEnabled(true);
        btn_y_plus.setEnabled(true);
        btn_in.setEnabled(true);
        btn_out.setEnabled(true);
    }

    public void Goon() {
        btn_play.setBackgroundResource(R.drawable.btn_pause);
        pausestatus = false;
        timestatus = true;
        String G1 = "G1 F9000 X" + pause_x + " Y" + pause_y + " Z" + pause_z + " E" + pause_e + "\n";

        ioService.writeTOSerialPort(G1);
        mApplication.loglist.add("手动" + G1);
        if (mApplication.loglist.size() == 1000) {
            clearLogList();
        }
        text_x.setText("--");
        text_y.setText("--");
        text_z.setText("--");
        btn_x_mius.setEnabled(false);
        btn_x_plus.setEnabled(false);
        btn_y_mius.setEnabled(false);
        btn_y_plus.setEnabled(false);
        btn_in.setEnabled(false);
        btn_out.setEnabled(false);
    }


    public void clearLogList() {
        if (mApplication.loglist.size() > 0) {
            mApplication.loglist.clear();
        }
    }

    public void SendCommand(final String message) {
        ioService.writeTOSerialPort(message);
        String printLog = "发送  " + message;
        mApplication.loglist.add(printLog);
        if (mApplication.loglist.size() == 1000) {
            clearLogList();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            myDialog.getOptionDialog(SweetAlertDialog.WARNING_TYPE, "quit", "Are you sure you want to quit this application",
                    "sure", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {

                            mApplication.closeSerialPort();
                            ioService.mSerialPort = null;
                            finish();
                        }
                    }).show();

        }

        return super.onKeyDown(keyCode, event);

    }

    public void changeHeadNum() {
        if (this.mApplication.configManager.head_num == 1) {

            btn_heater_mius_b.setVisibility(View.INVISIBLE);
            btn_heater_plus_b.setVisibility(View.INVISIBLE);
            text_heater_current_b.setVisibility(View.INVISIBLE);
            text_heater_target_b.setVisibility(View.INVISIBLE);
            progress_heater_b.setVisibility(View.INVISIBLE);
            rb_executorb.setVisibility(View.INVISIBLE);
            CurrentExcutor = 1;
            ExcutorHeatControlName = "T0";
            ExcutorInOutControlName = "T0";
            text_heater_current_degree_b.setVisibility(View.INVISIBLE);
            text_heater_target_degree_b.setVisibility(View.INVISIBLE);
            text_heater_target_hint_b.setVisibility(View.INVISIBLE);
            text_heater_current_hint_b.setVisibility(View.INVISIBLE);

            img_heater_b.setVisibility(View.INVISIBLE);

            btn_inout_b.setVisibility(View.INVISIBLE);
        } else if (this.mApplication.configManager.head_num == 2) {
            btn_heater_mius_b.setVisibility(View.VISIBLE);
            btn_heater_plus_b.setVisibility(View.VISIBLE);

            btn_heater_mius_b.setVisibility(View.VISIBLE);
            btn_heater_plus_b.setVisibility(View.VISIBLE);
            text_heater_current_b.setVisibility(View.VISIBLE);
            text_heater_target_b.setVisibility(View.VISIBLE);
            progress_heater_b.setVisibility(View.VISIBLE);
            rb_executorb.setVisibility(View.VISIBLE);
            text_heater_current_degree_b.setVisibility(View.VISIBLE);
            text_heater_target_degree_b.setVisibility(View.VISIBLE);
            text_heater_target_hint_b.setVisibility(View.VISIBLE);
            text_heater_current_hint_b.setVisibility(View.VISIBLE);
            img_heater_b.setVisibility(View.VISIBLE);
            btn_inout_b.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        changeHeadNum();
    }
}
