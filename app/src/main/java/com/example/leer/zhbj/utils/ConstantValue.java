package com.example.leer.zhbj.utils;

/**
 * Created by Leer on 2017/4/2.
 */

public class ConstantValue {
    public static final String IS_FIRST_ENTER = "is_first_enter";
    //genymotion模拟器域名
    public static final String HTTP_ROOT_ADDRESS = "http://10.0.3.2:8080/zhbj";

    //花生壳域名
//    public static final String HTTP_ROOT_ADDRESS = "http://16tk329255.iok.la:26456/zhbj";

    public static final String HTTP_NEWS_ADDRESS = HTTP_ROOT_ADDRESS+"/categories.json";

    public static final String NEWS_CENTER_CATEGORY = "news_center_category";

    //是否是模拟器
    public static boolean isEmulator = true;

    //"新闻"下"组图"的服务器数据接口
    public static final String HTTP_PHOTOS_ADDRESS = HTTP_ROOT_ADDRESS+"/photos/photos_1.json";

}
