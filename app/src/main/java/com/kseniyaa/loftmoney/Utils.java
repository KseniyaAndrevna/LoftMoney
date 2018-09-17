package com.kseniyaa.loftmoney;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utils {

    public static String getTokenValue(SharedPreferences sharedPreferences, Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String auth_token = sharedPreferences.getString(AuthActivity.SAVE_TOKEN, "");
        return auth_token;
    }

    public static void saveToken(String token, SharedPreferences sharedPreferences, Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AuthActivity.SAVE_TOKEN, token);
        editor.apply();
    }
}
