package com.news.theguardianapp.networking;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.news.theguardianapp.entity.Article;
import com.news.theguardianapp.utils.StringUtils;

import java.lang.reflect.Type;

public class ArticleSerialize implements JsonDeserializer<Article> {



    public interface CallServiceForImageByteArray{
        void callServiceForArray(Article article);
    }


    @Override
    public Article deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();


        Article article = new Article(StringUtils.removeQuotes(jsonObject.get("id").toString()),
                StringUtils.removeQuotes(jsonObject.get("webTitle").toString()),
                StringUtils.removeQuotes(jsonObject.get("sectionName").toString()));
        if(   jsonObject.get("fields") != null){
        String imageUrl =  StringUtils.removeQuotes(jsonObject.get("fields").getAsJsonObject().get("thumbnail").toString());
        article.setImageUrl(imageUrl);}
        return article;
    }




}