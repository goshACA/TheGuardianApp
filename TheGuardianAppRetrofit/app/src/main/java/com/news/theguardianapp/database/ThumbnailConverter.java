package com.news.theguardianapp.database;

import android.arch.persistence.room.TypeConverter;

import com.news.theguardianapp.entity.Fields;

public class ThumbnailConverter{

    @TypeConverter
    public String fromThumbnailToString(Fields thumbnail){
        return thumbnail.getImage();
    }

    @TypeConverter
    public Fields fromStringToThumbnail(String imageUrl){
        return new Fields(imageUrl);
    }
}