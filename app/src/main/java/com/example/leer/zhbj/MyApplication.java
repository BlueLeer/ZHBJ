package com.example.leer.zhbj;

import android.app.Application;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Leer on 2017/5/3.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

    }
}
