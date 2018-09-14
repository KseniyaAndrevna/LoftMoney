package com.kseniyaa.loftmoney;

import java.util.LinkedHashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {

    @GET("items")
    Call<List<Item>> getItems(@Query("type") String type, @Query("auth-token") String auth_token);

    @POST("items/add")
    Call<Item> createItem(@Body Item item,  @Query("auth-token") String auth_token);

    @POST("items/remove")
    Call<Item> deleteItem (@Query ("id") int id, @Query("auth-token") String auth_token);

    @GET("auth")
    Call<LinkedHashMap<String,String>> getAuthToken(@Query("social_user_id") String user_id);
    // TODO: 14.09.2018 get balance

}
