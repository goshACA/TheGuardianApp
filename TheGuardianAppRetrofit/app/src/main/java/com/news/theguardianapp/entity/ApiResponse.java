package com.news.theguardianapp.entity;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {
    @SerializedName("response")
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
