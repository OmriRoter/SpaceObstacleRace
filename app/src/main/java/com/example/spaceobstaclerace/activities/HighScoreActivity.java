    package com.example.spaceobstaclerace.activities;

    import android.os.Bundle;

    import androidx.appcompat.app.AppCompatActivity;

    import com.example.spaceobstaclerace.R;
    import com.example.spaceobstaclerace.fragments.HighScoreFragment;
    import com.example.spaceobstaclerace.fragments.MapsFragment;


    public class HighScoreActivity extends AppCompatActivity {

        private MapsFragment mapsFragment;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_high_score);

            if (savedInstanceState == null) {
                HighScoreFragment highScoreFragment = new HighScoreFragment();
                mapsFragment = new MapsFragment();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.highScoreContainer, highScoreFragment)
                        .replace(R.id.mapContainer, mapsFragment)
                        .commit();
            }
        }

        public void showLocationOnMap(double latitude, double longitude) {
            if (mapsFragment != null) {
                mapsFragment.showLocationOnMap(latitude, longitude);
            }
        }
    }