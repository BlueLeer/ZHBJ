package com.example.leer.zhbj.homecontentview;

import android.app.Activity;
import android.view.View;

/**
 * Created by Leer on 2017/4/5.
 */

public abstract class BaseView {

    private Activity mActivity;
    private String mTitle;
    private View mView;

    public Activity getActivity() {
        return mActivity;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public View getView() {
        return mView;
    }

    public void setView(View mView) {
        this.mView = mView;
    }

    public BaseView(Activity activity) {
        this.mActivity = activity;
        initView();
        initTitle();
    }

    protected abstract void initTitle();

    /**
     * 初始化页面,将其设置给mView
     */
    protected abstract void initView();

    public abstract void initData();

    public abstract void initMenu();
}
