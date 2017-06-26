package com.example.leer.zhbj.homecontentview.newschildrenview;

import android.app.Activity;
import android.view.View;

import com.example.leer.zhbj.R;

/**
 * Created by Leer on 2017/4/10.
 */

public class InteractionPager extends BasePager {
    public InteractionPager(Activity activity) {
        super(activity);
    }

    @Override
    public View initView() {
        return View.inflate(getActivity(),R.layout.view_news_pager_interaction,null);
    }

    @Override
    public void initData() {

    }
}
