package com.example.spaceobstaclerace.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spaceobstaclerace.R;
import com.example.spaceobstaclerace.ui.GameMode;

public class MenuActivity extends AppCompatActivity {

    private Button buttonModeSlow;
    private Button buttonModeFast;
    private Button sensorMode;
    private Button highScoresButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        initializeViews();
        setButtonListeners();
    }

    private void initializeViews() {
        buttonModeSlow = findViewById(R.id.button_mode_slow);
        buttonModeFast = findViewById(R.id.button_mode_fast);
        sensorMode = findViewById(R.id.sensor_mode);
        highScoresButton = findViewById(R.id.high_scores_button);
    }

    private void setButtonListeners() {
        buttonModeSlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(GameMode.BUTTON_SLOW);
            }
        });

        buttonModeFast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(GameMode.BUTTON_FAST);
            }
        });

        sensorMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(GameMode.SENSOR);
            }
        });

        highScoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHighScores();
            }
        });
    }

    private void startGame(GameMode mode) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("GAME_MODE", mode);
        startActivity(intent);
    }

    private void showHighScores() {
        Intent intent = new Intent(this, HighScoreActivity.class);
        startActivity(intent);
    }
}