package com.example.trackerapp.network;

import com.google.android.gms.maps.model.Polyline;
import com.google.gson.annotations.SerializedName;

public class DirectionResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("routes")
    private RouteResponse routeResponse;

    public String getStatus() {
        return status;
    }

    public RouteResponse getRouteResponse() {
        return routeResponse;
    }

    public DirectionResponse(String status, RouteResponse routeResponse) {
        this.status = status;
        this.routeResponse = routeResponse;
    }
}
