package com.example.lexlevi.sweapp.Controllers;

import com.example.lexlevi.sweapp.Models.User;
import com.example.lexlevi.sweapp.Common.URLs;

import retrofit2.http.*;
import retrofit2.Call;

import java.util.List;

public interface ChatServerAPI {

    // Users
    @POST(URLs.kUserSignup)
    Call<User> createUser(@Body User user);

    @POST(URLs.kUserLogin)
    Call<User> loginUser(@Body User user);

    // Groups
    @GET("group/{id}/users")
    Call<List<User>> groupList(@Path("id") int groupId, @Query("sort") String sort);
}
