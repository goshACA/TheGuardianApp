package com.news.theguardianapp.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Response {
    @SerializedName("results")
    private List<Article> articleList;

    public List<Article> getArticleList() {
        return articleList;
    }

    public void setArticleList(List<Article> articleList) {
        this.articleList = articleList;
    }
}
