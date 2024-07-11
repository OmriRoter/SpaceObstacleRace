package com.example.spaceobstaclerace.managers;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
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

import com.example.spaceobstaclerace.R;
import com.example.spaceobstaclerace.activities.MainActivity;
import com.example.spaceobstaclerace.activities.MenuActivity;
import com.example.spaceobstaclerace.game.entities.Coin;
import com.example.spaceobstaclerace.game.entities.Obstacle;
import com.example.spaceobstaclerace.game.entities.Player;
import com.example.spaceobstaclerace.game.utils.GameConstants;
import com.example.spaceobstaclerace.game.utils.VibrationUtil;
import com.example.spaceobstaclerace.ui.GameMode;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;

public class GameHandler {
    private final Context context;
    private final Player player;
    private final TextView scoreTextView;
    private final ImageView[] lifeImageViews;
    private final Handler handler;
    private final List<Obstacle> obstacleList;
    private final List<Coin> coinList;
    private int score = 0;
    private int lives = GameConstants.INITIAL_LIVES;
    private boolean isPaused = false;
    private final double obstacleSpeed;
    private final SoundManager soundManager;
    private int distance = 0;
    private final TextView distanceTextView;
    private boolean gameOver = false;
    private final HighScoreManager highScoreManager;
    private boolean isGameOver = false;
    private final GameMode gameMode;
    private final FusedLocationProviderClient fusedLocationClient;

    public GameHandler(Context context, Player player, TextView scoreTextView, TextView distanceTextView, ImageView[] lifeImageViews, Handler handler, double initialSpeed, GameMode gameMode) {
        this.context = context;
        this.player = player;
        this.scoreTextView = scoreTextView;
        this.distanceTextView = distanceTextView;
        this.lifeImageViews = lifeImageViews;
        this.handler = handler;
        this.obstacleList = new ArrayList<>();
        this.coinList = new ArrayList<>();
        this.obstacleSpeed = initialSpeed;
        this.soundManager = new SoundManager(context);
        this.highScoreManager = new HighScoreManager(context);
        this.gameMode = gameMode;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void startGame() {
        isGameOver = false;
        isPaused = false;
        handler.post(createObstacleRunnable);
        handler.post(createCoinRunnable);
        handler.post(moveObjectsRunnable);
    }

    public void pauseGame() {
        if (!isGameOver) {
            isPaused = true;
            handler.removeCallbacks(createObstacleRunnable);
            handler.removeCallbacks(createCoinRunnable);
            handler.removeCallbacks(moveObjectsRunnable);
        }
    }

    public void resumeGame() {
        if (!isGameOver) {
            isPaused = false;
            handler.post(createObstacleRunnable);
            handler.post(createCoinRunnable);
            handler.post(moveObjectsRunnable);
        }
    }

    private void createObstacle() {
        int randomLane = new Random().nextInt(Player.TOTAL_LANES);
        Obstacle obstacle = new Obstacle(context, randomLane, obstacleSpeed);
        obstacleList.add(obstacle);
        RelativeLayout layout = ((AppCompatActivity) context).findViewById(R.id.game_layout);
        layout.addView(obstacle.getObstacleImageView());
    }

    private void createCoin() {
        int randomLane = new Random().nextInt(Player.TOTAL_LANES);
        Coin coin = new Coin(context, randomLane, obstacleSpeed);
        coinList.add(coin);
        RelativeLayout layout = ((AppCompatActivity) context).findViewById(R.id.game_layout);
        layout.addView(coin.getCoinImageView());
    }

    private void handleCollision() {
        if (gameOver) return;

        gameOver = true;
        handler.removeCallbacksAndMessages(null);
        VibrationUtil.vibrateDevice(context);
        soundManager.playCollisionSound();

        lives--;
        updateLives();

        if (lives > 0) {
            gameOver = false;
            Toast.makeText(context, "Lives remaining: " + lives, Toast.LENGTH_SHORT).show();
            resetGame(false);
        } else {
            showGameOverDialog();
        }
    }

    private void handleCoinCollection() {
        score += GameConstants.COIN_VALUE;
        updateScore();
        soundManager.playCoinSound();
    }

    public void resetGame(boolean fullReset) {
        isGameOver = false;
        isPaused = false;

        clearObjects();
        player.resetPosition();

        if (fullReset) {
            score = 0;
            distance = 0;
            lives = GameConstants.INITIAL_LIVES;
            updateLives();
            updateScore();
            updateDistanceDisplay();
        }

        handler.removeCallbacks(createObstacleRunnable);
        handler.removeCallbacks(createCoinRunnable);
        handler.removeCallbacks(moveObjectsRunnable);

        handler.post(createObstacleRunnable);
        handler.post(createCoinRunnable);
        handler.post(moveObjectsRunnable);
    }

    private void clearObjects() {
        ((AppCompatActivity) context).runOnUiThread(() -> {
            RelativeLayout layout = ((AppCompatActivity) context).findViewById(R.id.game_layout);
            for (Obstacle obstacle : obstacleList) {
                layout.removeView(obstacle.getObstacleImageView());
            }
            for (Coin coin : coinList) {
                layout.removeView(coin.getCoinImageView());
            }
            obstacleList.clear();
            coinList.clear();
        });
    }

    private void showGameOverDialog() {
        isGameOver = true;
        ((AppCompatActivity) context).runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Game Over")
                    .setMessage("Your score: " + score + "\nDistance traveled: " + distance)
                    .setPositiveButton("Save Score", (dialog, which) -> {
                        showSaveScoreDialog();
                    })
                    .setNegativeButton("Restart", (dialog, which) -> {
                        restartGame();
                    })
                    .setNeutralButton("Quit", (dialog, which) -> ((AppCompatActivity) context).finish())
                    .setCancelable(false)
                    .show();
        });
    }
    private void goToMenuActivity() {
        Intent intent = new Intent(context, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        ((AppCompatActivity) context).finish();
    }
    private void restartGame() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("GAME_MODE", gameMode);
        context.startActivity(intent);
        ((AppCompatActivity) context).finish();
    }

    private void showSaveScoreDialog() {
        ((AppCompatActivity) context).runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            final EditText input = new EditText(context);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setTitle("Enter your name")
                    .setPositiveButton("Save", (dialog, which) -> {
                        String playerName = input.getText().toString();
                        if (!playerName.isEmpty()) {
                            saveScoreWithLocation(playerName);
                            goToMenuActivity();
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.cancel();
                        goToMenuActivity();
                    })
                    .show();
        });
    }
    private void saveScoreWithLocation(String playerName) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            highScoreManager.addScore(playerName, score, 0, 0);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener((AppCompatActivity) context, location -> {
                    double latitude = 0;
                    double longitude = 0;
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                    highScoreManager.addScore(playerName, score, latitude, longitude);
                });
    }


    private void updateLives() {
        for (int i = 0; i < lifeImageViews.length; i++) {
            lifeImageViews[i].setVisibility(i < lives ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void updateScore() {
        ((AppCompatActivity) context).runOnUiThread(() -> scoreTextView.setText("Score: " + score));
    }

    private void updateDistance() {
        distance += GameConstants.DISTANCE_INCREMENT;
        updateDistanceDisplay();
    }

    private void updateDistanceDisplay() {
        ((AppCompatActivity) context).runOnUiThread(() -> distanceTextView.setText("Distance: " + distance));
    }

    private final Runnable createObstacleRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isPaused) {
                createObstacle();
                handler.postDelayed(this, GameConstants.OBSTACLE_CREATION_INTERVAL);
            }
        }
    };

    private final Runnable createCoinRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isPaused) {
                createCoin();
                handler.postDelayed(this, GameConstants.COIN_CREATION_INTERVAL);
            }
        }
    };

    private final Runnable moveObjectsRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isPaused && !gameOver) {
                moveObstacles();
                moveCoins();
                updateDistance();
                handler.postDelayed(this, GameConstants.OBSTACLE_MOVEMENT_INTERVAL);
            }
        }
    };

    private void moveObstacles() {
        for (Iterator<Obstacle> iterator = obstacleList.iterator(); iterator.hasNext(); ) {
            Obstacle obstacle = iterator.next();
            obstacle.move();
            if (obstacle.isColliding(player)) {
                handleCollision();
                return;
            }
            if (obstacle.isOffScreen(context.getResources().getDisplayMetrics().heightPixels)) {
                RelativeLayout layout = ((AppCompatActivity) context).findViewById(R.id.game_layout);
                layout.removeView(obstacle.getObstacleImageView());
                iterator.remove();
                score++;
                updateScore();
            }
        }
    }

    private void moveCoins() {
        for (Iterator<Coin> iterator = coinList.iterator(); iterator.hasNext(); ) {
            Coin coin = iterator.next();
            coin.move();
            if (coin.isColliding(player)) {
                handleCoinCollection();
                RelativeLayout layout = ((AppCompatActivity) context).findViewById(R.id.game_layout);
                layout.removeView(coin.getCoinImageView());
                iterator.remove();
            } else if (coin.isOffScreen(context.getResources().getDisplayMetrics().heightPixels)) {
                RelativeLayout layout = ((AppCompatActivity) context).findViewById(R.id.game_layout);
                layout.removeView(coin.getCoinImageView());
                iterator.remove();
            }
        }
    }




    public boolean isPaused() {
        return isPaused;
    }


    public boolean isGameOver() {
        return isGameOver;
    }

    public void release() {
        soundManager.release();
    }
}