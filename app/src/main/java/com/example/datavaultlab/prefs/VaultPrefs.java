package com.example.datavaultlab.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

public final class VaultPrefs {

    private static final String PREFS_NAME    = "vault_prefs";
    private static final String KEY_API_TOKEN = "vault_api_token";

    private VaultPrefs() {}

    private static SharedPreferences encrypted(Context context) throws Exception {
        MasterKey masterKey = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        return EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }

    public static void storeToken(Context context, String token) throws Exception {
        encrypted(context).edit().putString(KEY_API_TOKEN, token).apply();
    }

    public static String fetchToken(Context context) throws Exception {
        return encrypted(context).getString(KEY_API_TOKEN, "");
    }

    public static void clear(Context context) throws Exception {
        encrypted(context).edit().clear().apply();
    }
}