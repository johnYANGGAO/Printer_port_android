package com.bjut.printer.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.bjut.printer.Consts.PublicConsts;
import com.bjut.printer.Operator.SendBroadCast;
import com.bjut.printer.Views.MyDialog;
import com.bjut.printer.application.MyApplication;
import com.bjut.printer.bean.DataOfSetUpAct;
import com.bjut.printer.bean.Setting;
import com.bjut.printer.bean.WorkLog;
import com.bjut.printer.services.CheckOrDownloadIntentService;
import com.bjut.printer.utils.NetWorkUtil;
import com.bjut.printer2.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SetUpActivity extends BaseActivity {
    public MyApplication mApplication;
    private SharedPreferences.Editor sharedata;
    public Button btn_back, btn_leveling, btn_restore_defaults, btn_update;
    private TextView text_pla_heater_temperature, text_pla_bed_temperature;
    private Button btn_pla_gear, btn_abs_gear, btn_head_num;
    private TextView text_abs_heater_temperature, text_abs_bed_temperature;
    private GridView gridview;
    public MyAdapter myadapter;
    private PopupWindow modewindow, modifywindow, headnumindow;
    private TextView text_mode, text_name, text_unit, text_current_head_num;
    private EditText edit_heater, edit_bed, edit_value;
    private Button btn_modify, btn_goback;
    private int mode;
    private RadioButton rb_head_num, rb_head_num1, rb_head_num2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.setup);
        mApplication = (MyApplication) getApplication();
        mApplication.mSettingFragment = this;
        sharedata = getSharedPreferences("data", 0).edit();
        DataOfSetUpAct.getSettingListData(this, mApplication);
        FindViews();
        SetListener();
        Initmodifywindow();
        Initmodewindow();
        Initheadnumindow();
    }


    public void FindViews() {

        btn_back = (Button) findViewById(R.id.btn_back);

        btn_leveling = (Button) findViewById(R.id.btn_leveling);
        btn_restore_defaults = (Button) findViewById(R.id.btn_restore_defaults);
        btn_update = (Button) findViewById(R.id.btn_update);

        text_pla_heater_temperature = (TextView) findViewById(R.id.text_pla_heater_temperature);
        text_pla_bed_temperature = (TextView) findViewById(R.id.text_pla_bed_temperature);
        btn_pla_gear = (Button) findViewById(R.id.btn_pla_gear);

        text_abs_heater_temperature = (TextView) findViewById(R.id.text_abs_heater_temperature);
        text_abs_bed_temperature = (TextView) findViewById(R.id.text_abs_bed_temperature);
        btn_abs_gear = (Button) findViewById(R.id.btn_abs_gear);
        btn_head_num = (Button) findViewById(R.id.btn_head_num);
        text_current_head_num = (TextView) findViewById(R.id.text_current_head_num);

        text_current_head_num.setText(mApplication.configManager.head_num + "");

        gridview = (GridView) findViewById(R.id.gv_gridview);
        myadapter = new MyAdapter();

    }


    public void SetListener() {

        btn_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_update.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {


                if (NetWorkUtil.networkCheck(SetUpActivity.this)) {

                    CheckOrDownloadIntentService.startActionCheck(SetUpActivity.this, "", "");

                } else {

//                    SendBroadCast.sendError(SetUpActivity.this, "未搜索到网络");
                    myDialog.getNotifiCationDialog(SweetAlertDialog.ERROR_TYPE, "oops",
                            "请检查网络连接！").show();
                }
            }
        });

        btn_leveling.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {


                myDialog.getOptionDialog(SweetAlertDialog.NORMAL_TYPE, "note", "确定要进行调平吗?", "OK", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.cancel();
                        if (!mApplication.mMainActivity.gatestatus) {

                            myDialog.getNotifiCationDialog(SweetAlertDialog.WARNING_TYPE, "note", "开门状态下 方可调平！").show();
                            return;
                        }

                        mApplication.mMainActivity.SendCommand("G28\n");
                        mApplication.mMainActivity.SendCommand("G1 X163 Y180 Z100\n");
                        // jumping from this to another
                        Intent intent = new Intent(SetUpActivity.this, LevelingActivity.class);
                        startActivity(intent);
                    }
                }).show();
//
            }
        });

        btn_restore_defaults.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {


                myDialog.getOptionDialog(SweetAlertDialog.WARNING_TYPE, "note", "确定要恢复出厂设置吗?", "OK", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        mApplication.restoreConfig();
                        mApplication.settinglist.clear();
                        DataOfSetUpAct.getSettingListData(SetUpActivity.this, mApplication);
                    }
                }).show();

            }
        });

        text_pla_heater_temperature.setText(mApplication.configManager.pla_heater_temperature);
        text_pla_bed_temperature.setText(mApplication.configManager.pla_bed_temperature);

        btn_pla_gear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                modewindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                mode = 0;
                text_mode.setText("PLA模式");
                edit_heater.setText(mApplication.configManager.pla_heater_temperature);
                edit_bed.setText(mApplication.configManager.pla_bed_temperature);
            }
        });
        btn_head_num.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                headnumindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                if (mApplication.configManager.head_num == 1) {
                    rb_head_num1.setChecked(true);
                    rb_head_num = rb_head_num1;

                }
                if (mApplication.configManager.head_num == 2) {
                    rb_head_num2.setChecked(true);
                    rb_head_num = rb_head_num2;

                }
                // rb_head_num1= (RadioButton) view.findViewById(R.id.radio1);
                // rb_head_num2= (RadioButton) view.findViewById(R.id.radio2);
            }
        });

        text_abs_heater_temperature.setText(mApplication.configManager.abs_heater_temperature);
        text_abs_bed_temperature.setText(mApplication.configManager.abs_bed_temperature);

        btn_abs_gear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                modewindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                mode = 1;
                text_mode.setText("ABS模式");
                edit_heater.setText(mApplication.configManager.abs_heater_temperature);
                edit_bed.setText(mApplication.configManager.abs_bed_temperature);

            }
        });

        gridview.setAdapter(myadapter);

    }

    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mApplication.settinglist.size();
        }

        @Override
        public Object getItem(int position) {
            return mApplication.settinglist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            HoldView holder = null;
            if (convertView == null) {
                holder = new HoldView();
                LayoutInflater inflater = LayoutInflater.from(SetUpActivity.this);
                convertView = inflater.inflate(R.layout.grid_item, null);
                holder.text_name = (TextView) convertView.findViewById(R.id.text_name);
                holder.text_value = (TextView) convertView.findViewById(R.id.text_value);
                holder.text_unit = (TextView) convertView.findViewById(R.id.text_unit);
                holder.text_range = (TextView) convertView.findViewById(R.id.text_range);
                holder.btn_gear = (Button) convertView.findViewById(R.id.btn_gear);
                convertView.setTag(holder);
            } else {
                holder = (HoldView) convertView.getTag();
            }
            Setting setting = mApplication.settinglist.get(position);
            holder.text_name.setText(setting.getName());
            holder.text_value.setText(setting.getValue());
            holder.text_unit.setText(setting.getUnit());
            holder.text_range.setText(setting.getRang());
            holder.btn_gear.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    modifywindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                    final Setting setting = mApplication.settinglist.get(position);
                    text_name.setText(setting.getName());
                    edit_value.setText(setting.getValue());
                    text_unit.setText(setting.getUnit());
                    btn_modify.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (setting.getId() == 1) {
                                ModifyParameter("M207 S", 21);
                            } else if (setting.getId() == 2) {
                                ModifyParameter("M207 F", 22);
                            } else if (setting.getId() == 3) {
                                ModifyParameter("M207 Z", 23);
                            } else if (setting.getId() == 4) {
                                ModifyParameter("M204 T", 24);
                            } else if (setting.getId() == 5) {
                                mApplication.configManager.extruder_test_step = edit_value.getText().toString();
                                mApplication.settinglist.get(4).setValue(mApplication.configManager.extruder_test_step);
                                sharedata.putString("extruder_test_step", mApplication.configManager.extruder_test_step);
                                sharedata.commit();
                                myadapter.notifyDataSetChanged();
                            } else if (setting.getId() == 6) {
                                ModifyParameter("G1 F", 26);
                            } else if (setting.getId() == 7) {
                                ModifyParameter("M301 P", 27);
                            } else if (setting.getId() == 8) {
                                ModifyParameter("M301 I", 28);
                            } else if (setting.getId() == 9) {
                                ModifyParameter("M301 D", 29);
                            } else if (setting.getId() == 10) {
                                ModifyParameter("M221 S", 30);
                            } else if (setting.getId() == 11) {
                                ModifyParameter("M204 S", 31);
                            } else if (setting.getId() == 12) {
                                ModifyParameter("M203 X", 32);
                            } else if (setting.getId() == 13) {
                                ModifyParameter("M203 Y", 33);
                            } else if (setting.getId() == 14) {
                                ModifyParameter("M203 Z", 34);
                            } else if (setting.getId() == 15) {
                                ModifyParameter("M203 E", 35);
                            } else if (setting.getId() == 16) {
                                ModifyParameter("M201 X", 36);
                            } else if (setting.getId() == 17) {
                                ModifyParameter("M201 Y", 37);
                            } else if (setting.getId() == 18) {
                                ModifyParameter("M201 Z", 38);
                            } else if (setting.getId() == 19) {
                                ModifyParameter("M201 E", 39);
                            } else if (setting.getId() == 20) {
                                ModifyParameter("M92 X", 40);
                            } else if (setting.getId() == 21) {
                                ModifyParameter("M92 Y", 41);
                            } else if (setting.getId() == 22) {
                                ModifyParameter("M92 Z", 42);
                            } else if (setting.getId() == 23) {
                                ModifyParameter("M92 E", 43);
                            }
                            modifywindow.dismiss();
                        }
                    });
                }
            });
            return convertView;
        }

        class HoldView {
            TextView text_name;
            TextView text_value;
            TextView text_unit;
            TextView text_range;
            Button btn_gear;
        }
    }


    private void Initheadnumindow() {

        LayoutInflater inflater = LayoutInflater.from(SetUpActivity.this);
        // 引入窗口配置文件
        final View view = inflater.inflate(R.layout.headnumwindow, null);
        // 创建PopupWindow对象
        headnumindow = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);
        Button btn_modify = (Button) view.findViewById(R.id.btn_modify);
        Button btn_goback = (Button) view.findViewById(R.id.btn_goback);
        rb_head_num1 = (RadioButton) view.findViewById(R.id.radio1);
        rb_head_num2 = (RadioButton) view.findViewById(R.id.radio2);
        rb_head_num = rb_head_num1;
        RadioGroup group = (RadioGroup) view.findViewById(R.id.radioGroup);
        //绑定一个匿名监听器
        group.setOnCheckedChangeListener(new OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                // TODO Auto-generated method stub
                //获取变更后的选中项的ID
                int radioButtonId = arg0.getCheckedRadioButtonId();
                //根据ID获取RadioButton的实例

                //更新文本内容，以符合选中项
                //  tv.setText("您的性别是：" + rb.getText());
            }


        });

        btn_modify.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                text_current_head_num.setText(rb_head_num.getText());
                int head_num = Integer.valueOf((String) rb_head_num.getText());
                mApplication.configManager.head_num = head_num;
                sharedata.putInt("head_num", mApplication.configManager.head_num);
                sharedata.commit();
                headnumindow.dismiss();
            }
        });

        btn_goback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                headnumindow.dismiss();
            }
        });
    }

    //TODO POPUP
    private void Initmodewindow() {

        LayoutInflater inflater = LayoutInflater.from(SetUpActivity.this);
        // 引入窗口配置文件
        final View view = inflater.inflate(R.layout.modewindow, null);
        // 创建PopupWindow对象
        modewindow = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);

        text_mode = (TextView) view.findViewById(R.id.text_mode);
        edit_heater = (EditText) view.findViewById(R.id.edit_heater);
        edit_bed = (EditText) view.findViewById(R.id.edit_bed);
        Button btn_modify = (Button) view.findViewById(R.id.btn_modify);
        Button btn_goback = (Button) view.findViewById(R.id.btn_goback);

        btn_modify.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mode == 0) {
                    mApplication.configManager.pla_heater_temperature = edit_heater.getText().toString();
                    mApplication.configManager.pla_bed_temperature = edit_bed.getText().toString();
                    sharedata.putString("pla_heater_temperature", mApplication.configManager.pla_heater_temperature);
                    sharedata.putString("pla_bed_temperature", mApplication.configManager.pla_bed_temperature);
                    sharedata.commit();
                } else if (mode == 1) {
                    mApplication.configManager.abs_heater_temperature = edit_heater.getText().toString();
                    mApplication.configManager.abs_bed_temperature = edit_bed.getText().toString();
                    sharedata.putString("abs_heater_temperature", mApplication.configManager.abs_heater_temperature);
                    sharedata.putString("abs_bed_temperature", mApplication.configManager.abs_bed_temperature);
                    sharedata.commit();
                }

            }
        });

        btn_goback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                modewindow.dismiss();
            }
        });
    }

    private void Initmodifywindow() {

        LayoutInflater inflater = LayoutInflater.from(SetUpActivity.this);
        // 引入窗口配置文件
        final View view = inflater.inflate(R.layout.modifywindow, null);
        // 创建PopupWindow对象
        modifywindow = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);

        text_name = (TextView) view.findViewById(R.id.text_name);
        edit_value = (EditText) view.findViewById(R.id.edit_value);
        text_unit = (TextView) view.findViewById(R.id.text_unit);
        btn_modify = (Button) view.findViewById(R.id.btn_modify);
        btn_goback = (Button) view.findViewById(R.id.btn_goback);

        btn_goback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                modifywindow.dismiss();
            }
        });
    }

    private void ModifyParameter(String order, int type) {
        mApplication.mMainActivity.SendCommand(order + edit_value.getText().toString() + "\n");
        mApplication.mMainActivity.SendCommand("M500\n");
        WorkLog worklog = new WorkLog();
        mApplication.worklog_no++;
        worklog.setId(mApplication.worklog_no);
        worklog.setSenddata(order + edit_value.getText().toString() + "\n");
        worklog.setType(type);
        worklog.setValue(edit_value.getText().toString());
        mApplication.workloglist.add(worklog);
    }
}