package com.kseniyaa.loftmoney;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    @GET("items")
    Call<ItemsData> getItems(@Query("type") String type);

    @POST("items/add")
    Call<Item> createItem(@Body Item item);

    @DELETE("items/{id}")
    Call<Item> deleteItem (@Path ("id") int id);
}
