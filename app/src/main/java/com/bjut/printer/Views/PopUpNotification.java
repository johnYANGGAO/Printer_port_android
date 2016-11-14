package com.bjut.printer.Views;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bjut.printer.Consts.PublicConsts;
import com.bjut.printer.application.MyApplication;
import com.bjut.printer.bean.WorkLog;
import com.bjut.printer2.R;

/**
 * Created by johnsmac on 9/11/16.
 */
public class PopUpNotification {

    private Context context;
    private Handler handler;
    private MyApplication mApplication;

    public PopUpNotification(Context mContext, Handler mHandler, MyApplication myApplication) {

        this.context = mContext;
        this.handler = mHandler;
        this.mApplication = myApplication;
    }

    public void showPopUp(View v, final String x, final String y, final String z) {

        LayoutInflater inflater = LayoutInflater.from(context);

        final View view = inflater.inflate(R.layout.backoriginwindow, null);

        final PopupWindow popUp = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        popUp.setFocusable(true);
        popUp.setOutsideTouchable(true);
        popUp.setBackgroundDrawable(new BitmapDrawable());

        final CheckBox cb_x = (CheckBox) view.findViewById(R.id.cb_x);
        final CheckBox cb_y = (CheckBox) view.findViewById(R.id.cb_y);
        final CheckBox cb_z = (CheckBox) view.findViewById(R.id.cb_z);
        Button btn_confirm = (Button) view.findViewById(R.id.btn_confirm);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

        btn_confirm.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                String value_x;
                String value_y;
                String value_z;
                String value = "";
                if (cb_x.isChecked()) {
                    value_x = "0.0";
                    value = value + "X";
                } else {
//                    value_x = text_x.getText().toString();
                    value_x = x;
                }
                if (cb_y.isChecked()) {
                    value_y = "0.0";
                    value = value + "Y";
                } else {
//                    value_y = text_y.getText().toString();
                    value_y = y;
                }
                if (cb_z.isChecked()) {
                    value_z = "0.0";
                    value = value + "Z";
                } else {
//                    value_z = text_z.getText().toString();
                    value_z = z;
                }

                // SendCommand("G28 " + value + "\n");
                Message msg = handler.obtainMessage();
                msg.obj = "G28 " + value + "\n";
                msg.what = PublicConsts.POPUPWINDOW_CONFIRM;
                handler.sendMessage(msg);

                WorkLog worklog = new WorkLog();
                mApplication.worklog_no++;
                worklog.setId(mApplication.worklog_no);
                worklog.setSenddata("G28 " + value + "\n");
                worklog.setType(4);
                worklog.setValuex(value_x);
                worklog.setValuey(value_y);
                worklog.setValuez(value_z);
                mApplication.workloglist.add(worklog);
                popUp.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                popUp.dismiss();
            }
        });

//        int[] location = new int[2];
//        v.getLocationOnScreen(location);
//        popUp.showAtLocation(v, Gravity.NO_GRAVITY, location[0] - 315, location[1] - 320);
        popUp.showAtLocation(v, Gravity.CENTER, 0, 0);

    }

    public void PopUpFileUnPrinted(View locationView, final Handler handler) {

        LayoutInflater inflater = LayoutInflater.from(context);

        final View view = inflater.inflate(R.layout.unfinishedwindow, null);
        final PopupWindow filePrintStill = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        final TextView text_filename = (TextView) view.findViewById(R.id.text_unfilename);
        final TextView text_percent = (TextView) view.findViewById(R.id.text_percent);
        final Button moving = (Button) view.findViewById(R.id.btn_goon);
        Button btn_quit = (Button) view.findViewById(R.id.btn_giveup);

        text_filename.setText(mApplication.configManager.unfilename);
        text_percent.setText(mApplication.configManager.percent);


        moving.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                filePrintStill.dismiss();
                handler.sendEmptyMessage(PublicConsts.POPUP_PRINT_WHAT);

            }
        });

        btn_quit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mApplication.configManager.unfilename = "";
                mApplication.configManager.unfilepath = "";
                mApplication.configManager.percent = "";
                mApplication.list_line = 0;
                mApplication.get_line = 0;
                mApplication.configManager.current_print = 0;
                mApplication.configManager.printlist.clear();
                filePrintStill.dismiss();

            }
        });

//        filePrintStill.showAtLocation(locationView, Gravity.HORIZONTAL_GRAVITY_MASK,
//                (int) ((getScreenHeight(context) - filePrintStill.getWidth()) * 0.5),
//                ((int) ((getScreenWidth(context) - filePrintStill.getHeight()) * 0.5)));

        filePrintStill.showAtLocation(locationView, Gravity.CENTER, 0, 0);
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }


}
