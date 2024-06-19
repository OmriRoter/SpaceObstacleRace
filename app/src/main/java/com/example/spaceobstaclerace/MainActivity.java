package com.example.spaceobstaclerace;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ImageView[] playerImageViews;
    private Button leftButton, rightButton, pauseButton;
    private int currentLane;
    private List<Obstacle> obstacleList;
    private Handler handler;
    private int lives = 3;
    private final long VIBRATION_DURATION = 700; // Vibration duration in milliseconds
    private TextView scoreTextView;
    private ImageView[] lifeImageViews;
    private int score = 0;
    private boolean isPaused = false;

    private Runnable createObstacleRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isPaused) {
                createObstacle();
                handler.postDelayed(this, 2000); // Create a new obstacle every 2 seconds, adjust as needed
            }
        }
    };

    private Runnable moveObstaclesRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isPaused) {
                boolean obstacleRemoved = false;
                for (Iterator<Obstacle> iterator = obstacleList.iterator(); iterator.hasNext(); ) {
                    Obstacle obstacle = iterator.next();
                    obstacle.move();
                    if (obstacle.isColliding(playerImageViews[currentLane])) {
                        handleCollision();
                        return;
                    }
                    if (obstacle.getObstacleImageView().getTop() > getResources().getDisplayMetrics().heightPixels) {
                        RelativeLayout layout = findViewById(R.id.game_layout);
                        layout.removeView(obstacle.getObstacleImageView());
                        iterator.remove();
                        obstacleRemoved = true;
                        score++;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                scoreTextView.setText("Score: " + score);
                            }
                        });
                    }
                }
                handler.postDelayed(this, 25); // Move obstacles every 50ms, adjust as needed
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoreTextView = findViewById(R.id.score_text);
        lifeImageViews = new ImageView[]{
                findViewById(R.id.life1),
                findViewById(R.id.life2),
                findViewById(R.id.life3)
        };

        playerImageViews = new ImageView[3];
        playerImageViews[0] = findViewById(R.id.player_left);
        playerImageViews[1] = findViewById(R.id.player_middle);
        playerImageViews[2] = findViewById(R.id.player_right);

        leftButton = findViewById(R.id.left_button);
        rightButton = findViewById(R.id.right_button);
        pauseButton = findViewById(R.id.pause_button);

        currentLane = 1; // Start in the middle lane (0 = left, 1 = middle, 2 = right)
        showPlayerInLane(currentLane);

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movePlayerToLane(currentLane - 1);
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movePlayerToLane(currentLane + 1);
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPaused) {
                    resumeGame();
                } else {
                    pauseGame();
                }
            }
        });

        obstacleList = new ArrayList<>();
        handler = new Handler();

        handler.post(createObstacleRunnable);
        handler.post(moveObstaclesRunnable);
    }

    private void movePlayerToLane(int newLane) {
        if (newLane < 0 || newLane > 2) {
            return; // Ignore if the new lane is out of bounds
        }

        ImageView currentPlayerImageView = playerImageViews[currentLane];
        ImageView newPlayerImageView = playerImageViews[newLane];

        currentPlayerImageView.setVisibility(View.INVISIBLE);
        newPlayerImageView.setVisibility(View.VISIBLE);

        currentLane = newLane;
    }

    private void showPlayerInLane(int lane) {
        for (int i = 0; i < playerImageViews.length; i++) {
            if (i == lane) {
                playerImageViews[i].setVisibility(View.VISIBLE);
            } else {
                playerImageViews[i].setVisibility(View.INVISIBLE);
            }
        }
    }

    private void createObstacle() {
        int randomLane = new Random().nextInt(3);
        int speed = 20; // Increase the speed
        Obstacle obstacle = new Obstacle(this, randomLane, speed);
        obstacleList.add(obstacle);
        RelativeLayout layout = findViewById(R.id.game_layout);
        layout.addView(obstacle.getObstacleImageView());
    }

    private void handleCollision() {
        // Stop creating and moving obstacles
        handler.removeCallbacksAndMessages(null);

        // Vibrate the device
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(VIBRATION_DURATION, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(VIBRATION_DURATION);
            }
        }

        // Show a toast message
        Toast.makeText(this, "Collision detected!", Toast.LENGTH_SHORT).show();

        // Decrease lives
        lives--;
        updateLives();

        if (lives > 0) {
            // Restart the game with remaining lives
            Toast.makeText(this, "Lives remaining: " + lives, Toast.LENGTH_SHORT).show();
            resetGame();
        } else {
            // Game over
            Toast.makeText(this, "Game Over! Your score: " + score, Toast.LENGTH_LONG).show();
            showGameOverDialog();
        }
    }

    private void resetGame() {
        // Remove all obstacles from the game layout and clear the obstacle list
        RelativeLayout layout = findViewById(R.id.game_layout);
        for (Obstacle obstacle : obstacleList) {
            layout.removeView(obstacle.getObstacleImageView());
        }
        obstacleList.clear();

        // Reset player position to the middle lane
        currentLane = 1;
        showPlayerInLane(currentLane);

        // Restart obstacle creation and movement
        handler.post(createObstacleRunnable);
        handler.post(moveObstaclesRunnable);
    }

    private void showGameOverDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Over")
                .setMessage("Your score: " + score)
                .setPositiveButton("Restart", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Restart the game with initial lives
                        lives = 3;
                        resetGame();
                    }
                })
                .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Quit the game
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void updateLives() {
        for (int i = 0; i < lifeImageViews.length; i++) {
            lifeImageViews[i].setVisibility(i < lives ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void pauseGame() {
        isPaused = true;
        pauseButton.setText("Resume");
        handler.removeCallbacks(createObstacleRunnable);
        handler.removeCallbacks(moveObstaclesRunnable);
    }

    private void resumeGame() {
        isPaused = false;
        pauseButton.setText("Pause");
        handler.post(createObstacleRunnable);
        handler.post(moveObstaclesRunnable);
    }
}
