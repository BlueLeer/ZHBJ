package com.example.leer.zhbj.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.leer.zhbj.homecontentview.BaseView;
import com.example.leer.zhbj.homecontentview.GovView;
import com.example.leer.zhbj.homecontentview.HomeView;
import com.example.leer.zhbj.homecontentview.NewsView;
import com.example.leer.zhbj.homecontentview.SettingView;
import com.example.leer.zhbj.homecontentview.SmartView;
import com.example.leer.zhbj.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leer on 2017/4/4.
 */

public class HomeContentFragment extends BaseFragment {

    private ImageView mIv_menu;
    private TextView mTv_title;
    private ViewPager mVp_home_content_container;
    private RadioGroup mRg_home_tab;
    private List<BaseView> mBaseViews;
    private ImageView mIv_photos_menu;
    /**
     * 刚刚进入主页面的时候默认选择的页面是"主页"
     */
    private int mSelectedId = 0;
    private SlidingFragmentActivity mSlidingFragmentActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = initView();
        initData();
        return view;
    }

    private void initData() {
        mBaseViews = new ArrayList<>();
        mBaseViews.add(new HomeView(getActivity()));
        mBaseViews.add(new NewsView(getActivity(), mIv_photos_menu));
        mBaseViews.add(new SmartView(getActivity()));
        mBaseViews.add(new GovView(getActivity()));
        mBaseViews.add(new SettingView(getActivity()));

        mVp_home_content_container.setAdapter(new MyAdapter());


        //初始化页面的选择,刚刚进入到页面的时候,选择的页面是应该是:"主页"
        initContentViewAndTitle(mSelectedId);
        //设置页面的切换监听事件
        mRg_home_tab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_home:
                        mSelectedId = 0;
                        break;
                    case R.id.rb_news:
                        mSelectedId = 1;
                        break;
                    case R.id.rb_smart:
                        mSelectedId = 2;
                        break;
                    case R.id.rb_gov:
                        mSelectedId = 3;
                        break;
                    case R.id.rb_setting:
                        mSelectedId = 4;
                        break;
                }

                initContentViewAndTitle(mSelectedId);
            }
        });
    }

    private void initContentViewAndTitle(int selectedId) {
        //根据点击的位置获取将要显示的页面
        BaseView baseView = mBaseViews.get(selectedId);

        //不同的页面是否可以滑动脱出侧边菜单
        mSlidingFragmentActivity = (SlidingFragmentActivity) baseView.getActivity();
        if (selectedId == 0 || selectedId == 4) {
            //不显示菜单小图标

            mIv_menu.setVisibility(View.GONE);
            mSlidingFragmentActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        } else {
            mIv_menu.setVisibility(View.VISIBLE);
            mSlidingFragmentActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        }

        //viewpager定位到选择的页面
        mVp_home_content_container.setCurrentItem(selectedId, false);
        //设置标题栏
        mTv_title.setText(baseView.getTitle());

        baseView.initData();

        //左上角的菜单图标的点击事件:打开或者关闭菜单
        mIv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingFragmentActivity.toggle();
            }
        });
    }

    private class MyAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mBaseViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BaseView baseView = mBaseViews.get(position);
            View view = baseView.getView();
            container.addView(view);
            return view;
        }
    }

    //初始化页面布局
    @Override
    public View initView() {
        View view = View.inflate(getContext(), R.layout.fragment_home_content, null);
        mIv_menu = (ImageView) view.findViewById(R.id.iv_menu);
        mTv_title = (TextView) view.findViewById(R.id.tv_title);
        mVp_home_content_container = (ViewPager) view.findViewById(R.id.vp_home_content_container);
        mRg_home_tab = (RadioGroup) view.findViewById(R.id.rg_home_tab);
        mIv_photos_menu = (ImageView) view.findViewById(R.id.iv_photos_menu);

        return view;
    }

}
