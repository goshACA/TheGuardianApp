package com.news.theguardianapp.database;

import android.arch.persistence.room.Room;
import android.content.Context;

public class DbWrapper {
    private static AppDatabase sInstance = null;
    private static final String DATABASE_NAME = "guardian_articles";

    public static void create(Context context) {
        sInstance = Room.databaseBuilder(context,
                AppDatabase.class, DATABASE_NAME)
                .build();
    }

    public static AppDatabase getAppDatabase() {
        if (sInstance == null) {
            throw new IllegalStateException("Database not created");
        }

        return sInstance;
    }
}
