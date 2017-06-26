package com.example.leer.zhbj.homecontentview.newschildrenview;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leer.zhbj.HttpData.PhotosData;
import com.example.leer.zhbj.R;
import com.example.leer.zhbj.utils.ConstantValue;
import com.example.leer.zhbj.utils.MyBitmapUtils;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * Created by Leer on 2017/4/10.
 */

public class PicturesPager extends BasePager {

    private PhotosData mPhotosData;
    private ImageView mPhotosMenu;

    private static final int DATA_GET_FINISHED = 100;
    private static final int MORE_DATA_GET_FINISHED = 101;

    private boolean isListViewShowing = true;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DATA_GET_FINISHED:
                    initAdapter();
                    break;
                case MORE_DATA_GET_FINISHED:
                    break;
                default:
                    break;
            }
        }
    };
    private MyAdapter mAdapter;
    private ListView lv_photos;
    private GridView gv_photos;

    private void initAdapter() {
        if (mAdapter == null) {
            mAdapter = new MyAdapter();
        }
        lv_photos.setAdapter(mAdapter);
        gv_photos.setAdapter(mAdapter);

        mPhotosMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isListViewShowing = !isListViewShowing;
                if (isListViewShowing) {
                    lv_photos.setVisibility(View.VISIBLE);
                    gv_photos.setVisibility(View.GONE);
                    mPhotosMenu.setImageResource(R.drawable.icon_pic_grid_type);
                } else {
                    lv_photos.setVisibility(View.GONE);
                    gv_photos.setVisibility(View.VISIBLE);
                    mPhotosMenu.setImageResource(R.drawable.icon_pic_list_type);
                }
            }
        });

        //默认情况下的显示情况
        lv_photos.setVisibility(View.VISIBLE);
        gv_photos.setVisibility(View.GONE);
        mPhotosMenu.setImageResource(R.drawable.icon_pic_grid_type);

    }

    private class MyAdapter extends BaseAdapter {
        //        BitmapUtils bitmapUtils;
        MyBitmapUtils bitmapUtils;

        public MyAdapter() {
//            bitmapUtils = new BitmapUtils(getActivity());
            bitmapUtils = new MyBitmapUtils(getActivity());
        }

        @Override
        public int getCount() {
            return mPhotosData.data.news.size();
        }

        @Override
        public PhotosData.PhotoNews getItem(int position) {
            return mPhotosData.data.news.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.lv_photos_news_item, null);
                viewHolder = new ViewHolder();
                viewHolder.mImage = (ImageView) convertView.findViewById(R.id.iv_photos_photo);
                viewHolder.mTitle = (TextView) convertView.findViewById(R.id.tv_photos_title);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            PhotosData.PhotoNews photoNews = getItem(position);

            viewHolder.mTitle.setText(photoNews.title);
            bitmapUtils.display(viewHolder.mImage, changeImageUrl(photoNews.listimage));
            return convertView;
        }
    }

    static class ViewHolder {
        public ImageView mImage;
        public TextView mTitle;
    }

    public PicturesPager(Activity activity, ImageView photosMenu) {
        super(activity);
        this.mPhotosMenu = photosMenu;
        initData();
    }

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.view_news_pager_pictures, null);
        lv_photos = (ListView) view.findViewById(R.id.lv_photos);
        gv_photos = (GridView) view.findViewById(R.id.gv_photos);
        return view;
    }

    @Override
    public void initData() {
        getDataFromServer();
    }

    public void getDataFromServer() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, ConstantValue.HTTP_PHOTOS_ADDRESS, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (responseInfo.result != null) {
                    resolveData(responseInfo.result);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "加载出错啦!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resolveData(String data) {
        Gson gson = new Gson();
        mPhotosData = gson.fromJson(data, PhotosData.class);
        mHandler.sendEmptyMessage(DATA_GET_FINISHED);
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
