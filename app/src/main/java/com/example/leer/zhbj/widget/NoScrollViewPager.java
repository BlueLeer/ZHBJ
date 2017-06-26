package com.example.leer.zhbj.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 使用于应用主界面的viewpager
 * 五个条目处于ViewPager中,并且不响应滑动事件
 * Created by Leer on 2017/4/21.
 */

public class NoScrollViewPager extends ViewPager {
    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //不拦截子控件的事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    //接收到事件以后,什么也不做
    //在这里是不响应滑动事件
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return true;
    }
}
