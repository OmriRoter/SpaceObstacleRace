package com.example.spaceobstaclerace;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Player player;
    private Button leftButton, rightButton, pauseButton;
    private TextView scoreTextView;
    private ImageView[] lifeImageViews;
    private GameHandler gameHandler;
    private boolean isPaused = false;
    private int lives = GameConstants.INITIAL_LIVES;

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

        leftButton = findViewById(R.id.left_button);
        rightButton = findViewById(R.id.right_button);
        pauseButton = findViewById(R.id.pause_button);

        player = new Player(new ImageView[]{
                findViewById(R.id.player_left),
                findViewById(R.id.player_middle),
                findViewById(R.id.player_right)
        });

        gameHandler = new GameHandler(this, player, scoreTextView, lifeImageViews, new Handler());

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.movePlayerToLane(player.getCurrentLane() - 1);
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.movePlayerToLane(player.getCurrentLane() + 1);
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

        gameHandler.startGame();
    }

    private void pauseGame() {
        isPaused = true;
        pauseButton.setText("Resume");
        gameHandler.pauseGame();
    }

    private void resumeGame() {
        isPaused = false;
        pauseButton.setText("Pause");
        gameHandler.resumeGame();
    }
}
