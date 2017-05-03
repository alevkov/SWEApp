package com.example.lexlevi.sweapp.Singletons;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by sphota on 5/1/17.
 */

public class Storage extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UniteamDatabase";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NOTIF = "notif";

    private final String EMAIL = "UniteamUserEmail";
    private final String USER_ID = "WalkmanAudioUserId";

    private static final String KEY_CHAT_ID = "chat_id";
    private static final String KEY_NUMBER_NEW = "number_new";
    private static final String KEY_GROUP_ID = "group_id";

    private static final String CREATE_TABLE_NOTIFS = "CREATE TABLE "
            + TABLE_NOTIF + "(" + KEY_CHAT_ID + " TEXT PRIMARY KEY ,"
            + KEY_GROUP_ID + " TEXT," + KEY_NUMBER_NEW
            + " INTEGER"+ ")";

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
        db.execSQL(CREATE_TABLE_NOTIFS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIF);
        onCreate(db);
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

    // Notifs
    public synchronized void addNewNotifForChat(String chatId, String groupId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NOTIF + " WHERE "
                + KEY_CHAT_ID + " = '" + chatId + "' AND "
                + KEY_GROUP_ID + " = '" + groupId + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        Log.d("ADD NOTIF", ">>> CALLED >>>");
        if (c == null || c.getCount() < 1) {
            ContentValues values = new ContentValues();
            values.put(KEY_NUMBER_NEW, 1);
            values.put(KEY_CHAT_ID, chatId);
            values.put(KEY_GROUP_ID, groupId);
            db.insert(TABLE_NOTIF, null, values);
        } else {
            c.moveToFirst();
            Integer i = c.getInt(c.getColumnIndex(KEY_NUMBER_NEW));
            ContentValues values = new ContentValues();
            i = i + 1;
            values.put(KEY_NUMBER_NEW, i);
            db.update(TABLE_NOTIF, values, KEY_CHAT_ID + " = ?" + " AND " + KEY_GROUP_ID + " = ?", new String[] { chatId, groupId });
        }
    }

    public synchronized void clearNotifsForChat(String chatId, String groupId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTIF, KEY_CHAT_ID + " = ?" + " AND " + KEY_GROUP_ID + " = ?", new String[] { chatId, groupId });
    }

    public synchronized Integer fetchNotifForChat(String chatId, String groupId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NOTIF + " WHERE "
                + KEY_CHAT_ID + " = '" + chatId + "' AND "
                + KEY_GROUP_ID + " = '" + groupId + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null && c.getCount() > 0)
            c.moveToFirst();
        else
            return null;
        return c.getInt(c.getColumnIndex(KEY_NUMBER_NEW));
    }
}
