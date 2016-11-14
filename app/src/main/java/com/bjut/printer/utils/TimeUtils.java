package com.bjut.printer.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by johnsmac on 9/13/16.
 */
public class TimeUtils {

    public static String getNowTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        return str;
    }
    public static String GetUsedTime(int usedtime) {
        int hour = usedtime / 3600;
        int minute = (usedtime - hour * 3600) / 60;
        int second = usedtime - hour * 3600 - minute * 60;
        String time = String.format("%02d:%02d:%02d", hour, minute, second);
        return time;
    }
}
