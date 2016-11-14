package com.bjut.printer.Operator;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;

/**
 * Created by johnsmac on 9/14/16.
 */
public class SaveEnergy {

    private PowerManager pm;
    private PowerManager.WakeLock wl;
    private Context context;
    private KeyguardManager km;
    private KeyguardManager.KeyguardLock kl;
    /**
     * we call it in the broadcast
     * */
    public SaveEnergy(Context mContext) {

        this.context = mContext;

    }

    public void wakeOrLock(boolean wake) {
        if (wake) {
            pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

            wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");

            wl.acquire();

            km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            kl = km.newKeyguardLock("unLock");

            kl.disableKeyguard();
        } else {

            kl.reenableKeyguard();
            wl.release();
        }

    }
}
