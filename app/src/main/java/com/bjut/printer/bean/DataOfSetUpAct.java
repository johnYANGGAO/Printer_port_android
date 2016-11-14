package com.bjut.printer.bean;

import android.content.Context;

import com.bjut.printer.application.MyApplication;
import com.bjut.printer2.R;

import java.util.ArrayList;

/**
 * Created by johnsmac on 9/12/16.
 */
public class DataOfSetUpAct {


    public static void getSettingListData(Context context, MyApplication mApplication) {

        Setting setting1 = new Setting();
        setting1.setId(1);
        setting1.setName(context.getString(R.string.retract_length));
        setting1.setValue(mApplication.configManager.retract_length);
        setting1.setUnit("mm");
        setting1.setRang("0.0~9.9mm");
        mApplication.settinglist.add(setting1);

        Setting setting2 = new Setting();
        setting2.setId(2);
        setting2.setName(context.getString(R.string.retract_feedrate));
        setting2.setValue(mApplication.configManager.retract_feedrate);
        setting2.setRang("0~2000");
        mApplication.settinglist.add(setting2);

        Setting setting3 = new Setting();
        setting3.setId(3);
        setting3.setName("Z-lift/hop");
        setting3.setValue(mApplication.configManager.Z_lift);
        setting3.setUnit("mm");
        setting3.setRang("0.0~3.0mm");
        mApplication.settinglist.add(setting3);

        Setting setting4 = new Setting();
        setting4.setId(4);
        setting4.setName(context.getString(R.string.accel_retract));
        setting4.setValue(mApplication.configManager.accel_retract);
        setting4.setRang("100~99000");
        mApplication.settinglist.add(setting4);

        Setting setting5 = new Setting();
        setting5.setId(5);
        setting5.setName(context.getString(R.string.extruder_test_step));
        setting5.setValue(mApplication.configManager.extruder_test_step);
        setting5.setUnit("mm");
        setting5.setRang("0~10mm");
        mApplication.settinglist.add(setting5);

        Setting setting6 = new Setting();
        setting6.setId(6);
        setting6.setName(context.getString(R.string.extruder_test_feedrate));
        setting6.setValue(mApplication.configManager.extruder_test_feedrate);
        setting6.setRang("0~1200");
        mApplication.settinglist.add(setting6);

        Setting setting7 = new Setting();
        setting7.setId(7);
        setting7.setName("PID-P");
        setting7.setValue(mApplication.configManager.pid_p);
        setting7.setRang("1.00~9990.00");
        mApplication.settinglist.add(setting7);

        Setting setting8 = new Setting();
        setting8.setId(8);
        setting8.setName("PID-I");
        setting8.setValue(mApplication.configManager.pid_i);
        setting8.setRang("0.10~9990.00");
        mApplication.settinglist.add(setting8);

        Setting setting9 = new Setting();
        setting9.setId(9);
        setting9.setName("PID-D");
        setting9.setValue(mApplication.configManager.pid_d);
        setting9.setRang("1.00~9990.00");
        mApplication.settinglist.add(setting9);

        Setting setting10 = new Setting();
        setting10.setId(10);
        setting10.setName(context.getString(R.string.flow));
        setting10.setValue(mApplication.configManager.flow);
        setting10.setUnit("%");
        setting10.setRang("10~400%");
        mApplication.settinglist.add(setting10);

        Setting setting11 = new Setting();
        setting11.setId(11);
        setting11.setName(context.getString(R.string.accel));
        setting11.setValue(mApplication.configManager.accel);
        setting11.setRang("500~99000");
        mApplication.settinglist.add(setting11);

        Setting setting12 = new Setting();
        setting12.setId(12);
        setting12.setName(context.getString(R.string.v_x));
        setting12.setValue(mApplication.configManager.v_x);
        setting12.setRang("1~999");
        mApplication.settinglist.add(setting12);

        Setting setting13 = new Setting();
        setting13.setId(13);
        setting13.setName(context.getString(R.string.v_y));
        setting13.setValue(mApplication.configManager.v_y);
        setting13.setRang("1~999");
        mApplication.settinglist.add(setting13);

        Setting setting14 = new Setting();
        setting14.setId(14);
        setting14.setName(context.getString(R.string.v_z));
        setting14.setValue(mApplication.configManager.v_z);
        setting14.setRang("1~999");
        mApplication.settinglist.add(setting14);

        Setting setting15 = new Setting();
        setting15.setId(15);
        setting15.setName(context.getString(R.string.v_e));
        setting15.setValue(mApplication.configManager.v_e);
        setting15.setRang("1~999");
        mApplication.settinglist.add(setting15);

        Setting setting16 = new Setting();
        setting16.setId(16);
        setting16.setName(context.getString(R.string.a_x));
        setting16.setValue(mApplication.configManager.a_x);
        setting16.setRang("100~99000");
        mApplication.settinglist.add(setting16);

        Setting setting17 = new Setting();
        setting17.setId(17);
        setting17.setName(context.getString(R.string.a_y));
        setting17.setValue(mApplication.configManager.a_y);
        setting17.setRang("100~99000");
        mApplication.settinglist.add(setting17);

        Setting setting18 = new Setting();
        setting18.setId(18);
        setting18.setName(context.getString(R.string.a_z));
        setting18.setValue(mApplication.configManager.a_z);
        setting18.setRang("100~99000");
        mApplication.settinglist.add(setting18);

        Setting setting19 = new Setting();
        setting19.setId(19);
        setting19.setName(context.getString(R.string.a_e));
        setting19.setValue(mApplication.configManager.a_e);
        setting19.setRang("1~999");
        mApplication.settinglist.add(setting19);

        Setting setting20 = new Setting();
        setting20.setId(20);
        setting20.setName(context.getString(R.string.steps_x));
        setting20.setValue(mApplication.configManager.steps_x);
        setting20.setRang("5.00~9999.00");
        mApplication.settinglist.add(setting20);

        Setting setting21 = new Setting();
        setting21.setId(21);
        setting21.setName(context.getString(R.string.steps_y));
        setting21.setValue(mApplication.configManager.steps_y);
        setting21.setRang("5.00~9999.00");
        mApplication.settinglist.add(setting21);

        Setting setting22 = new Setting();
        setting22.setId(22);
        setting22.setName(context.getString(R.string.steps_z));
        setting22.setValue(mApplication.configManager.steps_z);
        setting22.setRang("5.00~9999.00");
        mApplication.settinglist.add(setting22);

        Setting setting23 = new Setting();
        setting23.setId(23);
        setting23.setName(context.getString(R.string.steps_e));
        setting23.setValue(mApplication.configManager.steps_e);
        setting23.setRang("5.00~9999.00");
        mApplication.settinglist.add(setting23);

    }

}
