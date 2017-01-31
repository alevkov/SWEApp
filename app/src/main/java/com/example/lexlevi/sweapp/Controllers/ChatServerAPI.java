package com.example.lexlevi.sweapp.Controllers;

import com.example.lexlevi.sweapp.Models.User;

import retrofit2.http.*;
import retrofit2.Call;

import java.util.List;

public interface ChatServerAPI {
    // Request method and URL specified in the annotation
    // Callback for the parsed response is the last parameter

//    @GET("users/{username}")
//    Call<User> getUser(@Path("username") String username);
//
//    @GET("group/{id}/users")
//    Call<List<User>> groupList(@Path("id") int groupId, @Query("sort") String sort);

    @POST("users/new")
    Call<User> createUser(@Body User user);
}
