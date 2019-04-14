package com.news.theguardianapp.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class LastIdPreference {

    public static final String PREF_NAME = "AppPreferences";
    public static final String LAST_ID = "last_id";


    public static void saveLastId(Context context, String lastId) {
        getSharedPreferences(context)
                .edit()
                .putString(LAST_ID, lastId)
                .apply();
    }



    private static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);

    }
}
