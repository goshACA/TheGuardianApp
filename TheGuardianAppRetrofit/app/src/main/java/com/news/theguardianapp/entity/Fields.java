package com.news.theguardianapp.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Fields implements Serializable {
    @SerializedName("thumbnail")
    private  String image;

    public Fields(){}
    public Fields(String image){this.image = image;}

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }
}
