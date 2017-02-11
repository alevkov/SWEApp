package com.example.lexlevi.sweapp.Singletons;

import com.example.lexlevi.sweapp.Models.User;

public class UserSession {
    private static UserSession instance = null;
    private User currentUser;

    protected UserSession() {
        // Exists only to defeat instantiation.
    }

    public static UserSession getInstance() {
        if(instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public User setCurrentUser(User u) {
        currentUser = u;
        return currentUser;
    }

    private boolean isUserInSession()
    {
        return instance != null;
    }
}
