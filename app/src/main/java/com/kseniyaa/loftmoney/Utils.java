package com.kseniyaa.loftmoney;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utils {
    private static final String SAVE_TOKEN = "token";

    public static String getTokenValue( Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(SAVE_TOKEN, "");
    }

    public static void saveToken(String token, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SAVE_TOKEN, token);
        editor.apply();
    }
}
