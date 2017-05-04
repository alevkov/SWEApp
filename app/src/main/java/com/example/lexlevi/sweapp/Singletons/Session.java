package com.example.lexlevi.sweapp.Singletons;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.lexlevi.sweapp.LoginActivity;
import com.example.lexlevi.sweapp.Models.Group;
import com.example.lexlevi.sweapp.Models.User;

import java.util.List;

public class Session {

    private static Session instance = null;
    private User currentUser;
    private List<Group> currentUserGroups;
    private Context ctx;

    public boolean inMessageView = false;

    protected Session() { }

    public static Session shared() {
        if(instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public User setCurrentUser(User u) {
        currentUser = u;
        setEmail(u.getEmail());
        setUserId(u.getId());
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
        return instance != null && valid();
    }

    public void setContext(Context ctx) {
        Storage.getInstance(ctx).setSettingsWithContext(ctx);
        this.ctx = ctx;
    }

    private void setEmail(String t) {
        Storage.getInstance(ctx).persistEmail(t);
    }

    private void setUserId(String id) {
        Storage.getInstance(ctx).persistUserId(id);
    }

    public String getEmail() {
        return Storage.getInstance(ctx).fetchEmail();
    }

    public String getUserId() {
        return Storage.getInstance(ctx).fetchUserId();
    }

    public boolean valid() {
        return Storage.getInstance(ctx).emailExists() && Storage.getInstance(ctx).userIdExists();
    }

    public void invalidateCredentials() {
        currentUser = null;
        Storage.getInstance(ctx).destroyCredentials();
    }
}
