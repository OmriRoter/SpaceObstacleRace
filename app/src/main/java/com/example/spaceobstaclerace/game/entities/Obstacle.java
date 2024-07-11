package com.example.spaceobstaclerace.game.entities;

import android.content.Context;
import android.graphics.Rect;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.spaceobstaclerace.R;

public class Obstacle {
    private final ImageView obstacleImageView;
    private final int lane;
    private double speed;

    public Obstacle(Context context, int lane, double speed) {
        this.lane = lane;
        this.speed = speed;
        obstacleImageView = new ImageView(context);
        obstacleImageView.setImageResource(R.drawable.obstacle);
        obstacleImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        setObstacleProperties();
    }

    private void setObstacleProperties() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(150, 150); // Reduced size
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        int screenWidth = obstacleImageView.getContext().getResources().getDisplayMetrics().widthPixels;
        int laneWidth = screenWidth / Player.TOTAL_LANES;

        params.leftMargin = lane * laneWidth + (laneWidth - params.width) / 2;
        obstacleImageView.setLayoutParams(params);
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void move() {
        obstacleImageView.setY((float) (obstacleImageView.getY() + speed));
    }

    public ImageView getObstacleImageView() {
        return obstacleImageView;
    }

    public boolean isOffScreen(int screenHeight) {
        return obstacleImageView.getY() > screenHeight;
    }

    public boolean isColliding(Player player) {
        Rect obstacleRect = new Rect();
        obstacleImageView.getHitRect(obstacleRect);

        Rect playerRect = player.getPlayerRect();

        return Rect.intersects(obstacleRect, playerRect);
    }

}