package com.news.theguardianapp.utils;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import com.news.theguardianapp.entity.Article;
import com.news.theguardianapp.service.ByteArrayDownloadService;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageSetterAsyncTask extends AsyncTask<Article, Void, Article> {

   private ByteArrayDownloadService.SetAdapterData mAdapterSetNewData;

   public ImageSetterAsyncTask(ByteArrayDownloadService.SetAdapterData adapterSetNewData){
       mAdapterSetNewData = adapterSetNewData;
   }

    @Override
    protected Article doInBackground(Article... articles) {
        try {
            URL url = new URL(articles[0].getImageUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            articles[0].setImageBytes(BitmapConverterUtils.convertBitmapToByteArray( BitmapFactory.decodeStream(input)));
            return articles[0];
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Article article) {
        mAdapterSetNewData.setAdapterData(article);
    }
}