package com.example.spaceobstaclerace;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameHandler {
    private Context context;
    private Player player;
    private TextView scoreTextView;
    private ImageView[] lifeImageViews;
    private Handler handler;
    private List<Obstacle> obstacleList;
    private int score = 0;
    private int lives = GameConstants.INITIAL_LIVES;
    private boolean isPaused = false;

    public GameHandler(Context context, Player player, TextView scoreTextView, ImageView[] lifeImageViews, Handler handler) {
        this.context = context;
        this.player = player;
        this.scoreTextView = scoreTextView;
        this.lifeImageViews = lifeImageViews;
        this.handler = handler;
        this.obstacleList = new ArrayList<>();
    }

    public void startGame() {
        handler.post(createObstacleRunnable);
        handler.post(moveObstaclesRunnable);
    }

    public void pauseGame() {
        isPaused = true;
        handler.removeCallbacks(createObstacleRunnable);
        handler.removeCallbacks(moveObstaclesRunnable);
    }

    public void resumeGame() {
        isPaused = false;
        handler.post(createObstacleRunnable);
        handler.post(moveObstaclesRunnable);
    }

    private void createObstacle() {
        int randomLane = new Random().nextInt(3);
        int speed = GameConstants.OBSTACLE_SPEED;
        Obstacle obstacle = new Obstacle(context, randomLane, speed);
        obstacleList.add(obstacle);
        RelativeLayout layout = (RelativeLayout) ((AppCompatActivity) context).findViewById(R.id.game_layout);
        layout.addView(obstacle.getObstacleImageView());
    }

    private void handleCollision() {
        handler.removeCallbacksAndMessages(null);
        VibrationUtil.vibrateDevice(context);

        Toast.makeText(context, "Collision detected!", Toast.LENGTH_SHORT).show();
        lives--;
        updateLives();

        if (lives > 0) {
            Toast.makeText(context, "Lives remaining: " + lives, Toast.LENGTH_SHORT).show();
            resetGame(false);
        } else {
            Toast.makeText(context, "Game Over! Your score: " + score, Toast.LENGTH_LONG).show();
            showGameOverDialog();
        }
    }

    private void resetGame(boolean resetScore) {
        RelativeLayout layout = (RelativeLayout) ((AppCompatActivity) context).findViewById(R.id.game_layout);
        for (Obstacle obstacle : obstacleList) {
            layout.removeView(obstacle.getObstacleImageView());
        }
        obstacleList.clear();

        player.resetPosition();

        if (resetScore) {
            score = 0;
            scoreTextView.setText("Score: " + score);
        }

        startGame();
    }

    private void showGameOverDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Game Over")
                .setMessage("Your score: " + score)
                .setPositiveButton("Restart", (dialog, which) -> {
                    lives = GameConstants.INITIAL_LIVES;
                    resetGame(true);
                })
                .setNegativeButton("Quit", (dialog, which) -> ((AppCompatActivity) context).finish())
                .setCancelable(false)
                .show();
    }

    private void updateLives() {
        for (int i = 0; i < lifeImageViews.length; i++) {
            lifeImageViews[i].setVisibility(i < lives ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private Runnable createObstacleRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isPaused) {
                createObstacle();
                handler.postDelayed(this, GameConstants.OBSTACLE_CREATION_INTERVAL);
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
                    if (obstacle.isColliding(player.getCurrentPlayerImageView())) {
                        handleCollision();
                        return;
                    }
                    if (obstacle.isOffScreen(context.getResources().getDisplayMetrics().heightPixels)) {
                        RelativeLayout layout = (RelativeLayout) ((AppCompatActivity) context).findViewById(R.id.game_layout);
                        layout.removeView(obstacle.getObstacleImageView());
                        iterator.remove();
                        obstacleRemoved = true;
                        score++;
                        ((AppCompatActivity) context).runOnUiThread(() -> scoreTextView.setText("Score: " + score));
                    }
                }
                handler.postDelayed(this, GameConstants.OBSTACLE_MOVEMENT_INTERVAL);
            }
        }
    };
}
