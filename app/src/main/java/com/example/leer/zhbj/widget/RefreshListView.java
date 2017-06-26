package com.example.leer.zhbj.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.leer.zhbj.R;

/**
 * Created by Leer on 2017/4/23.
 */

public class RefreshListView extends ListView{

    private static final String TAG = "RefreshListView";
    private ImageView iv_arrow;
    private ProgressBar pb_refresh;
    private TextView tv_refresh_label;
    private TextView tv_refresh_date;
    //头部刷新布局
    private View mHeadView;
    //头布局的高度
    private int mHeadViewHeight;

    //下拉刷新的状态
    private static final int PULL_TO_REFRESH = 1;
    //松手刷新的状态
    private static final int RELEASE_TO_REFRESH = 2;
    //正在刷新的状态
    private static final int REFRESHING = 3;

    //当前的下拉应该显示的状态,默认是"下拉刷新"
    private int mCurrentState = PULL_TO_REFRESH;
    private RotateAnimation mRotateDown;
    private RotateAnimation mRotateUp;

    private OnRefreshListener mOnRefreshListener;
    private boolean isRefresh = false;
    private View mFooterView;
    private int mFooterViewHeight;
    private ProgressBar pb_refresh_more;
    private TextView tv_refresh_label_more;
    private boolean isLoadingMore;

    //记录点击的初始位置
    private int startY = -1;
    private int startX = -1;
    //记录点击后移动的距离
    private int disY = 0;
    private int disX = 0;
    //头布局(刷新状态栏)的padding值(-mViewHeight代表完全隐藏)
    private int mPaddingTop;


    public RefreshListView(Context context) {
        super(context);
        initHeadView();
        initFooterView();
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeadView();
        initFooterView();
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHeadView();
        initFooterView();
    }

    private void initFooterView() {
        mFooterView = View.inflate(getContext(), R.layout.view_list_view_footer,null);
        this.addFooterView(mFooterView);

        mFooterView.measure(0,0);

        mFooterViewHeight = mFooterView.getMeasuredHeight();
        mFooterView.setPadding(0,0,0,-mFooterViewHeight);

        pb_refresh_more = (ProgressBar) mFooterView.findViewById(R.id.pb_refresh_more);
        tv_refresh_label_more = (TextView) mFooterView.findViewById(R.id.tv_refresh_label_more);

        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int count = getCount();
                int currentPos = getLastVisiblePosition();
                if(scrollState == SCROLL_STATE_IDLE && currentPos == count-1){
                    Log.d(TAG,"数据刷新中-----------------");
                    mFooterView.setPadding(0,0,0,0);
                    //设置选中的位置,应该选中最后一个(也就是footerview),这样就能够让其显现出来
                    setSelection(count-1);

                    if(mOnRefreshListener != null && isLoadingMore == false){
                        isLoadingMore = true;
                        mOnRefreshListener.loadMoreData();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    public void initHeadView() {
        mHeadView = View.inflate(getContext(), R.layout.view_list_view_head, null);

        this.addHeaderView(mHeadView);
        //手动调用子View的measure()方法,使得子view能够得到测量
        mHeadView.measure(0, 0);
        //测量控件的高度
        mHeadViewHeight = mHeadView.getMeasuredHeight();
        mPaddingTop = -mHeadViewHeight;

        iv_arrow = (ImageView) mHeadView.findViewById(R.id.iv_arrow);
        pb_refresh = (ProgressBar) mHeadView.findViewById(R.id.pb_refresh);
        tv_refresh_label = (TextView) mHeadView.findViewById(R.id.tv_refresh_label);
        tv_refresh_date = (TextView) mHeadView.findViewById(R.id.tv_refresh_date);

        //初始化下拉刷新状态栏的动画,就是箭头和progressbar的切换动画
        initAnim();

        //初始的时候,头布局应该是隐藏的状态
        mHeadView.setPadding(0,-mHeadViewHeight,0,0);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) ev.getY();
                startX = (int) ev.getX();

                //按下的时候初始化
                disY = 0;
                disX = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                //这里需要注意:
                //因为ListView里面嵌套了一个NewsTabViewPager(这是一个自定义的ViewPager),
                //这个viewPager也会接收到ACTION_DOWN事件,ACTION_DOWN事件我们没有拦截(具体看
                // NewsTabViewPager里面的onTouchEvent()方法里面的处理逻辑,此时ACTION_DOWN事件会被
                // 消费掉,通过判断startY有没有赋值,如果没有赋值,就重新获取一遍)
                if(startY == -1){
                    startY = (int) ev.getY();
                }
                if(startX == -1){
                    startX = (int) ev.getX();
                }



                disY = (int) (ev.getY() - startY);
                disX = (int) (ev.getX() - startX);

                if(Math.abs(disX) > Math.abs(disY)){
                    getParent().requestDisallowInterceptTouchEvent(false);
                }

                mPaddingTop = -mHeadViewHeight + disY;

                if(mCurrentState == REFRESHING){
                    mPaddingTop = disY;
                }

                //设置以下条件的目的是:
                //1.当状态没有发生改变的时候,不用更改状态,不用设置动画效果
                //2.当前处于刷新状态的时候,下拉不会改变当前的状态
                if (mPaddingTop < 0 && mCurrentState != PULL_TO_REFRESH && mCurrentState != REFRESHING) {
                    mCurrentState = PULL_TO_REFRESH;
                    setAnim();
                } else if (mPaddingTop >= 0 && mCurrentState != RELEASE_TO_REFRESH&& mCurrentState != REFRESHING) {
                    mCurrentState = RELEASE_TO_REFRESH;
                    setAnim();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mPaddingTop >= 0 ) {
                    mPaddingTop = 0;
                    mCurrentState = REFRESHING;
                } else if (mPaddingTop < 0) {
                    mPaddingTop = -mHeadViewHeight;
                }

                //将起始的y坐标归位,下次出发ACTION_DOWN的时候,重新判断
                startY = -1;
                startX = -1;
                break;
        }

        setPaddingAndChangeState();
        return super.onTouchEvent(ev);
    }

    private void setAnim() {
        //当状态发生改变的时候,设置动画
        if(mCurrentState == PULL_TO_REFRESH){
            iv_arrow.startAnimation(mRotateDown);
        }else if(mCurrentState == RELEASE_TO_REFRESH){
            iv_arrow.startAnimation(mRotateUp);
        }
    }

    private void setPaddingAndChangeState() {
        int currentPosition = getFirstVisiblePosition();
        if (currentPosition == 0) {
            mHeadView.setPadding(0, mPaddingTop, 0, 0);
        }

        switch (mCurrentState){
            case PULL_TO_REFRESH:
                tv_refresh_label.setText("下拉刷新");
                pb_refresh.setVisibility(View.INVISIBLE);
                iv_arrow.setVisibility(View.VISIBLE);
                break;
            case RELEASE_TO_REFRESH:
                tv_refresh_label.setText("松手刷新");
                pb_refresh.setVisibility(View.INVISIBLE);
                iv_arrow.setVisibility(View.VISIBLE);
                break;
            case REFRESHING:
                tv_refresh_label.setText("正在刷新...");
                pb_refresh.setVisibility(View.VISIBLE);
                iv_arrow.clearAnimation();
                iv_arrow.setVisibility(View.INVISIBLE);

                if(mOnRefreshListener != null && isRefresh == false){
                    //更改刷新状态
                    isRefresh = true;
                    //刷新回调
                    mOnRefreshListener.refresh();
                }
                break;
        }
    }

    public void initAnim(){
        mRotateDown = new RotateAnimation(-180,0, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        mRotateDown.setDuration(500);
        mRotateDown.setFillAfter(true);

        mRotateUp = new RotateAnimation(0,-180, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        mRotateUp.setDuration(500);
        mRotateUp.setFillAfter(true);
    }

    //刷新成功,复位
    public void refreshCompleted(){
        mCurrentState = PULL_TO_REFRESH;
        mHeadView.setPadding(0,-mHeadViewHeight,0,0);

        //刷新完成,将刷新状态改为false
        isRefresh = false;
        mPaddingTop = -mHeadViewHeight;
    }

    public void loadMoreDataCompleted(){
        isLoadingMore = false;
        //注意理解这里的paddingTop值是负值
        mFooterView.setPadding(0,-mFooterViewHeight,0,0);
    }

    //设置数据刷新回调接口
    public void setOnRefreshListener(OnRefreshListener refreshListener){
        this.mOnRefreshListener = refreshListener;
    }


    public interface OnRefreshListener {
        void refresh();
        void loadMoreData();
    }
}
