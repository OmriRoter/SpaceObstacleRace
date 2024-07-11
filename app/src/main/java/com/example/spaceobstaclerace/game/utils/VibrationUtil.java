package com.example.spaceobstaclerace.game.utils;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class VibrationUtil {
    public static void vibrateDevice(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(GameConstants.VIBRATION_DURATION, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }
}