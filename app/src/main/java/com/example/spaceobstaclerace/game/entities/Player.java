package com.example.spaceobstaclerace.game.entities;

import android.graphics.Rect;
import android.view.View;
import android.widget.ImageView;

public class Player {
    private final ImageView[] playerImageViews;
    private int currentLane;
    public static final int TOTAL_LANES = 5;

    public Player(ImageView[] playerImageViews) {
        this.playerImageViews = playerImageViews;
        this.currentLane = TOTAL_LANES / 2; // Start in the middle lane
        showPlayerInLane(currentLane);
    }

    public int getCurrentLane() {
        return currentLane;
    }

    public ImageView getCurrentPlayerImageView() {
        return playerImageViews[currentLane];
    }

    public void movePlayerToLane(int newLane) {
        if (newLane < 0 || newLane >= TOTAL_LANES) {
            return;
        }

        ImageView currentPlayerImageView = playerImageViews[currentLane];
        ImageView newPlayerImageView = playerImageViews[newLane];

        currentPlayerImageView.setVisibility(View.INVISIBLE);
        newPlayerImageView.setVisibility(View.VISIBLE);

        currentLane = newLane;
    }

    public void resetPosition() {
        movePlayerToLane(TOTAL_LANES / 2);
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

    public Rect getPlayerRect() {
        ImageView currentPlayerImageView = getCurrentPlayerImageView();
        Rect playerRect = new Rect();
        int[] location = new int[2];
        currentPlayerImageView.getLocationOnScreen(location);
        playerRect.left = location[0];
        playerRect.top = location[1];
        playerRect.right = playerRect.left + currentPlayerImageView.getWidth();
        playerRect.bottom = playerRect.top + currentPlayerImageView.getHeight();
        return playerRect;
    }
}