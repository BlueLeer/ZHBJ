package com.example.leer.zhbj.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Leer on 2017/4/30.
 */

public class NetBitmapCache {
    private static final String TAG = "NetBitmapCache";

    public static Bitmap downloadBitmap(String urlAddress) {
        Bitmap bitmap = null;
        HttpURLConnection connection = null;
        if (!TextUtils.isEmpty(urlAddress)) {
            try {
                URL url = new URL(urlAddress);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestMethod("GET");
                InputStream inputStream = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                byte a = 'a';
                if (a > 0x10) {
                    Log.d(TAG, "---=-----------a大于0x10 : " + a);
                    int i = 0x0f;
                    Log.d(TAG, "---=-----------ox10 : " + Integer.toHexString(0x0f));

                }else{
                    Log.d(TAG, "---=----------a小于于0x10 : " + a);
                }
                Log.d(TAG, "---=-----------a的byte是 : " + a);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

        }

        return bitmap;
    }

}
