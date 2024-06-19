package com.example.spaceobstaclerace;// Obstacle.java
import android.content.Context;
import android.graphics.Rect;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.spaceobstaclerace.R;

public class Obstacle {
    private ImageView obstacleImageView;
    private int lane;
    private int speed;

    public Obstacle(Context context, int lane, int speed) {
        this.lane = lane;
        this.speed = speed;
        obstacleImageView = new ImageView(context);
        obstacleImageView.setImageResource(R.drawable.obstacle);
        obstacleImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        setObstacleProperties();
    }

    private void setObstacleProperties() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(200, 200);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        switch (lane) {
            case 0:
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                break;
            case 1:
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                break;
            case 2:
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                break;
        }
        obstacleImageView.setLayoutParams(params);
    }

    public void move() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) obstacleImageView.getLayoutParams();
        params.topMargin += speed;
        obstacleImageView.setLayoutParams(params);
    }

    public ImageView getObstacleImageView() {
        return obstacleImageView;
    }

    public int getLane() {
        return lane;
    }

    public boolean isColliding(ImageView playerImageView) {
        Rect obstacleRect = new Rect();
        obstacleImageView.getHitRect(obstacleRect);

        Rect playerRect = new Rect();
        playerImageView.getHitRect(playerRect);

        return obstacleRect.intersect(playerRect);
    }
}