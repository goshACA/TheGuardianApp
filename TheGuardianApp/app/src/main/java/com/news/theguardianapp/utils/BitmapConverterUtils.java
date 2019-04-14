package com.news.theguardianapp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public final class BitmapConverterUtils {
    private BitmapConverterUtils(){}


    public static byte[] convertBitmapToByteArray(Bitmap bitmap){
        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /* Ignored for PNGs */, blob);
        byte[] bitmapdata = blob.toByteArray();
        return bitmapdata;
    }



    public static Bitmap convertByteArrayToBitmap(byte[] array){
        return BitmapFactory.decodeByteArray(array, 0, array.length);
    }



}
