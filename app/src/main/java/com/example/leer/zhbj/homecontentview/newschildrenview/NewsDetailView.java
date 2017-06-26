package com.example.leer.zhbj.homecontentview.newschildrenview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leer.zhbj.HttpData.NewsChildren;
import com.example.leer.zhbj.HttpData.NewsDetailsData;
import com.example.leer.zhbj.NewsArticleActivity;
import com.example.leer.zhbj.R;
import com.example.leer.zhbj.utils.ConstantValue;
import com.example.leer.zhbj.utils.MyBitmapUtils;
import com.example.leer.zhbj.utils.SPUtils;
import com.example.leer.zhbj.widget.RefreshListView;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.List;

/**
 * 是"新闻"详情页,里面有一个ViewPager展示TopNews,有一个ListView展示News,为了能够让TopNews能够上下滑动,将
 * TopNews设置为ListView的HeadView
 * Created by Leer on 2017/4/20.
 */

public class NewsDetailView {
    private Context mContext;
    private View mView;
    private static final String TAG = "NewsDetailView";

    private NewsChildren mNewsCenterData;

    @ViewInject(R.id.vp_news_details_topnews)
    private ViewPager vp_news_details_topnews;

    @ViewInject(R.id.lv_news_details_topnews)
    private RefreshListView lv_news_details_topnews;

    @ViewInject(R.id.circle_indicator)
    private CirclePageIndicator circle_indicator;

    @ViewInject(R.id.tv_topnews_title)
    private TextView tv_topnews_title;

    @ViewInject(R.id.iv_news_icon)
    private ImageView iv_news_icon;

    @ViewInject(R.id.tv_news_title)
    private TextView tv_news_title;

    @ViewInject(R.id.tv_news_date)
    private TextView tv_news_date;

    private String mUrlString;

    private NewsDetailsData mNewsDetailsDatas;//每一个新闻页签下面的所有数据
    private List<NewsDetailsData.TopNews> mTopNewsDatas;//头条新闻下面的所有数据
    private List<NewsDetailsData.Topic> mTopicDatas;//主题对应的数据
    private List<NewsDetailsData.News> mNewsDatas;//每一个页签下面对应的具体新闻数据
    private final MyBitmapUtils mBitmapUtils;
    private View mHeadTopNewsView;
    private String mMoreUrlString;

    //初次加载数据的状态
    private static final int STATE_LOAD_DATA = 1;
    //加载更多数据的状态
    private static final int STATE_LOAD_DATA_MORE = 2;
    private MyListViewAdapter mListViewAdapter;
    private Handler mHandle;
    //记录头条ViewPager当前的位置,初始情况下是0
    private int mTopNewsCurrentIndex = 0;

    public NewsDetailView(Context context, NewsChildren newsData) {
        this.mContext = context;
        this.mNewsCenterData = newsData;

        mView = initView();//view注入
        ViewUtils.inject(this, mView);

        mHeadTopNewsView = View.inflate(mContext, R.layout.view_news_pager_topnews, null);
        ViewUtils.inject(this, mHeadTopNewsView);

        circle_indicator.setVisibility(View.GONE);
        lv_news_details_topnews.addHeaderView(mHeadTopNewsView);

        //xutils下面的一个工具,解决从网络下载图片、缓存、填充控件
        mBitmapUtils = new MyBitmapUtils(mContext);
        Bitmap bitmap = android.graphics.BitmapFactory.decodeResource(mContext.getResources(), R.drawable.news_pic_default);
//        //图片预加载过程中显示的图片
//        mBitmapUtils.configDefaultLoadingImage(bitmap);
//        //图片加载失败的时候加载的图片
//        mBitmapUtils.configDefaultLoadFailedImage(bitmap);

        lv_news_details_topnews.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void refresh() {
                getDataFromServerAndResolve(mUrlString, STATE_LOAD_DATA);
            }

            @Override
            public void loadMoreData() {
                if (mMoreUrlString != null) {
                    getDataFromServerAndResolve(mMoreUrlString, STATE_LOAD_DATA_MORE);
                } else {
                    Toast.makeText(mContext, "没有更多的数据啦!", Toast.LENGTH_SHORT).show();
                    lv_news_details_topnews.loadMoreDataCompleted();
                }

                Log.d(TAG, "mMoreUrlString : " + mMoreUrlString);
            }
        });
    }

    public View getView() {
        return mView;
    }

    public View initView() {
        View view = View.inflate(mContext, R.layout.view_news_detail, null);
        return view;
    }


    //初始化数据,在NewsPager中被调用
    public void initData() {
        if (mNewsCenterData != null) {
            mUrlString = ConstantValue.HTTP_ROOT_ADDRESS + mNewsCenterData.url;
            if (TextUtils.isEmpty(mMoreUrlString)) {
                mMoreUrlString = null;
            }
            Log.d(TAG, "xxxxx : " + mUrlString);
        }

        //获取缓存数据
        String result = SPUtils.getString(mContext, mUrlString, null);
        if (!TextUtils.isEmpty(result)) {
            resolveData(result);
            initAdapter();
        } else {
            //如果没有就从缓存里获取到数据,就从服务器端获取数据
            getDataFromServerAndResolve(mUrlString, STATE_LOAD_DATA);
        }
    }

    public void initAdapter() {
        if (mTopNewsDatas != null) {
            //设置头条新闻的数据适配器
            vp_news_details_topnews.setAdapter(new MyViewPagerAdapter());
            circle_indicator.setViewPager(vp_news_details_topnews);
            circle_indicator.setVisibility(View.VISIBLE);
        }

        //默认的位置是0
        circle_indicator.setCurrentItem(0);
        String defaultTitle = mTopNewsDatas.get(0).title;
        tv_topnews_title.setText(defaultTitle);

        circle_indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String title = mTopNewsDatas.get(position).title;
                tv_topnews_title.setText(title);
            }

            @Override
            public void onPageScrollStateChanged(int state) {


            }
        });

        //设置新闻的数据适配器
        if (mNewsDatas != null) {
            mListViewAdapter = new MyListViewAdapter();
            lv_news_details_topnews.setAdapter(mListViewAdapter);
        }

        //设置ListView的点击事件
        lv_news_details_topnews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int headViewCount = lv_news_details_topnews.getHeaderViewsCount();
                Log.d(TAG, "当前点击的位置是 : " + position);
                NewsDetailsData.News news = mNewsDatas.get(position - headViewCount);
                String articleUrl = news.url;
                Intent i = NewsArticleActivity.newIntent(mContext, articleUrl);
                mContext.startActivity(i);
            }
        });

//        //设置头条ViewPager自动轮播
//        if (mHandle == null) {
//            mHandle = new Handler(){
//                @Override
//                public void handleMessage(Message msg) {
//                    mTopNewsCurrentIndex++;
//                    if(mTopNewsCurrentIndex > mTopNewsDatas.size()-1){
//                        mTopNewsCurrentIndex = 0;
//                    }
//                    vp_news_details_topnews.setCurrentItem(mTopNewsCurrentIndex);
//                    mHandle.sendEmptyMessageDelayed(0,3000);
//                }
//            };
//
//            //保证该消息只在刚刚进入到页面的时候发送一次消息
//            mHandle.sendEmptyMessageDelayed(0,3000);
//        }
//
//        //用户按下头条新闻的viewpager时,此时自动轮播应该关闭,抬起手指的时候自动轮播应该打开
//        vp_news_details_topnews.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()){
//                    case MotionEvent.ACTION_DOWN:
//                        mHandle.removeMessages(0);
//                        break;
//                    //当用户保持按下操作，并从当前控件(ViewPager)转移到外层控件(RefreshListView)时，会触发ACTION_CANCEL
//                    case MotionEvent.ACTION_CANCEL:
//                        mHandle.sendEmptyMessageDelayed(0,3000);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        mHandle.sendEmptyMessageDelayed(0,3000);
//                        break;
//                    default:break;
//                }
//                return false;
//            }
//        });

    }

    //新闻ListView的数据适配器
    public class MyListViewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mNewsDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mNewsDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.lv_news_item, null);
                viewHolder.newsIcon = (ImageView) convertView.findViewById(R.id.iv_news_icon);
                viewHolder.newsTitle = (TextView) convertView.findViewById(R.id.tv_news_title);
                viewHolder.newsDate = (TextView) convertView.findViewById(R.id.tv_news_date);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            NewsDetailsData.News news = mNewsDatas.get(position);
            viewHolder.newsTitle.setText(news.title);
            viewHolder.newsDate.setText(news.pubdate);
            mBitmapUtils.display(viewHolder.newsIcon, changeImageUrl(news.listimage));
            return convertView;
        }
    }

    static class ViewHolder {
        ImageView newsIcon;
        TextView newsTitle;
        TextView newsDate;
    }

    //头条轮播的ViewPager的数据适配器
    public class MyViewPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mTopNewsDatas.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(mContext);
            //设置内容的缩放方式,x和y方向上都是填充式的缩放,类似于设置background
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            String url = changeImageUrl(mTopNewsDatas.get(position).topimage);
            String s = changeImageUrl(url);
            mBitmapUtils.display(imageView, s);

            container.addView(imageView);
            return imageView;
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

    public void getDataFromServerAndResolve(String url, final int loadState) {

        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (!TextUtils.isEmpty(responseInfo.result)) {
                    if (loadState == STATE_LOAD_DATA) {
                        resolveData(responseInfo.result);
                        initAdapter();

                        //将数据放入缓存中
                        SPUtils.putString(mContext, mUrlString, responseInfo.result);
                    } else if (loadState == STATE_LOAD_DATA_MORE) {
                        resolveMoreDate(responseInfo.result);
                        if (mListViewAdapter != null) {
                            mListViewAdapter.notifyDataSetChanged();
                        }
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                //数据刷新完成,通知ListView收起刷新状态栏
                lv_news_details_topnews.refreshCompleted();
                lv_news_details_topnews.loadMoreDataCompleted();
            }

            @Override
            public void onFailure(HttpException e, String s) {
                //数据刷新失败,通知ListView收起刷新状态栏
                lv_news_details_topnews.refreshCompleted();

                //通知刷新失败
                Toast.makeText(mContext, "刷新失败!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * @param data 需要解析的json数据
     */
    private void resolveData(String data) {
        Gson gson = new Gson();
        mNewsDetailsDatas = gson.fromJson(data, NewsDetailsData.class);

        if (!TextUtils.isEmpty(mNewsDetailsDatas.data.more)) {
            mMoreUrlString = ConstantValue.HTTP_ROOT_ADDRESS + mNewsDetailsDatas.data.more;
        } else {
            mMoreUrlString = null;
        }

        //将所有的数据分成不同的模块
        //头条轮播的数据
        mTopNewsDatas = mNewsDetailsDatas.data.topnews;
        //主题的数据
        mTopicDatas = mNewsDetailsDatas.data.topic;
        //新闻列表的数据
        mNewsDatas = mNewsDetailsDatas.data.news;

    }

    private void resolveMoreDate(String data) {
        Gson gson = new Gson();
        NewsDetailsData newsDetailsData = gson.fromJson(data, NewsDetailsData.class);
        if (newsDetailsData != null) {
            if (!TextUtils.isEmpty(newsDetailsData.data.more)) {
                mMoreUrlString = ConstantValue.HTTP_ROOT_ADDRESS + newsDetailsData.data.more;
            } else {
                mMoreUrlString = null;
            }
        }
        mNewsDatas.addAll(newsDetailsData.data.news);
    }

    //智慧北京的项目中加载图片的连接是10.0.2.2,但是这里使用的是genymotion模拟器,应该
    //更改为10.0.3.2
    private String changeImageUrl(String url) {
        //花生壳服务器请求链接
//        String newUrl = url.replace("10.0.2.2:8080", "16tk329255.iok.la:26456");
        //Genymotion模拟器请求链接
        String newUrl = url.replace("10.0.2.2:8080", "10.0.3.2:8080");
        return newUrl;
    }

}
