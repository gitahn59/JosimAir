package com.cbnu.josimair.Model.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.preference.PreferenceManager;
import java.util.Calendar;

public class Alarm {
    private static Calendar last;
    private static int term;
    private static Vibrator vibrator;
    private static Ringtone ringtone;
    static{
        last = Calendar.getInstance();
        last.set(Calendar.DATE,-1);
        term = 5;
        vibrator=null;
    }

    public static void setLastAsNow(){
        last = Calendar.getInstance();
    }

    public static synchronized boolean isNeeded(){
        long now = Calendar.getInstance().getTimeInMillis();
        long pre = last.getTimeInMillis();

        if(now - pre < term * 1000 * 60){
            return false;
        }else{
            return true;
        }
    }

    public static void notify(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        if (sp.getBoolean("소리", false)) {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            ringtone = RingtoneManager.getRingtone(context, notification);
            ringtone.play();
        }
        else if (sp.getBoolean("진동", false)) {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {


                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createWaveform(new long[]{700, 700, 700, 700}, 0));
                } else {
                    vibrator.vibrate(new long[]{700, 700, 700, 700}, 0);
                }
            }
        }
    }

    public static void cancel(){
        if(ringtone!=null){
            ringtone.stop();
            ringtone=null;
        }

        if(vibrator!=null){
            vibrator.cancel();
            vibrator = null;
        }
    }

}
