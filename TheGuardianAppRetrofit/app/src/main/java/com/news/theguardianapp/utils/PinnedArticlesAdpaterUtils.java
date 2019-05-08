package com.news.theguardianapp.utils;

import com.news.theguardianapp.adapter.FeedAdapter;

public class PinnedArticlesAdpaterUtils {

    private static FeedAdapter sFeedAdapter;

    public static FeedAdapter getFeedAdapter() {
        return sFeedAdapter;
    }

    public static void setFeedAdapter(FeedAdapter feedAdapter) {
        sFeedAdapter = feedAdapter;
    }
}
