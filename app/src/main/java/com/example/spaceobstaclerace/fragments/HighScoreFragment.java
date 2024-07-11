package com.example.spaceobstaclerace.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.spaceobstaclerace.R;
import com.example.spaceobstaclerace.activities.HighScoreActivity;
import com.example.spaceobstaclerace.managers.HighScoreManager;

import java.util.List;
import java.util.Map;

public class HighScoreFragment extends Fragment {
    private ListView highScoreListView;
    private HighScoreManager highScoreManager;
    private List<Map.Entry<String, HighScoreManager.ScoreData>> highScores;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_high_score, container, false);
        highScoreListView = view.findViewById(R.id.highScoreListView);
        highScoreManager = new HighScoreManager(requireContext());
        updateHighScoreList();
        return view;
    }

    private void updateHighScoreList() {
        highScores = highScoreManager.getHighScores();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1);

        for (Map.Entry<String, HighScoreManager.ScoreData> score : highScores) {
            adapter.add(score.getKey() + ": " + score.getValue().score);
        }

        highScoreListView.setAdapter(adapter);
        highScoreListView.setOnItemClickListener((parent, view, position, id) -> {
            HighScoreManager.ScoreData selectedScore = highScores.get(position).getValue();
            if (getActivity() instanceof HighScoreActivity) {
                ((HighScoreActivity) getActivity()).showLocationOnMap(selectedScore.latitude, selectedScore.longitude);
            }
        });
    }
}