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
import com.news.theguardianapp.preference.LastIdPreference;
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
                new IdCheckerAsyncTask().execute();
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

    private  class IdCheckerAsyncTask extends AsyncTask<Void, Void, String>{

        private final String mUrl =  "http://content.guardianapis.com/search?api-key=test&show-fields=thumbnail";
        @Override
        protected String doInBackground(Void... voids) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {

                URL url = new URL(mUrl);

                connection = (HttpURLConnection) url.openConnection();
                connection.connect();



                InputStream stream;// = connection.getInputStream();

                int status = connection.getResponseCode();



                if (status != HttpURLConnection.HTTP_OK)
                    stream = connection.getErrorStream();
                else
                    stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                }
                String json = buffer.toString();

                JSONObject jsonObject = new JSONObject(json);
                JSONArray result = jsonObject.getJSONObject("response").getJSONArray("results");

                return result.getJSONObject(0).getString("id");


            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            if(s != null && !s.equals(sLastArticleId)){
                createNotification();

            } handler.postDelayed(runnable, 10000);
        }
    }
}
