package com.example.spaceobstaclerace.game.entities;

import android.content.Context;
import android.graphics.Rect;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.spaceobstaclerace.R;

public class Coin {
    private final ImageView coinImageView;
    private final int lane;
    private double speed;

    public Coin(Context context, int lane, double speed) {
        this.lane = lane;
        this.speed = speed;
        coinImageView = new ImageView(context);
        coinImageView.setImageResource(R.drawable.coin);
        coinImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        setCoinProperties();
    }

    private void setCoinProperties() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        int screenWidth = coinImageView.getContext().getResources().getDisplayMetrics().widthPixels;
        int laneWidth = screenWidth / Player.TOTAL_LANES;

        params.leftMargin = lane * laneWidth + (laneWidth - params.width) / 2;
        coinImageView.setLayoutParams(params);
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void move() {
        coinImageView.setY((float) (coinImageView.getY() + speed));
    }

    public ImageView getCoinImageView() {
        return coinImageView;
    }

    public boolean isOffScreen(int screenHeight) {
        return coinImageView.getY() > screenHeight;
    }

    public boolean isColliding(Player player) {
        Rect coinRect = new Rect();
        coinImageView.getHitRect(coinRect);

        Rect playerRect = player.getPlayerRect();

        return Rect.intersects(coinRect, playerRect);
    }


}