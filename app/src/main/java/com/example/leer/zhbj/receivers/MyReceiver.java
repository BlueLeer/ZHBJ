package com.example.leer.zhbj.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.leer.zhbj.NewsArticleActivity;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Leer on 2017/5/3.
 */

public class MyReceiver extends BroadcastReceiver {
    public static final String TAG = "MyReceiver-------";
    public static final String EXTRA_KEY = "url";
    private String mUrl;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "收到通知啦!");
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "onReceive - " + intent.getAction());
        String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
        if(extra != null){
            try {
                JSONObject jsonObject = new JSONObject(extra);
                mUrl = jsonObject.getString(EXTRA_KEY);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "onReceive ---url  " + mUrl);
        }

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
        }else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "收到了自定义消息。消息内容是：" + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            // 自定义消息不会展示在通知栏，完全要开发者写代码去处理
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "收到了通知");
            // 在这里可以做些统计，或者做些其他工作
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "用户点击打开了通知");
            // 在这里可以自己写代码去定义用户点击后的行为
            if(mUrl != null){
                Intent i = NewsArticleActivity.newIntent(context, mUrl);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }

        } else {
            Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
    }
}
