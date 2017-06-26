package com.example.leer.zhbj;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.jetbrains.annotations.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Window;

import com.example.leer.zhbj.fragment.HomeContentFragment;
import com.example.leer.zhbj.fragment.menufragments.NewsCenterMenuFragment;
import com.example.leer.zhbj.utils.ConstantValue;
import com.example.leer.zhbj.utils.SPUtils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

/**
 * 这个类管理着主页的内容,是另个fragment 的容器
 * Created by Leer on 2017/4/2.
 */
public class HomeActivity extends SlidingFragmentActivity {

    private Fragment mHomeContentFragment;
    private Fragment mLeftMenuFragment;
    private FragmentManager mFm;
    private SlidingMenu mSlidingMenu;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);
        //更新sp中是否第一次进入页面的boolean值
        SPUtils.putBoolean(this, ConstantValue.IS_FIRST_ENTER, false);

        mFm = getSupportFragmentManager();

        //初始化左侧的菜单
        initMenu();

        //初始化各个Fragment
        initFragment();
    }

    private void initMenu() {
        //获取到SlidingMenu的实例
        mSlidingMenu = getSlidingMenu();
        setBehindContentView(R.layout.fragment_left_menu);//设置拖拽出来的menu的内容
        mSlidingMenu.setMode(SlidingMenu.LEFT);//设置模式:从哪里拖拽出来
        float density = getResources().getDisplayMetrics().density;
        mSlidingMenu.setBehindOffset((int) (250 * density + 0.5f));//设置屏幕预留的宽度
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);//设置拖拽的模式,当前的模式是:从屏幕的任何位置拖拽都能拖拽出侧拉菜单
    }


    private void initFragment() {
        //左侧菜单
        mLeftMenuFragment = mFm.findFragmentById(R.id.container_left_menu);

        //内容主界面
        mHomeContentFragment = mFm.findFragmentById(R.id.container_home_content);

        if (mLeftMenuFragment == null) {
            mLeftMenuFragment = new NewsCenterMenuFragment();
        }
        if (mHomeContentFragment == null) {
            mHomeContentFragment = new HomeContentFragment();
        }


        mFm.beginTransaction().replace(R.id.container_home_content, mHomeContentFragment).commit();

    }

    //往侧拉菜单栏里面添加fragment
    public void addMenu(Fragment fragment) {
        mFm.beginTransaction().replace(R.id.container_left_menu, fragment).commit();
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, HomeActivity.class);
    }
}
