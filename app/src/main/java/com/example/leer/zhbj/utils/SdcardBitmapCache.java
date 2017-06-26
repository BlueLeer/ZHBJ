package com.example.leer.zhbj.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by Leer on 2017/5/1.
 */

public class SdcardBitmapCache {
    private static final String LOCAL_ROOT_PATH = "pictures";
    public static void putCache(Context context,String url,Bitmap bitmap){
        //在应用下面创建一个目录
        File file = context.getDir(LOCAL_ROOT_PATH,Context.MODE_PRIVATE);
        String bitmapFilePath = MD5util.encodingMD5(url);
        File bitmapFile = new File(file,bitmapFilePath);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(bitmapFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getCache(Context context,String url){
        Bitmap bitmap = null;
        File file = context.getDir(LOCAL_ROOT_PATH,Context.MODE_PRIVATE);
        File bitmapFile = new File(file,MD5util.encodingMD5(url));
        if (bitmapFile.exists()){
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(bitmapFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return bitmap;
    }
}
