package com.example.leer.zhbj.homecontentview.newschildrenview;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.leer.zhbj.HttpData.NewsChildren;
import com.example.leer.zhbj.HttpData.NewsData;
import com.example.leer.zhbj.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leer on 2017/4/10.
 */

public class NewsPager extends BasePager {

    private ViewPager vp_news_details;
    private TabPageIndicator indicator;
    private NewsData mNewsData;
    private List<String> mNewsTabs;
    private List<NewsDetailView> mNewsDetailViews;

    private NewsChildren[] mNewsChildrenData;

    @ViewInject(R.id.ib_next_item)
    private ImageButton ib_next_item;

    public NewsPager(Activity activity) {
        super(activity);
    }



    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.view_news_pager_news, null);
        vp_news_details = (ViewPager) view.findViewById(R.id.vp_news_details);

        indicator = (TabPageIndicator) view.findViewById(R.id.indicator);

        //注入view,如果是activity的话,直接传入view就可以了
        ViewUtils.inject(this,view);

        //注意:在viewpagerindicator还没有填充数据的时候,应该将它的可见性设置成GONE!!!!
        indicator.setVisibility(View.GONE);
        ib_next_item.setVisibility(View.GONE);

        System.out.print("git测试!!!!!");
        System.out.print("git测试!!!!!");
        System.out.print("git测试!!!!!");
        System.out.print("git测试!!!!!");

        return view;
    }

    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public CharSequence getPageTitle(int position) {
            return mNewsTabs.get(position);
        }

        @Override
        public int getCount() {
            return mNewsTabs.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            NewsDetailView newsDetailView = mNewsDetailViews.get(position);
            //每一个新闻详情页面被创建的时候,同时应该加载数据
            newsDetailView.initData();
            View view = mNewsDetailViews.get(position).getView();
            container.addView(view);
            return view;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }


    public void setData(NewsData newsData) {
        this.mNewsData = newsData;
        this.mNewsChildrenData = mNewsData.data[0].children;
        initData();
    }

    @Override
    public void initData() {

        if (mNewsData != null) {
            mNewsTabs = new ArrayList<>();
            for (int i = 0; i < mNewsData.data[0].children.length; i++) {
                mNewsTabs.add(mNewsData.data[0].children[i].title);
            }
        }

        mNewsDetailViews = new ArrayList<>();
        for (int i = 0; i < mNewsTabs.size(); i++) {
            NewsDetailView newsDetailView = new NewsDetailView(mActivity,mNewsChildrenData[i]);
            mNewsDetailViews.add(newsDetailView);
        }

        //得到数据以后,填充布局
        vp_news_details.setAdapter(new MyPagerAdapter());

        //将indicator和viewpager进行绑定
        indicator.setViewPager(vp_news_details);
        //当数据加载完毕时,将indicator设置为可见
        indicator.setVisibility(View.VISIBLE);
        ib_next_item.setVisibility(View.VISIBLE);

        //设置indicator右边的小图标点击时切换到下一页的功能
//        ib_next_item.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int currentIndex = vp_news_details.getCurrentItem();
//                currentIndex++;
//                vp_news_details.setCurrentItem(currentIndex);
//            }
//        });

        indicator.setCurrentItem(0);
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            SlidingFragmentActivity slidingFragmentActivity = (SlidingFragmentActivity) mActivity;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    slidingFragmentActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                } else {
                    slidingFragmentActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //事件注入
    @OnClick(R.id.ib_next_item)
    public void nextPage(View view){
        int currentIndex = vp_news_details.getCurrentItem();
        currentIndex++;
        vp_news_details.setCurrentItem(currentIndex);
    }
}
