package com.example.trackerapp.network;

import com.google.gson.annotations.SerializedName;

public class LegsResponse {


    @SerializedName("steps")
    private StepResponse stepResponse;

    public StepResponse getStepResponse() {
        return stepResponse;
    }

    public LegsResponse(StepResponse stepResponse) {
        this.stepResponse = stepResponse;
    }
}
