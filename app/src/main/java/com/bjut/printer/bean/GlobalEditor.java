package com.bjut.printer.bean;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by johnsmac on 9/12/16.
 */
public class GlobalEditor {

    private  static SharedPreferences.Editor sharedataeditor;


     public static synchronized SharedPreferences.Editor getSharedataeditorInstance(Context context){

         if (sharedataeditor == null) {
             sharedataeditor = context.getSharedPreferences("data", 0).edit();
         }
         return sharedataeditor;
     }

    public static void upDateValue(String key, Object value, Context context) {

        if (sharedataeditor == null) {
            sharedataeditor = context.getSharedPreferences("data", 0).edit();
        }

        if (value instanceof String) {
            sharedataeditor.putString(key, (String) value);

        } else if (value instanceof Integer) {

            sharedataeditor.putInt(key, (int) value);
        }
        sharedataeditor.commit();
    }

}
