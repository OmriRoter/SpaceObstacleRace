package com.example.spaceobstaclerace;

import android.view.View;
import android.widget.ImageView;

public class Player {
    private ImageView[] playerImageViews;
    private int currentLane;

    public Player(ImageView[] playerImageViews) {
        this.playerImageViews = playerImageViews;
        this.currentLane = 1;
        showPlayerInLane(currentLane);
    }

    public int getCurrentLane() {
        return currentLane;
    }

    public ImageView getCurrentPlayerImageView() {
        return playerImageViews[currentLane];
    }

    public void movePlayerToLane(int newLane) {
        if (newLane < 0 || newLane > 2) {
            return;
        }

        ImageView currentPlayerImageView = playerImageViews[currentLane];
        ImageView newPlayerImageView = playerImageViews[newLane];

        currentPlayerImageView.setVisibility(View.INVISIBLE);
        newPlayerImageView.setVisibility(View.VISIBLE);

        currentLane = newLane;
    }

    public void resetPosition() {
        movePlayerToLane(1);
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
}
