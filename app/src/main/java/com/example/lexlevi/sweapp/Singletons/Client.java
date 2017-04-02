package com.example.lexlevi.sweapp.Singletons;

import com.example.lexlevi.sweapp.Common.URLs;
import com.example.lexlevi.sweapp.Interfaces.ChatServerAPI;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lexlevi on 3/19/17.
 */

public class Client {

    private static Client instance = null;
    public static ChatServerAPI api = null;

    protected Client() {
        // Exists only to defeat instantiation.
    }

    public static Client shared() {
        if (instance == null) {
            instance = new Client();
            api = new Retrofit.Builder()
                    .baseUrl(URLs.BASE_API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(ChatServerAPI.class);
        }
        return instance;
    }

    public ChatServerAPI api() {
        return api;
    }
}
