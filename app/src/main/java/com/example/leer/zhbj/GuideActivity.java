package com.example.leer.zhbj;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leer on 2017/4/2.
 */
public class GuideActivity extends AppCompatActivity {

    private ViewPager vp_guide;
    private LinearLayout ll_points;
    private Button bt_enter;
    private List<ImageView> mImageViews;
    private int mDisPoint;
    private ImageView iv_red_point;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        //初始化页面的组件
        initUI();
        //初始化数据
        initDate();
        //初始化引导页面的小红点
        initGuidePoints();
        //给页面的viewPager设置数据适配器
        vp_guide.setAdapter(new MyAdapter());
        //测量点之间的距离
        //当layout()方法执行完毕以后,就会回调这个方法
        ll_points.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mDisPoint = ll_points.getChildAt(1).getLeft() - ll_points.getChildAt(0).getLeft();
            }
        });
        //设置viewpager的滑动切换监听事件
        setViewPagerScroll();
        //设置"立即体验按钮的点击跳转事件"
        bt_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = HomeActivity.newIntent(GuideActivity.this);
                startActivity(i);
                finish();
            }
        });
    }

    private void initGuidePoints() {
        for (int i = 0; i < mImageViews.size(); i++) {
            ImageView imageView = new ImageView(this);
            imageView.setBackgroundResource(R.drawable.shape_normal_point);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (i > 0) {
                //在当前的屏幕下设置成20 pixel刚刚好,那么转化为dp为pixel/density=10dp
                float density = getResources().getDisplayMetrics().density;
                Log.d("xxxxxxxxxx","当前的像素密度是 : "+density);
                params.leftMargin = (int) (density * 10 + 0.5f);
            }
            imageView.setLayoutParams(params);

            ll_points.addView(imageView);
        }

    }

    private void setViewPagerScroll() {
        //设置viewpager的滑动监听事件
        vp_guide.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //当页面滑动的时候
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_red_point.getLayoutParams();
                int leftMargin = (int) (position * mDisPoint + mDisPoint * positionOffset);
                params.leftMargin = leftMargin;
                iv_red_point.setLayoutParams(params);
            }

            //当页面被选中的时候
            @Override
            public void onPageSelected(int position) {

                //最后一页
                if(position == mImageViews.size() -1){
                    bt_enter.setVisibility(View.VISIBLE);
                }else{
                    bt_enter.setVisibility(View.INVISIBLE);
                }
            }

            //当页面状态发生改变的时候,例如由静止变成滑动的状态
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class MyAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mImageViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = mImageViews.get(position);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private void initDate() {
        int[] ids = new int[]{R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};
        mImageViews = new ArrayList<>();
        for (int i = 0; i < ids.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setBackgroundResource(ids[i]);
            mImageViews.add(imageView);
        }
    }

    private void initUI() {
        vp_guide = (ViewPager) findViewById(R.id.vp_guide);
        ll_points = (LinearLayout) findViewById(R.id.ll_points);
        bt_enter = (Button) findViewById(R.id.bt_enter);

        iv_red_point = (ImageView) findViewById(R.id.iv_red_point);


    }

    public static Intent newIntent(Context context) {
        return new Intent(context, GuideActivity.class);
    }
}
