package com.example.lexlevi.sweapp.Singletons;

import com.example.lexlevi.sweapp.Common.URLs;
import com.example.lexlevi.sweapp.Interfaces.ChatServerAPI;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lexlevi on 3/19/17.
 */

public class ChatServerClient {

    private static ChatServerClient instance = null;
    public static ChatServerAPI api = null;

    protected ChatServerClient() {
        // Exists only to defeat instantiation.
    }

    public static ChatServerClient getInstance() {
        if (instance == null) {
            instance = new ChatServerClient();
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
