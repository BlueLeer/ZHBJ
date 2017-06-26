package com.example.leer.zhbj.utils;

import android.content.Context;

/**将从网络获取的数据缓存到sp当中
 * 以url为链接，以该url下面的网络数据为缓存对象，进行缓存
 * Created by Leer on 2017/4/10.
 */

public class CacheUtils {

    /**将url作为key,将该url获取到的json数据存入到sp当中去
     * @param url 地址
     * @param json json数据
     * @param ctx 上下文环境
     */
    public static void setCache(String url, String json, Context ctx){
        SPUtils.putString(ctx,url,json);
    }

    /** 从sp当中获取缓存的数据
     * @param url 获取缓存数据的key,也就是从服务器端获取数据所用到的url地址
     * @param ctx 上下文环境
     * @return 返回该url(key)作为key缓存到sp当中的数据
     */
    public static String getCache(String url,Context ctx){
        return SPUtils.getString(ctx,url,null);
    }
}
