package com.bjut.printer.application;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;

import com.bjut.printer.ApplicationConfig.ConfigManager;
import com.bjut.printer.activity.MainActivity;
import com.bjut.printer.activity.SetUpActivity;
import com.bjut.printer.bean.Setting;
import com.bjut.printer.bean.WorkLog;
//import com.tencent.bugly.crashreport.CrashReport;
//import com.tencent.bugly.crashreport.common.strategy.StrategyBean;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import android_serialport_api.SerialPort;

public class MyApplication extends android.app.Application {

    public static Context mContext;
    public ArrayList<String> loglist = new ArrayList();
    public MainActivity mMainActivity;
    public String udid ,speed,pause_x,pause_y,pause_z,pause_e,pause_target_z;
    public int initial=1;
    public String target_heater_temperature_a,target_heater_temperature_b,target_bed_temperature;
    public boolean pauseplace,playstatus,pausestatus,goonstatus;
    public SetUpActivity mSettingFragment;
    public ArrayList<WorkLog> workloglist = new ArrayList<WorkLog>();
    public ArrayList<Setting> settinglist = new ArrayList<Setting>();
    private SerialPort mSerialPort = null;
    public String strPathHead = null;
//    public float consumable = (float) 330.00;
    public int worklog_no = -1;
    public String current_x, current_y, current_z;
    public float current_e;
    public float current_e_b;
    public String heater_dest_a, heater_dest_b;
    public String bed_current, bed_dest;
    public String filepath;
    public int get_line;
    public int list_line ;
    public String m_szAndroidID;
    public int usbstatus = 0;
    //作为全局对象
    public ConfigManager configManager;
    //we must use type boolean (default value is false)
    // because Boolean's default value is null;
    public boolean hasPowerFaults;
    public boolean hasSupplyFaults;
    public boolean BlockMaterialFaults;
    public boolean fromFaults;
    public boolean closedDoor;
//    public String getStrPathHead() {
//        return strPathHead;
//    }
//
//    public void setStrPathHead(String strPathHead) {
//        this.strPathHead = strPathHead;
//    }

    public Boolean hasFaults(){

        if(hasPowerFaults||hasSupplyFaults){
            return true;
        }
        return false;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mContext=getApplicationContext();

//        CrashReport.initCrashReport(this, "900012606", false);//provided by tenCent ,but i don't want it

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            strPathHead = Environment.getExternalStorageDirectory().toString();
        } else {
            strPathHead = getFilesDir().getPath();
        }
        File destDir = new File(strPathHead + "/Gcode");

        //create a file directory but not be used here just make a direct
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        // ConfigManager  will be used in another class
        configManager=new ConfigManager(this);
        //initialize parameters
        configManager.getDefaultConfig();
    }


    // reset config with  default parameter
    public void restoreConfig() {
        configManager.recoveryDefaultConfig();
    }

    public SerialPort getSerialPort(Activity activity) throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPort == null) {
            /** Read serial port parameters
             in fact ,we always retrieve the default value until set value somewhere using editor
            */
            SharedPreferences sp = activity.getSharedPreferences("OME", MODE_PRIVATE);
            String path = sp.getString("DEVICE", "/dev/ttySAC3");
            int baud_rate = Integer.decode(sp.getString("BAUD", "115200"));
            Log.i("SerialPort","preference's path -->"+path+" --and baud rate-->"+baud_rate);
            /* Check parameters */
            if ((path.length() == 0) || (baud_rate == -1)) {
                throw new InvalidParameterException();
            }

			/* Open the serial port */
            mSerialPort = new SerialPort(new File(path), baud_rate, 0);
        }
        return mSerialPort;
    }


    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }
}
