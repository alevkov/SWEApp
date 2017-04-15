package com.example.lexlevi.sweapp.Singletons;

import com.example.lexlevi.sweapp.Models.Group;
import com.example.lexlevi.sweapp.Models.User;

import java.util.List;

public class Session {

    private static Session instance = null;
    private User currentUser;
    private List<Group> currentUserGroups;

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

    public List<Group> setCurrentUserGroups(List<Group> groups) {
        currentUserGroups = groups;
        return currentUserGroups;
    }

    public User user() {
        return currentUser;
    }

    public List<Group> groups() { return currentUserGroups; }

    private boolean isUserInSession() {
        return instance != null;
    }
}
