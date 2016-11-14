package com.bjut.printer.UpdateApk;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bjut.printer2.R;
import com.daimajia.numberprogressbar.NumberProgressBar;

/**
 * Created by johnsmac on 9/15/16.
 */


/**
 * here's dialog  for updating the progress that be showed in base activity
 */

public class UpdateDialogManager {

    private static NumberProgressBar prgb;
    private static TextView title, content;

    public static void setPrgb(int mProgress) {
        if (prgb != null) {
            prgb.setVisibility(View.VISIBLE);
            prgb.setProgress(mProgress);
        }
    }

    public static void setTitle(String updateTitle) {
        if (title != null) {
            title.setText(updateTitle);
        }
    }

    public static void setContent(String desciptor) {
        if (content != null) {
            content.setText(desciptor);
        }
    }

    @SuppressLint("InflateParams")
    public static void initUpdateDialog(Context context, final Dialog dialog,
                                        View.OnClickListener listener,View.OnClickListener abort) {

        View view = LayoutInflater.from(context).inflate(
                R.layout.dialogupdateapk, null);
        TextView sure = (TextView) view.findViewById(R.id.updatesure);
        TextView cancel = (TextView) view.findViewById(R.id.updatecancel);
        title = (TextView) view.findViewById(R.id.notification_title);
        content = (TextView) view.findViewById(R.id.content);
        prgb = (NumberProgressBar) view.findViewById(R.id.progressbar);
        prgb.setMax(100);
        sure.setOnClickListener(listener);
        cancel.setOnClickListener(abort);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(view);
        dialog.setCancelable(false);
    }
}

