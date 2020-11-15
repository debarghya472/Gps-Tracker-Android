package com.example.trackerapp.network;

import com.google.android.gms.maps.model.Polyline;
import com.google.gson.annotations.SerializedName;

public class DirectionResponse {
    @SerializedName("polylines")
    private Polyline polyline;

    public Polyline getPolyline() {
        return polyline;
    }

    public DirectionResponse(Polyline polyline) {
        this.polyline = polyline;
    }
}
