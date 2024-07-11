package com.example.spaceobstaclerace.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.spaceobstaclerace.R;
import com.example.spaceobstaclerace.managers.HighScoreManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;
import java.util.Map;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<Map.Entry<String, HighScoreManager.ScoreData>> highScores;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateMapMarkers();
    }

    public void setHighScores(List<Map.Entry<String, HighScoreManager.ScoreData>> highScores) {
        this.highScores = highScores;
        if (mMap != null) {
            updateMapMarkers();
        }
    }

    private void updateMapMarkers() {
        if (mMap == null || highScores == null) return;

        mMap.clear();
        for (Map.Entry<String, HighScoreManager.ScoreData> entry : highScores) {
            HighScoreManager.ScoreData scoreData = entry.getValue();
            if (scoreData.latitude != 0 && scoreData.longitude != 0) {
                LatLng position = new LatLng(scoreData.latitude, scoreData.longitude);
                mMap.addMarker(new MarkerOptions().position(position).title(entry.getKey() + ": " + scoreData.score));
            }
        }
    }

    public void showLocationOnMap(double latitude, double longitude) {
        if (mMap != null) {
            LatLng location = new LatLng(latitude, longitude);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
        }
    }
}