package com.news.theguardianapp.networking;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

;

public class JsonTask extends AsyncTask<Void, Void, JSONObject> {

    private static int pageNumber = 1;
    private final static String mUrl = "http://content.guardianapis.com/search?api-key=test&show-fields=thumbnail&page=";
    private HandleJson mHandleJson;

    private int mLoadNewItems = -1;

    public JsonTask(HandleJson handleJson) {
        mHandleJson = handleJson;
    }

    public JsonTask(HandleJson handleJson, int loadNewItems) {
        mHandleJson = handleJson;
        mLoadNewItems = loadNewItems;
    }

    protected void onPreExecute() {
        super.onPreExecute();

    }

    protected JSONObject doInBackground(Void... params) {


        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url;
            if (mLoadNewItems > -1) {
                url = new URL(mUrl.concat(mLoadNewItems + ""));
                if (pageNumber > 1)
                    --pageNumber;
            } else
                url = new URL(mUrl.concat(pageNumber + ""));
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();


            InputStream stream;

            int status = connection.getResponseCode();


            if (status != HttpURLConnection.HTTP_OK)
                stream = connection.getErrorStream();
            else
                stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            String json = buffer.toString();

            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getJSONObject("response");


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
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        if (result == null)
            return;
        mHandleJson.getNewPosts(result);
        ++pageNumber;
    }
}