package com.example.trackerapp.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface JsonPlaceHolderApi {

    @GET("json?origin={lat1Id},{lon1Id}&destination={lat2Id},{lon2Id}&key=YOUR_API_KEY")
    Call<DirectionResponse> getDirection(@Path("lat1Id") double lat1,
                                         @Path("lon1Id") double lon1,
                                         @Path("lat2Id") double lat2,
                                         @Path("lon2Id") double lon2);
}
