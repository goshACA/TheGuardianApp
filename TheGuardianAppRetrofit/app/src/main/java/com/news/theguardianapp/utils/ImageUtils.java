package com.news.theguardianapp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;


public final class ImageUtils {

    private static final String PREFIX = "https://media.guim.co.uk/";

    public static void createDirectoryAndSaveFile(Bitmap img, String imgName) {


        File direct = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM);;

        if (!direct.exists()) {
            direct.mkdirs();
        }


        File file = new File(direct, encodeImgName(imgName));
        if (file.exists()) {
            file.delete();
        }

        try {
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            img.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap loadImageFromFolder(String imgName) {
        File folder =  Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM);
        if (!folder.exists())
            return null;
        File file = new File(folder, encodeImgName(imgName));
        if (!file.exists())
            return null;
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    private static String encodeImgName(String imgName){
        imgName = imgName.replaceFirst(PREFIX, "");
        return imgName.replace('/', '.');
    }

}

