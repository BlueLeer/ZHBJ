package com.example.leer.zhbj.fragment.menufragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.leer.zhbj.HomeActivity;
import com.example.leer.zhbj.HttpData.NewsData;
import com.example.leer.zhbj.R;
import com.example.leer.zhbj.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leer on 2017/4/4.
 */

public class NewsCenterMenuFragment extends BaseFragment {

    private static final String CURRENT_INDEX = "current_index";
    private ListView mLv_menu_news;
    private List<String> mNewsCategories;
    private MyAdapter mAdapter;
    private int mCurrentPosition = 0;
    private HomeActivity mActivity;
    private OnDetailsPagerChangeListener mOnDetailsPagerChangeListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentPosition = getArguments().getInt(CURRENT_INDEX,0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.mActivity = (HomeActivity) getActivity();
        return initView();
    }

    @Override
    public View initView() {
        View view = View.inflate(getContext(), R.layout.left_menu_newscenter, null);
        mLv_menu_news = (ListView) view.findViewById(R.id.lv_menu_news);
        if(mNewsCategories!=null && mNewsCategories.size() > 0){
            mAdapter = new MyAdapter();
            mLv_menu_news.setAdapter(mAdapter);
        }

        mLv_menu_news.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrentPosition = position;
                //点击菜单左侧的侧拉栏,需要在右侧实时的更新详情页面
                mAdapter.notifyDataSetChanged();
                //点击菜单列表的时候,自动回收菜单
                mActivity.toggle();
                //当选择不同的条目的时候,在这里进行回调,切换到不同的新闻类型(如:新闻,组图...)
                if(mOnDetailsPagerChangeListener != null){
                    mOnDetailsPagerChangeListener.detailPagerChanged(position);
                }

            }
        });
        return view;
    }

    public void initAdapterData(NewsData newsData) {//注意:此时ViewPager还没有实例化布局,不能直接将数据设置给ViewPager
        mNewsCategories = new ArrayList<>();
        for(int i = 0;i<newsData.data.length;i++){
            mNewsCategories.add(newsData.data[i].title);
        }

    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mNewsCategories.size();
        }

        @Override
        public String getItem(int position) {
            return mNewsCategories.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getContext(),R.layout.left_menu_newscenter_item,null);
            TextView textView = (TextView) view.findViewById(R.id.tv_left_menu_item);
            textView.setText(mNewsCategories.get(position));
            if(mCurrentPosition == position){
                textView.setEnabled(true);
            }else{
                textView.setEnabled(false);
            }
            return view;
        }
    }

    public interface OnDetailsPagerChangeListener{
        void detailPagerChanged(int positon);
    }

    public void setmOnDetailsPagerChangeListener(OnDetailsPagerChangeListener mOnDetailsPagerChangeListener){
        this.mOnDetailsPagerChangeListener = mOnDetailsPagerChangeListener;
    }

    public static NewsCenterMenuFragment getInstance(int position){
        NewsCenterMenuFragment newsCenterMenuFragment = new NewsCenterMenuFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(CURRENT_INDEX,position);

        newsCenterMenuFragment.setArguments(bundle);
        return newsCenterMenuFragment;
    }

}
