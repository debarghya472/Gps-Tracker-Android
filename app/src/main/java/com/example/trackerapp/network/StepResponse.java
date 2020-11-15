package com.example.trackerapp.network;

import com.google.android.gms.maps.model.Polyline;
import com.google.gson.annotations.SerializedName;

public class StepResponse {

    @SerializedName("travel_mode")
    private  String mode;
    @SerializedName("polyline")
    private POLYline polyline;

    public StepResponse(String mode, POLYline polyline) {
        this.mode = mode;
        this.polyline = polyline;
    }

    public String getMode() {
        return mode;
    }

    public POLYline getPolyline() {
        return polyline;
    }
}
