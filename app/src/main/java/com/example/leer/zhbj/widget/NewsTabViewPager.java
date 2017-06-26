package com.example.leer.zhbj.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 使用于"新闻"页面的ViewPager
 * 主要功能在于是否响应滑动事件
 * Created by Leer on 2017/4/21.
 */

public class NewsTabViewPager extends ViewPager {

    //记录点击时的初始X的位置
    private float startX;
    //记录点击时的初始Y的位置
    private float startY;

    public NewsTabViewPager(Context context) {
        super(context);
    }

    public NewsTabViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        //获取页面的总数
        int count = getAdapter().getCount();


        //请求父控件不要拦截事件,不拦截事件的时候,就会进入到该方法的执行逻辑中来
        //对于下面特殊的情况再决定是否拦截Touch事件的传递
        receiveTouchEvent();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = ev.getX();
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float disX = ev.getX() - startX;
                float disY = ev.getY() - startY;

                //判断是上下滑动还是左右滑动
                //左右滑动的时候
                if (Math.abs(disX) - Math.abs(disY) > 0) {
                    //当处于第一个页面并且向右滑动的时候
                    if (getCurrentItem() == 0 && disX > 0) {
                        unreceiveTouchEvent();
                    }
                    //当处于最后一个页面并且向左滑动的时候
                    if (getCurrentItem() == count - 1 && disX < 0){
                        unreceiveTouchEvent();
                    }
                }
                //上下滑动的时候,父控件应该拦截Touch事件的传递
                else {
                    unreceiveTouchEvent();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 请求父控件不要拦截Touch事件
     */
    public void receiveTouchEvent() {
        getParent().requestDisallowInterceptTouchEvent(true);
    }

    /**
     * 父控件拦截事件
     */
    public void unreceiveTouchEvent() {
        getParent().requestDisallowInterceptTouchEvent(false);
    }
}
