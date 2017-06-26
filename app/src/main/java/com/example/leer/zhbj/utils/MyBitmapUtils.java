package com.example.leer.zhbj.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.leer.zhbj.R;

/**
 * Created by Leer on 2017/4/30.
 */

public class MyBitmapUtils {
    private static final String TAG = "MyBitmapUtils";
    private static int loadImageCount = 0;
    private Context mContext;

    public MyBitmapUtils(Context context) {
        this.mContext = context;
    }

    public void display(ImageView container, String url) {
        MyAsyncTask myAsyncTask = new MyAsyncTask(container, url);
        myAsyncTask.execute();

    }

    //三个泛型参数的含义:
    //1.doInBackground方法接收的参数类型
    //2.onProgressUpdate方法接收的参数类型
    //3.doInBackground方法的返回值类型及onPostExecute方法的方法接收参数类型
    private class MyAsyncTask extends AsyncTask<Void, Integer, Bitmap> {
        private ImageView iv;
        private String urlAddress;
        private Bitmap bitmap;

        public MyAsyncTask(ImageView imageView, String url) {
            this.iv = imageView;
            this.urlAddress = url;
        }

        //预加载执行的任务,在doInBackground之前运行,用于进行一些界面上的初始化操作,
        // 例如显示进度对话框等等运行在主线程
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //设置默认显示的图片
            Bitmap bp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.news_pic_default);
            iv.setImageBitmap(bp);
            iv.setTag(urlAddress);
        }

        //在后台执行的任务，运行在子线程
        //返回任务执行结束后得到的结果
        @Override
        protected Bitmap doInBackground(Void... params) {
            //先从内存加载数据
            bitmap = MemoryBitmapCache.getCache(urlAddress);
            if (bitmap == null) {
                //再从sd卡加载数据
                bitmap = SdcardBitmapCache.getCache(mContext, urlAddress);
                if (bitmap == null) {
                    //最后从网络加载数据
                    bitmap = NetBitmapCache.downloadBitmap(urlAddress);
                    SdcardBitmapCache.putCache(mContext, urlAddress, bitmap);
                    MemoryBitmapCache.putCache(urlAddress, bitmap);
                    Log.d(TAG, "从网络加载图片啦----------");
                } else {
                    MemoryBitmapCache.putCache(urlAddress, bitmap);
                    Log.d(TAG, "从本地加载图片啦----------");
                }
            } else {
                Log.d(TAG, "从内存加载图片啦----------");
            }
            return bitmap;
        }

        //在doInBackgroud()方法中调用publishProgress()方法更新进度条的操作时,这个方法会得到调用
        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        //接收doInBackgroud()方法返回的结果,在这里操作UI
        @Override
        protected void onPostExecute(Bitmap bitmap) {
//            Log.d(TAG, "onPostExecute 执行成功!!!!!!!!!!!!!!!!!!");
            String tag = (String) iv.getTag();
            if (bitmap != null && urlAddress == tag) {
                iv.setImageBitmap(bitmap);
                loadImageCount++;
//                Log.d(TAG,"已加载的图片的数量是 : "+loadImageCount);
            }
        }
    }

}
