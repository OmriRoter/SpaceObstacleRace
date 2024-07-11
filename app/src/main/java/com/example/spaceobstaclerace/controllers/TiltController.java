package com.example.spaceobstaclerace.controllers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.example.spaceobstaclerace.game.entities.Player;

public class TiltController implements SensorEventListener {
    private final SensorManager sensorManager;
    private final Sensor accelerometer;
    private final Player player;
    private static final float TILT_THRESHOLD = 1.5f; // Reduced threshold for more sensitivity
    private long lastMoveTime = 0;
    private static final long MOVE_COOLDOWN = 200; // 200ms cooldown between moves

    public TiltController(Context context, Player player) {
        this.player = player;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void start() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float tiltValue = event.values[0];
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastMoveTime > MOVE_COOLDOWN) {
                int currentLane = player.getCurrentLane();
                if (tiltValue < -TILT_THRESHOLD && currentLane < Player.TOTAL_LANES - 1) {
                    player.movePlayerToLane(currentLane + 1);
                    lastMoveTime = currentTime;
                } else if (tiltValue > TILT_THRESHOLD && currentLane > 0) {
                    player.movePlayerToLane(currentLane - 1);
                    lastMoveTime = currentTime;
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used in this implementation
    }
}