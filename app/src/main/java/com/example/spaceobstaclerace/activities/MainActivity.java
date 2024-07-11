package com.example.spaceobstaclerace.activities;

import static com.example.spaceobstaclerace.game.utils.GameConstants.LOCATION_PERMISSION_REQUEST_CODE;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;

import com.example.spaceobstaclerace.R;
import com.example.spaceobstaclerace.controllers.TiltController;
import com.example.spaceobstaclerace.game.entities.Player;
import com.example.spaceobstaclerace.game.utils.GameConstants;
import com.example.spaceobstaclerace.managers.GameHandler;
import com.example.spaceobstaclerace.ui.GameMode;

public class MainActivity extends AppCompatActivity {

    private com.example.spaceobstaclerace.game.entities.Player player;
    private Button leftButton, rightButton, pauseButton;
    private GameHandler gameHandler;
    private GameMode gameMode;
    private TiltController tiltController;
    private double obstacleSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameMode = (GameMode) getIntent().getSerializableExtra("GAME_MODE");

        TextView scoreTextView = findViewById(R.id.score_text);
        TextView distanceTextView = findViewById(R.id.distance_text);
        ImageView[] lifeImageViews = new ImageView[]{
                findViewById(R.id.life1),
                findViewById(R.id.life2),
                findViewById(R.id.life3)
        };

        leftButton = findViewById(R.id.left_button);
        rightButton = findViewById(R.id.right_button);
        pauseButton = findViewById(R.id.pause_button);

        player = new Player(new ImageView[]{
                findViewById(R.id.player_far_left),
                findViewById(R.id.player_left),
                findViewById(R.id.player_middle),
                findViewById(R.id.player_right),
                findViewById(R.id.player_far_right)
        });

        setupGameMode();
        gameHandler = new GameHandler(this, player, scoreTextView, distanceTextView, lifeImageViews, new Handler(), obstacleSpeed, gameMode);
        setupControls();
        startNewGame();
        checkLocationPermission();
    }

    private void startNewGame() {
        gameHandler.resetGame(true);
        gameHandler.startGame();
    }

    private void setupGameMode() {
        switch (gameMode) {
            case BUTTON_SLOW:
            case SENSOR:
                obstacleSpeed = GameConstants.OBSTACLE_SPEED;
                break;
            case BUTTON_FAST:
                obstacleSpeed = GameConstants.OBSTACLE_SPEED * 1.5;
                break;
        }
    }

    private void setupControls() {
        switch (gameMode) {
            case BUTTON_SLOW:
            case BUTTON_FAST:
                setupButtonControls();
                break;
            case SENSOR:
                setupSensorControls();
                break;
        }

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gameHandler.isGameOver()) {
                    if (gameHandler.isPaused()) {
                        resumeGame();
                    } else {
                        pauseGame();
                    }
                }
            }
        });
    }

    private void setupButtonControls() {
        leftButton.setVisibility(View.VISIBLE);
        rightButton.setVisibility(View.VISIBLE);

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gameHandler.isGameOver() && !gameHandler.isPaused()) {
                    player.movePlayerToLane(player.getCurrentLane() - 1);
                }
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gameHandler.isGameOver() && !gameHandler.isPaused()) {
                    player.movePlayerToLane(player.getCurrentLane() + 1);
                }
            }
        });
    }

    private void setupSensorControls() {
        leftButton.setVisibility(View.GONE);
        rightButton.setVisibility(View.GONE);

        tiltController = new TiltController(this, player);
        tiltController.start();
    }

    private void pauseGame() {
        pauseButton.setText("Resume");
        gameHandler.pauseGame();
        if (gameMode == GameMode.SENSOR) {
            tiltController.stop();
        }
    }

    private void resumeGame() {
        pauseButton.setText("Pause");
        gameHandler.resumeGame();
        if (gameMode == GameMode.SENSOR) {
            tiltController.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gameMode == GameMode.SENSOR) {
            tiltController.stop();
        }
        gameHandler.pauseGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameMode == GameMode.SENSOR && !gameHandler.isPaused()) {
            tiltController.start();
        }
        if (!gameHandler.isPaused()) {
            gameHandler.resumeGame();
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gameHandler != null) {
            gameHandler.release();
        }
        if (tiltController != null) {
            tiltController.stop();
        }
        if (getWindow() != null) {
            getWindow().getDecorView();
            getWindow().getDecorView().removeCallbacks(null);
        }
        new Handler().removeCallbacksAndMessages(null);
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }}