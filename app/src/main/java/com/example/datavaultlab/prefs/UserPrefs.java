package com.example.datavaultlab.prefs;

import android.content.Context;
import android.content.SharedPreferences;

public final class UserPrefs {

    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_USERNAME  = "key_username";
    private static final String KEY_LANGUAGE  = "key_language";
    private static final String KEY_THEME     = "key_theme";

    private UserPrefs() {}

    public static boolean save(Context context, String username,
                               String language, String theme, boolean sync) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit()
                .putString(KEY_USERNAME, username)
                .putString(KEY_LANGUAGE, language)
                .putString(KEY_THEME,    theme);

        if (sync) return editor.commit();
        editor.apply();
        return true;
    }

    public static Snapshot load(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return new Snapshot(
                prefs.getString(KEY_USERNAME, ""),
                prefs.getString(KEY_LANGUAGE, "en"),
                prefs.getString(KEY_THEME,    "light")
        );
    }

    public static void clear(Context context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit().clear().apply();
    }

    public static final class Snapshot {
        public final String username;
        public final String language;
        public final String theme;

        public Snapshot(String username, String language, String theme) {
            this.username = username;
            this.language = language;
            this.theme    = theme;
        }
    }
}