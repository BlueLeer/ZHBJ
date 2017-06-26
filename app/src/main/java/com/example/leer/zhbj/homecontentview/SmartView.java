package com.example.leer.zhbj.homecontentview;

import android.app.Activity;
import android.view.View;

import com.example.leer.zhbj.R;

/**
 * Created by Leer on 2017/4/5.
 */

public class SmartView extends BaseView {
    public SmartView(Activity activity) {
        super(activity);
    }

    @Override
    protected void initTitle() {
        setTitle("智慧服务");
    }

    @Override
    public void initView() {
        View view = View.inflate(getActivity(), R.layout.view_smart,null);
        setView(view);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initMenu() {

    }
}


