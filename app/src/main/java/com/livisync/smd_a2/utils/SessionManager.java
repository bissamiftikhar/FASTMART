package com.livisync.smd_a2.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "FastMartSession";
    private static final String KEY_UID = "uid";
    private static final String KEY_NAME = "name";
    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_LOGGED_IN = "isLoggedIn";
    private static final String KEY_THEME = "theme";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveSession(String uid, String name, String account) {
        editor.putString(KEY_UID, uid);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_ACCOUNT, account);
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.apply();
    }

    public void clearSession() {
        editor.putString(KEY_UID, null);
        editor.putString(KEY_NAME, null);
        editor.putString(KEY_ACCOUNT, null);
        editor.putBoolean(KEY_LOGGED_IN, false);
        editor.apply();
    }

    public boolean isLoggedIn() { return prefs.getBoolean(KEY_LOGGED_IN, false); }
    public String getUid() { return prefs.getString(KEY_UID, null); }
    public String getName() { return prefs.getString(KEY_NAME, null); }
    public String getAccount() { return prefs.getString(KEY_ACCOUNT, null); }

    public void setTheme(String theme) { editor.putString(KEY_THEME, theme).apply(); }
    public String getTheme() { return prefs.getString(KEY_THEME, "light"); }
}