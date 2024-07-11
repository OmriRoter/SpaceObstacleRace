package com.example.spaceobstaclerace.managers;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HighScoreManager {
    private static final String PREFS_NAME = "HighScores";
    private static final int MAX_HIGH_SCORES = 10;

    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public HighScoreManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void addScore(String playerName, int score, double latitude, double longitude) {
        Map<String, ScoreData> scores = getScoresMap();
        scores.put(playerName, new ScoreData(score, latitude, longitude));

        List<Map.Entry<String, ScoreData>> sortedScores = new ArrayList<>(scores.entrySet());
        sortedScores.sort(new Comparator<Map.Entry<String, ScoreData>>() {
            @Override
            public int compare(Map.Entry<String, ScoreData> o1, Map.Entry<String, ScoreData> o2) {
                return o2.getValue().score - o1.getValue().score;
            }
        });

        Map<String, ScoreData> topScores = new LinkedHashMap<>();
        for (int i = 0; i < Math.min(sortedScores.size(), MAX_HIGH_SCORES); i++) {
            Map.Entry<String, ScoreData> entry = sortedScores.get(i);
            topScores.put(entry.getKey(), entry.getValue());
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("scores", gson.toJson(topScores));
        editor.apply();
    }

    public List<Map.Entry<String, ScoreData>> getHighScores() {
        Map<String, ScoreData> scores = getScoresMap();
        return new ArrayList<>(scores.entrySet());
    }

    private Map<String, ScoreData> getScoresMap() {
        String json = sharedPreferences.getString("scores", "{}");
        Type type = new TypeToken<LinkedHashMap<String, ScoreData>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public static class ScoreData {
        public int score;
        public double latitude;
        public double longitude;

        public ScoreData(int score, double latitude, double longitude) {
            this.score = score;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}