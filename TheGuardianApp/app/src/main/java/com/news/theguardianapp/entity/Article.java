package com.news.theguardianapp.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity(tableName = "articles")
public class Article implements Serializable{
    @NonNull
    @PrimaryKey()
    private String id;
    private String title;
    private String category;
    private String imageUrl;
    private boolean isPined = false;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] imageBytes;

    @Ignore
    public Article(){}
    @Ignore
    public Article(String id,String title,  String category){
        this.id = id;
        this.title = title;
        this.category = category;
    }

    public Article(String id, String title, String imageUrl, String category){
        this(id, title, category);
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object obj) {
       if(!(obj instanceof Article))
           return false;
       return id.equals(((Article) obj).id);
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



    @Override
    public String toString() {
        return title + "| " + "| " + category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isPined() {
        return isPined;
    }

    public void setPined(boolean pined) {
        isPined = pined;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }
}
