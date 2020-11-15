package com.example.trackerapp.network;

import retrofit2.Call;
import retrofit2.http.GET;

public interface JsonPlaceHolderApi {

    @GET("")
    Call<DirectionResponse> getDirection();
}
