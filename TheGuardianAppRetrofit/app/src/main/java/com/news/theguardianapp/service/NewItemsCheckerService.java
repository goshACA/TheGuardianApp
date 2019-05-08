package com.news.theguardianapp.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.news.theguardianapp.R;
import com.news.theguardianapp.activities.GuardianApplication;
import com.news.theguardianapp.entity.ApiResponse;
import com.news.theguardianapp.preference.LastIdPreference;
import com.news.theguardianapp.retrofit.ApiManager;
import com.news.theguardianapp.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewItemsCheckerService  extends Service {

    private String sLastArticleId = PreferenceManager.getDefaultSharedPreferences(GuardianApplication.mContext).getString(LastIdPreference.LAST_ID, "none");
    private static Handler handler;
    private static Runnable runnable;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                if (NetworkUtils.isInternettAvailable())
                    checkLastIdNotChangedAndCreateNotification();
            }
        };

        handler.postDelayed(runnable, 15000);
    }


    private  void createNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           createChannelNotification();
        }
        else{
            createSimpleNotification();
        }
    }


    private  void createSimpleNotification(){

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder; notificationBuilder = new NotificationCompat.Builder(this, "theguardian")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("TheGuardianApp")
                .setContentText("Check new articles")
                .setAutoCancel(true)
                .setColor(0xffff7700)
                .setVibrate(new long[]{100, 100, 100, 100})
                .setPriority(Notification.PRIORITY_MAX)
                .setSound(defaultSoundUri);


        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        mNotificationManager.notify(new Random().nextInt(1000), notificationBuilder.build());


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannelNotification(){
        String chanel_id = "3000";
        CharSequence name = "Channel";
        String description = "TheGuardianChannel";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(chanel_id, name, importance);
        mChannel.setDescription(description);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        mChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder  notificationBuilder = new NotificationCompat.Builder(this, "theguardian")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("TheGuardianApp")
                .setContentText("Check new articles")
                .setAutoCancel(true)
                .setColor(0xffff7700)
                .setVibrate(new long[]{100, 100, 100, 100})
                .setPriority(Notification.PRIORITY_MAX)
                .setSound(defaultSoundUri);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(mChannel);
        notificationManager.notify(new Random().nextInt(1000), notificationBuilder.build());

    }

    private void checkLastIdNotChangedAndCreateNotification(){
        final Call<ApiResponse> apiResponseCall = ApiManager.getApiClient().searchArticles("test", "thumbnail", 1);
        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.body() != null){
                  if(sLastArticleId != null && !sLastArticleId.equals(response.body().getResponse().getArticleList().get(0).getId())) {
                      createNotification();
                  }
                }  handler.postDelayed(runnable, 15000);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });

    }


}
