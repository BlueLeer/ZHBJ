package com.example.leer.zhbj.utils;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

/**
 * 内存缓存
 * 应用运行时,数据就是缓存在内存当中的,未被回收的对象都是放在内存当中的
 * 但是,每个应用的运行内存大小是系统提前预分配好了的,如果一味的向内存中添加大尺寸的图片很容易使内存溢出(OOM)
 * <p>
 * 方案一:利用HasMap存贮
 * 方案二:将Bitmap用SoftReference包装起来,利用HasMap存贮
 * 方案三:利用LruCache存贮
 * Created by Leer on 2017/5/2.
 */

public class MemoryBitmapCache {
    //存在内存当中的是一个个Bitmap图片,将他们设置成软引用
    //从 Android 2.3 (API Level 9)开始，
    // 垃圾回收器会更倾向于回收持有软引用或弱引用的对象，这让软引用和弱引用变得不再可靠。
//    static HashMap<String, SoftReference<Bitmap>> bitmapHashMap = new HashMap<>();

    static MyLruCache<String, Bitmap> mLruBitmapCache = new MyLruCache<>((int) (Runtime.getRuntime().maxMemory() / 8));

    private static class MyLruCache<T, K> extends LruCache<String, Bitmap> {
        public MyLruCache(int maxSize) {
            super(maxSize);
            Log.d("xxxxxxxxx", "maxSize : " + maxSize);
        }

        //必须重写这个方法,用来计算每张Bitmap图片的大小
        @Override
        protected int sizeOf(String key, Bitmap value) {
            //每张图片的大小等于每行的Bytes数乘以行数(高度)
            int size = value.getRowBytes() * value.getHeight();
            return size;
        }
    }

    public static void putCache(String url, Bitmap bitmap) {
        String key = MD5util.encodingMD5(url);

        if (bitmap != null) {
//            SoftReference<Bitmap> bit = new SoftReference<Bitmap>(bitmap);
//            bitmapHashMap.put(key, bit);

            mLruBitmapCache.put(key, bitmap);
        }

    }

    static LruCache<String, String> m = new LruCache<String, String>(22) {
        @Override
        protected int sizeOf(String key, String value) {
            return super.sizeOf(key, value);
        }
    };

    public static Bitmap getCache(String url) {
        Bitmap bitmap = null;
        String key = MD5util.encodingMD5(url);
//        SoftReference<Bitmap> bit = bitmapHashMap.get(key);
//        if(bit != null){
//            bitmap = bit.get();
//        }

        bitmap = mLruBitmapCache.get(key);
        return bitmap;
    }

}
