package com.news.theguardianapp.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.news.theguardianapp.entity.Article;
import com.news.theguardianapp.utils.ImageSetterAsyncTask;

public class ByteArrayDownloadService extends IntentService {


    public static void setSetAdapterData(SetAdapterData setAdapterData) {
        sSetAdapterData = setAdapterData;
    }

    public interface SetAdapterData{
       void setAdapterData(Article article);
   }

   private static SetAdapterData sSetAdapterData;
    public ByteArrayDownloadService() {
        super(ByteArrayDownloadService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent( @Nullable Intent intent) {
        if(intent.getSerializableExtra(Article.class.getSimpleName())!=null){
           new ImageSetterAsyncTask(sSetAdapterData).execute((Article) intent.getSerializableExtra(Article.class.getSimpleName()));
        }
    }
}
