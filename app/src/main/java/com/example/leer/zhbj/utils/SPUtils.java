package com.example.leer.zhbj.utils;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Leer on 2017/4/2.
 */

public class SPUtils {

    /**
     * @param context 上下文环境
     * @param key     存储boolean值对应的key
     * @param value   需要存储的boolean值
     */
    public static void putBoolean(Context context, String key, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(
                context).edit().putBoolean(key, value).commit();
    }

    /**
     * @param context   上下文环境
     * @param key       获取boolean值的key
     * @param delfValue 默认的值
     * @return 存贮在sp中的boolean值
     */
    public static boolean getBoolean(Context context, String key, boolean delfValue) {
        return PreferenceManager.getDefaultSharedPreferences(
                context).getBoolean(key, delfValue);
    }

    /**
     * @param context 上下文环境
     * @param key     存储int值对应的key
     * @param value   需要存储的int值
     */
    public static void putInt(Context context, String key, int value) {
        PreferenceManager.getDefaultSharedPreferences(
                context).edit().putInt(key, value).commit();
    }

    /**
     * @param context   上下文环境
     * @param key       获取int值对应的key
     * @param delfValue 默认的值
     * @return 从sp中获取到的int值
     */
    public static int getInt(Context context, String key, int delfValue) {
        return PreferenceManager.getDefaultSharedPreferences(
                context).getInt(key, delfValue);
    }

    /**
     * @param context   上下文环境
     * @param key       获取string值所需要的key
     * @param delfValue 默认的值
     * @return 默认返回的string类型的值
     */
    public static String getString(Context context, String key, String delfValue) {
        return PreferenceManager.getDefaultSharedPreferences(
                context).getString(key, delfValue);
    }

    public static void putString(Context context, String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(
                context).edit().putString(key, value).commit();
    }
}
