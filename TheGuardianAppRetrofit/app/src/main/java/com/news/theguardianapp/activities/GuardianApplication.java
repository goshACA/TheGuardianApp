package com.news.theguardianapp.activities;

import android.app.Application;
import android.content.Context;

import com.news.theguardianapp.database.DbWrapper;
import com.news.theguardianapp.utils.NetworkUtils;

public class GuardianApplication extends Application {

    public static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        DbWrapper.create(this);
        NetworkUtils.setContext(this);
        mContext = this;
    }


}
