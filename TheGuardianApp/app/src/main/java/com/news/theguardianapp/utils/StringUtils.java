package com.news.theguardianapp.utils;

public final class StringUtils {
    private StringUtils(){}

    public static String removeQuotes(String str){
        return str.replace('"', ' ').trim();
    }
}
