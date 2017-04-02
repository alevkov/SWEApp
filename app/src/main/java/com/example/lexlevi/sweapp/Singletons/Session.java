package com.example.lexlevi.sweapp.Singletons;

import com.example.lexlevi.sweapp.Models.User;

public class Session {
    private static Session instance = null;
    private User currentUser;

    protected Session() {
        // Exists only to defeat instantiation.
    }

    public static Session shared() {
        if(instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public User setCurrentUser(User u) {
        currentUser = u;
        return currentUser;
    }

    public User user() {
        return currentUser;
    }

    private boolean isUserInSession() {
        return instance != null;
    }
}
