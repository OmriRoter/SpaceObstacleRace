package com.example.spaceobstaclerace.managers;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.spaceobstaclerace.R;

public class SoundManager {
    private MediaPlayer collisionSound;
    private MediaPlayer coinSound;

    public SoundManager(Context context) {
        collisionSound = MediaPlayer.create(context, R.raw.collision_sound);
        coinSound = MediaPlayer.create(context, R.raw.coin_sound);
    }

    public void playCollisionSound() {
        if (collisionSound != null) {
            collisionSound.start();
        }
    }

    public void playCoinSound() {
        if (coinSound != null) {
            coinSound.start();
        }
    }

    public void release() {
        if (collisionSound != null) {
            collisionSound.release();
            collisionSound = null;
        }
        if (coinSound != null) {
            coinSound.release();
            coinSound = null;
        }
    }
}