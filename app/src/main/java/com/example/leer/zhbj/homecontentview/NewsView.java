package com.example.leer.zhbj.homecontentview;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.leer.zhbj.HomeActivity;
import com.example.leer.zhbj.HttpData.NewsData;
import com.example.leer.zhbj.R;
import com.example.leer.zhbj.fragment.menufragments.NewsCenterMenuFragment;
import com.example.leer.zhbj.homecontentview.newschildrenview.BasePager;
import com.example.leer.zhbj.homecontentview.newschildrenview.InteractionPager;
import com.example.leer.zhbj.homecontentview.newschildrenview.NewsPager;
import com.example.leer.zhbj.homecontentview.newschildrenview.PicturesPager;
import com.example.leer.zhbj.homecontentview.newschildrenview.TopicPager;
import com.example.leer.zhbj.utils.CacheUtils;
import com.example.leer.zhbj.utils.ConstantValue;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leer on 2017/4/5.
 */

public class NewsView extends BaseView {
    private HomeActivity mActivity;
    private NewsData mNewsData;
    private static final String TAG = "NewsView";
    private FrameLayout mNewsDetailViewsContainer;
    private List<BasePager> mDetailsPagers;
    private int mCurrentIndex;
    private NewsPager mNewsPager;
    //切换"组图"下面的组图显示,以ListView的形式还是GridView的形式
    private ImageView mPhotosMenu;

    public NewsView(Activity activity, ImageView imageView) {
        super(activity);
        this.mActivity = (HomeActivity) getActivity();

        this.mPhotosMenu = imageView;
        initDetailsPager();
    }

    private void initDetailsPager() {
        mDetailsPagers = new ArrayList<>();
        mNewsPager = new NewsPager(mActivity);
        mDetailsPagers.add(mNewsPager);

        mDetailsPagers.add(new TopicPager(mActivity));
        mDetailsPagers.add(new PicturesPager(mActivity,mPhotosMenu));
        mDetailsPagers.add(new InteractionPager(mActivity));

//        初始化的时候,显示"新闻"详情页
        addNewsDetailsView(0);
        mCurrentIndex = 0;

    }

    @Override
    protected void initTitle() {
        setTitle("新闻中心");
    }

    @Override
    public void initView() {
        View view = View.inflate(getActivity(), R.layout.view_newscenter, null);
        mNewsDetailViewsContainer = (FrameLayout) view.findViewById(R.id.fl_news_data_details_container);

        setView(view);
    }

    @Override
    public void initData() {
        //先从缓存中获取数据
        String result = CacheUtils.getCache(ConstantValue.HTTP_NEWS_ADDRESS,mActivity);
        //如果缓存中的数据不为空,就直接将数据解析出来,填充页面
        if(!TextUtils.isEmpty(result)){
            resolveJson(result);
            Log.d(TAG,"从sp当中获取缓存成功!!"+result);
        }else{
            //没有缓存,或者需要更新数据,就从服务器获取数据
            initDataFromServer();
        }
    }

    //初始化侧拉菜单的内容
    @Override
    public void initMenu() {
        NewsCenterMenuFragment newsCenterMenuFragment =NewsCenterMenuFragment.getInstance(mCurrentIndex);
        if (mNewsData != null) {
            newsCenterMenuFragment.initAdapterData(mNewsData);
        }

        newsCenterMenuFragment.setmOnDetailsPagerChangeListener(new NewsCenterMenuFragment.OnDetailsPagerChangeListener() {
            @Override
            public void detailPagerChanged(int positon) {
                addNewsDetailsView(positon);
                mCurrentIndex = positon;

                if(positon == 2){
                    mPhotosMenu.setVisibility(View.VISIBLE);
                }else{
                    mPhotosMenu.setVisibility(View.GONE);
                }
            }
        });
        mActivity.addMenu(newsCenterMenuFragment);
    }

    //从服务器端获取新闻数据
    private void initDataFromServer() {
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.GET, ConstantValue.HTTP_NEWS_ADDRESS, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> mResponseInfo) {
                Log.d("网络请求成功,返回的数据是 : ", mResponseInfo.result);
                //解析json数据
                resolveJson(mResponseInfo.result);
                //将从服务器端获取的数据缓存到sp当中
                CacheUtils.setCache(ConstantValue.HTTP_NEWS_ADDRESS,mResponseInfo.result,mActivity);
            }

            @Override
            public void onFailure(HttpException mE, String mS) {
                Toast.makeText(getActivity(),"网络出错!!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**将json格式的数据解析成java对象
     * @param json json格式的数据
     */
    private void resolveJson(String json){
        Gson gson = new Gson();
        mNewsData = gson.fromJson(json,NewsData.class);
        //json已经解析完毕,此时更新侧拉栏
        initMenu();

        //提示新闻的每一个详情页面都更新数据
        mNewsPager.setData(mNewsData);

    }

    public void addNewsDetailsView(int index){
        //移除掉所有的页面
        mNewsDetailViewsContainer.removeAllViews();
        //添加新的页面
        mNewsDetailViewsContainer.addView(mDetailsPagers.get(index).getView());
    }
}
