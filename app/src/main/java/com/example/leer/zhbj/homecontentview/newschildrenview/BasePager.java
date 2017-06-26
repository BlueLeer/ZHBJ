package com.example.leer.zhbj.homecontentview.newschildrenview;

import android.app.Activity;
import android.content.Context;
import android.view.View;

/**
 * Created by Leer on 2017/4/10.
 */

public abstract class BasePager {
    protected Context mActivity;
    protected View mView;

    public BasePager(Activity activity) {
        //注意顺序,这里需要先将activity赋值,在initView()方法中需要用到activity
        this.mActivity = activity;
        this.mView = initView();
    }

    public abstract View initView();

    public abstract void initData();

    public View getView() {
        if (mView != null) {
            return mView;
        } else {
            return null;
        }
    }

    public Context getActivity() {
        if (mActivity != null) {
            return mActivity;
        } else {
            return null;
        }
    }
}
