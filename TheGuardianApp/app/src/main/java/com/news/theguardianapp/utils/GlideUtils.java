package com.news.theguardianapp.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class GlideUtils {
    private static Context sContext;

    public static void setContext(Context context) {
        sContext = context;
    }

    public static void loadImage(ImageView imageView, String imageUrl) {
        if (NetworkUtils.isInternettAvailable())
            Glide.with(sContext).load(imageUrl)
                    .into(imageView);
    }
}
