package com.cesaas.android.pos.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.cesaas.android.pos.R;

/**
 * Author FGB
 * Description 声音池工具类
 * Created at 2017/9/7 16:57
 * Version 1.0
 */

public class SoundPoolUtils {

    private static MediaPlayer mPlayer = null;
    private static SoundPool soundPool;

    /**
     * 通常都是播放短音效，比如枪声或者水滴声
     * @param context
     */
    public static void initSoundPool(Context context){
        soundPool= new SoundPool(10, AudioManager.STREAM_SYSTEM,5);
        soundPool.load(context, R.raw.new_order,1);
        soundPool.play(1,1, 1, 0, 0, 1);
    }

    /**
     *  播放比较长的音频，如游戏中的背景音乐
     * @param context
     */
    public static void initMediaPlayer(Context context){
        mPlayer = MediaPlayer.create(context,R.raw.new_order);
        mPlayer.setLooping(false);
        mPlayer.start();
    }
}
