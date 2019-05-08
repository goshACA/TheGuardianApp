package com.news.theguardianapp.retrofit;


import com.news.theguardianapp.entity.ApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GuardianRequest {

    @GET("search")
    Call<ApiResponse> searchArticles(@Query("api-key") String api_key, @Query("show-fields") String show_fields, @Query("page") int page);


}
