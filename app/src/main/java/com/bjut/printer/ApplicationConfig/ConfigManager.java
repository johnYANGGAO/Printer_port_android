package com.bjut.printer.ApplicationConfig;

import android.content.Context;
import android.content.SharedPreferences;

import com.bjut.printer.Consts.ConfigConsts;

import java.util.ArrayList;

/**
 * Created by johnsmac on 9/12/16.
 */
public class ConfigManager {

    private Context context;
    public int print_mode = 1;
    public int head_num = 2;
    private SharedPreferences sharedata;
    private SharedPreferences.Editor sharedataeditor;
    public String print_speed;
    public String pla_heater_temperature, pla_bed_temperature;
    public String abs_heater_temperature, abs_bed_temperature;
    public String retract_length, retract_feedrate, Z_lift, accel_retract;
    public String extruder_test_step, extruder_test_feedrate;
    public String pid_p, pid_i, pid_d;
    public String flow, accel;
    public String v_x, v_y, v_z, v_e;
    public String a_x, a_y, a_z, a_e;
    public String steps_x, steps_y, steps_z, steps_e;
    public int gcodelength;
    public int current_print = 1;
    public String heater_current_a, heater_current_b;
    public String unfilename, unfilepath, percent;
    public int list_line = 0;
    public ArrayList<String> printlist = new ArrayList<String>();


    public ConfigManager(Context mContext) {

        this.context = mContext;

    }

    private SharedPreferences getSharedata() {

        return context.getSharedPreferences("data", 0);

    }

    private SharedPreferences.Editor getSharedataeditor() {

        return getSharedata().edit();
    }


    public void recoveryDefaultConfig() {

        if (sharedataeditor == null) {
            sharedataeditor = getSharedataeditor();
        }


        pla_heater_temperature = ConfigConsts.pla_heater_temperature;
        pla_bed_temperature = ConfigConsts.pla_bed_temperature;
        head_num = ConfigConsts.head_num;


        sharedataeditor.putInt("head_num", head_num);
        sharedataeditor.putString("pla_heater_temperature", pla_heater_temperature);
        sharedataeditor.putString("pla_bed_temperature", pla_bed_temperature);


        abs_heater_temperature = ConfigConsts.abs_heater_temperature;
        abs_bed_temperature =ConfigConsts.abs_bed_temperature;
        sharedataeditor.putString("abs_heater_temperature", abs_heater_temperature);
        sharedataeditor.putString("abs_bed_temperature", abs_bed_temperature);


        retract_length = ConfigConsts.retract_length;
        retract_feedrate = ConfigConsts.retract_feedrate;
        Z_lift = ConfigConsts.Z_lift;
        accel_retract = ConfigConsts.accel_retract;
        extruder_test_step = ConfigConsts.extruder_test_step;

        sharedataeditor.putString("retract_length", retract_length);
        sharedataeditor.putString("retract_feedrate", retract_feedrate);
        sharedataeditor.putString("Z_lift", Z_lift);
        sharedataeditor.putString("accel_retract", accel_retract);
        sharedataeditor.putString("extruder_test_step", extruder_test_step);


        extruder_test_feedrate = ConfigConsts.extruder_test_feedrate;
        pid_p = ConfigConsts.pid_p;
        pid_i = ConfigConsts.pid_i;
        pid_d = ConfigConsts.pid_d;

        flow = ConfigConsts.flow;
        accel = ConfigConsts.accel;

        sharedataeditor.putString("extruder_test_feedrate", extruder_test_feedrate);
        sharedataeditor.putString("pid_p", pid_p);
        sharedataeditor.putString("pid_i", pid_i);
        sharedataeditor.putString("pid_d", pid_d);
        sharedataeditor.putString("flow", flow);
        sharedataeditor.putString("accelaccel", accel);

        v_x = ConfigConsts.v_x;
        v_y = ConfigConsts.v_y;
        v_z = ConfigConsts.v_z;
        v_e = ConfigConsts.v_e;
        a_x = ConfigConsts.a_x;
        a_y = ConfigConsts.a_y;
        a_z = ConfigConsts.a_z;
        a_e = ConfigConsts.a_e;
        steps_x = ConfigConsts.steps_x;
        steps_y = ConfigConsts.steps_y;
        steps_z = ConfigConsts.steps_z;
        steps_e = ConfigConsts.steps_e;

        sharedataeditor.putString("v_x", v_x);
        sharedataeditor.putString("v_y", v_y);
        sharedataeditor.putString("v_z", v_z);
        sharedataeditor.putString("v_e", v_e);
        sharedataeditor.putString("a_x", a_x);
        sharedataeditor.putString("a_y", a_y);
        sharedataeditor.putString("a_x", a_x);
        sharedataeditor.putString("a_e", a_e);
        sharedataeditor.putString("steps_x", steps_x);
        sharedataeditor.putString("steps_y", steps_y);
        sharedataeditor.putString("steps_z", steps_z);
        sharedataeditor.putString("steps_e", steps_e);

        sharedataeditor.commit();


    }

    public void getDefaultConfig() {

        sharedata = getSharedata();
        head_num = sharedata.getInt("head_num", 2);
        print_speed = sharedata.getString("print_speed", "100");
        print_mode = sharedata.getInt("print_mode", 1);
        pla_heater_temperature = sharedata.getString("pla_heater_temperature", "200");
        pla_bed_temperature = sharedata.getString("pla_bed_temperature", "50");
        abs_heater_temperature = sharedata.getString("abs_heater_temperature", "230");
        abs_bed_temperature = sharedata.getString("abs_bed_temperature", "75");
        retract_length = sharedata.getString("retract_length", "0");
        retract_feedrate = sharedata.getString("retract_feedrate", "0");
        Z_lift = sharedata.getString("Z_lift", "0");
        accel_retract = sharedata.getString("accel_retract", "0");
        extruder_test_step = sharedata.getString("extruder_test_step", "5");
        extruder_test_feedrate = sharedata.getString("extruder_test_feedrate", "100");
        pid_p = sharedata.getString("pid_p", "7.00");
        pid_i = sharedata.getString("pid_i", "0.10");
        pid_d = sharedata.getString("pid_d", "1.00");
        flow = sharedata.getString("flow", "100");
        accel = sharedata.getString("accel", "3000");
        v_x = sharedata.getString("v_x", "500");
        v_y = sharedata.getString("v_y", "500");
        v_z = sharedata.getString("v_z", "5");
        v_e = sharedata.getString("v_e", "25");
        a_x = sharedata.getString("a_x", "9000");
        a_y = sharedata.getString("a_y", "9000");
        a_z = sharedata.getString("a_z", "100");
        a_e = sharedata.getString("a_e", "10000");
        steps_x = sharedata.getString("steps_x", "80.33");
        steps_y = sharedata.getString("steps_y", "80.33");
        steps_z = sharedata.getString("steps_z", "3200");
        steps_e = sharedata.getString("steps_z", "96.4");

        current_print = sharedata.getInt("current_print", 0);
        gcodelength = sharedata.getInt("gcodelength", 0);
        unfilename = sharedata.getString("unfilename", "");
        unfilepath = sharedata.getString("unfilepath", "");
        percent = sharedata.getString("percent", "");
        heater_current_a = sharedata.getString("heater_current_a", "190");
        heater_current_b = sharedata.getString("heater_current_b", "190");
        list_line = sharedata.getInt("current_print", 0);

        if (printlist.size() == 0) {
            int size = sharedata.getInt("Status_size", 0);
            for (int i = 0; i < size; i++) {
                printlist.add(sharedata.getString("Status_" + i, null));
            }
        }

    }

}
