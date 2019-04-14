package com.news.theguardianapp.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.news.theguardianapp.entity.Article;

@Database(entities = Article.class, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public  abstract  ArticleDao mArticleDao();

}
