package com.bjut.printer.utils;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.bjut.printer2.R;

import java.util.HashMap;

/**
 * Created by johnsmac on 9/18/16.
 */


/**
 * It turns out that the constructor is deprecated to API21+
 * so I decided to create a one existing old code
 */
public class SoundPlayUtils {

    private SoundPool sp;
    private Context mContext;

    public SoundPlayUtils(Context context) {

        sp = new SoundPool(3, AudioManager.STREAM_MUSIC, 3);
        this.mContext = context;
    }

    public void play_voice() {
        sp.play(sp.load(mContext, R.raw.bee, 1), 1, 1, 0, 0, 1);
    }
/**
 //another way is showed below

 public void play_bee(){

 MediaPlayer sound=MediaPlayer.create(mContext,R.raw.bee);
 sound.setLooping(false);
 sound.start();
 }
 */
}
