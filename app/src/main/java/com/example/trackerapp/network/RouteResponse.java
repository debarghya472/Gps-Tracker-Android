package com.example.trackerapp.network;

import com.google.gson.annotations.SerializedName;

public class RouteResponse {
    @SerializedName("summary")
    private String summary;
    @SerializedName("legs")
    private LegsResponse legsResponse;

    public LegsResponse getLegsResponse() {
        return legsResponse;
    }

    public String getSummary() {
        return summary;
    }

    public RouteResponse(String summary, LegsResponse legsResponse) {
        this.summary = summary;
        this.legsResponse = legsResponse;
    }
}
