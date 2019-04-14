package com.news.theguardianapp.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.news.theguardianapp.entity.Article;

import java.util.List;

@Dao
public interface ArticleDao {
    @Query("select * from articles")
    List<Article> findAll();

    @Query("select * from articles" +
            " where isPined = 1")
    List<Article> findPinnedArticles();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Article article);

}
