package com.bjut.printer.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;

/**
 * Created by johnsmac on 9/17/16.
 */

public class UsbStatusReceiver extends BroadcastReceiver {


    public static final int USB_STATE_ON = 0x00021;
    public static final int USB_STATE_OFF = 0x00022;
    private Handler handler;

    public UsbStatusReceiver() {
    }

    public UsbStatusReceiver(Handler usbHandler) {

        this.handler = usbHandler;

    }


    public IntentFilter getUsbFilter() {

        IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        return filter;
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (device != null) {
                handler.sendEmptyMessage(USB_STATE_OFF);
            } else {
                handler.sendEmptyMessage(USB_STATE_ON);
            }
        }

    }

}