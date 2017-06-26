package com.example.leer.zhbj;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import com.example.leer.zhbj.utils.ConstantValue;
import com.example.leer.zhbj.utils.SPUtils;

import cn.jpush.android.api.JPushInterface;

public class SplahActivity extends AppCompatActivity {

    private RelativeLayout rl_splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splah);
        rl_splash = (RelativeLayout) findViewById(R.id.rl_splash);
        startAnim();
        ConstantValue.isEmulator = isEmulator(this);


    }

    private void startAnim() {
        //旋转动画
        RotateAnimation rorateAnim = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rorateAnim.setDuration(1000);
        rorateAnim.setFillAfter(true);

        //缩放动画
        ScaleAnimation scaleAnim = new ScaleAnimation(0f, 1.0f, 0f, 1.0f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnim.setDuration(1000);
        scaleAnim.setFillAfter(true);

        //淡出动画
        AlphaAnimation alphaAnim = new AlphaAnimation(0f, 1.0f);
        alphaAnim.setDuration(2000);
        alphaAnim.setFillAfter(true);

        AnimationSet aniSet = new AnimationSet(true);
        aniSet.addAnimation(rorateAnim);
        aniSet.addAnimation(scaleAnim);
        aniSet.addAnimation(alphaAnim);


        rl_splash.setAnimation(aniSet);
        rl_splash.startAnimation(aniSet);

        aniSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                //动画结束以后,应该跳转页面
                boolean isFirstEnter = SPUtils.getBoolean(SplahActivity.this, ConstantValue.IS_FIRST_ENTER, true);
                //初次进入应用的时候,应该跳转到新手引导页面
                if (isFirstEnter) {
                    //跳转到新手引导页面
                    Intent i = GuideActivity.newIntent(SplahActivity.this);
                    startActivity(i);
                } else {
                    //非初次进入应用,应该跳转到主页
                    Intent i = HomeActivity.newIntent(SplahActivity.this);
                    startActivity(i);
                }

                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    //判断是模拟器还是真机
    public static boolean isEmulator(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            if (imei != null && imei.equals("000000000000000")) {
                return true;
            }
            return (Build.MODEL.equals("sdk")) || (Build.MODEL.equals("google_sdk"));
        } catch (Exception ioe) {

        }
        return false;
    }
}
