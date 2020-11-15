package com.example.trackerapp.network;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class POLYline {
    @SerializedName("points")
    private List<String> pointsList;

    public POLYline(List<String> pointsList) {
        this.pointsList = pointsList;
    }

    public List<String> getPointsList() {
        return pointsList;
    }
}
