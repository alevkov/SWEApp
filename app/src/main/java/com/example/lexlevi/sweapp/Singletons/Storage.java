package com.example.lexlevi.sweapp.Singletons;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

/**
 * Created by sphota on 5/1/17.
 */

public class Storage extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UniteamDatabase";
    private static final int DATABASE_VERSION = 1;

    private final String EMAIL = "UniteamUserEmail";
    private final String USER_ID = "WalkmanAudioUserId";

    private Context ctx;
    private SharedPreferences settings;
    private static Storage ourInstance = null;

    Storage(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.ctx = context;
    }

    public static Storage getInstance(Context ctx) {
        if (ourInstance == null) {
            ourInstance = new Storage(ctx.getApplicationContext());
        }
        return ourInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void setSettingsWithContext(Context c) {
        this.settings = PreferenceManager.getDefaultSharedPreferences(c);
    }

    public boolean emailExists() {
        if (!this.settings.contains(EMAIL)) return false;
        String value = this.settings.getString(EMAIL, null);
        return value != null;
    }

    public boolean userIdExists() {
        if (!this.settings.contains(USER_ID)) return false;
        String value = this.settings.getString(USER_ID, null);
        return value != null;
    }

    public void persistEmail(String t) {
        SharedPreferences.Editor prefsEditor;
        prefsEditor = this.settings.edit();
        prefsEditor.putString(EMAIL, t);
        prefsEditor.apply();
    }

    public void persistUserId(String id) {
        SharedPreferences.Editor prefsEditor;
        prefsEditor = this.settings.edit();
        prefsEditor.putString(USER_ID, id);
        prefsEditor.apply();
    }

    public String fetchEmail() {
        if (this.emailExists()) {
            String authTokenString = settings.getString(EMAIL, null);
            return authTokenString;
        }
        return null;
    }

    public String fetchUserId() {
        if (this.userIdExists()) {
            String userIdString = settings.getString(USER_ID, null);
            return userIdString;
        }
        return null;
    }

    public void destroyCredentials() {
        SharedPreferences.Editor prefsEditor;
        prefsEditor = this.settings.edit();
        prefsEditor.putString(EMAIL, null);
        prefsEditor.putString(USER_ID, null);
        prefsEditor.commit();
    }
}
