package com.news.theguardianapp.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.news.theguardianapp.database.ThumbnailConverter;

import java.io.Serializable;

@Entity(tableName = "articles")
public class Article implements Serializable{
    @NonNull
    @PrimaryKey()
    @SerializedName("id")
    private String id;
    @SerializedName("webTitle")
    private String title;
    @SerializedName("sectionName")
    private String category;
    @SerializedName("fields")
    @TypeConverters(ThumbnailConverter.class)
    private Fields thumbnail;
    private boolean isPined = false;
    public Article(){}
    @Ignore
    public Article(String id, String title,  String category){
        this.id = id;
        this.title = title;
        this.category = category;
    }

    @Override
    public boolean equals(Object obj) {
       if(!(obj instanceof Article))
           return false;
       return id.equals(((Article) obj).id);
    }

    @Override
    public String toString() {
        return title + "| "  + category + "|" + thumbnail.getImage();
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return thumbnail.getImage();
    }

    public boolean isPined() {
        return isPined;
    }

    public void setPined(boolean pined) {
        isPined = pined;
    }

    public Fields getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Fields image) {
        this.thumbnail = image;
    }

}
