package com.example.leer.zhbj;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by Leer on 2017/4/27.
 */
public class NewsArticleActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String URL_KEY = "url_key";

    @ViewInject(R.id.wv_news)
    private WebView mWebView;

    @ViewInject(R.id.iv_back)
    private ImageView iv_back;

    @ViewInject(R.id.iv_text_size)
    private ImageView iv_text_size;

    @ViewInject(R.id.iv_share)
    private ImageView iv_share;

    @ViewInject(R.id.pb_loading_web_view)
    private ProgressBar pb_loading_web_view;
    private int mCurrentIndex;
    private String mUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_news_article);

        //初始化分享的sdk
        ShareSDK.initSDK(this,"1d6b42fb68759");

        ViewUtils.inject(this);//view注入

        mUrl = changeArticleUrl(getIntent().getExtras().getString(URL_KEY,""));
        if(!TextUtils.isEmpty(mUrl)){
            initWebPage(mUrl);
        }

        iv_back.setOnClickListener(this);
        iv_share.setOnClickListener(this);
        iv_text_size.setOnClickListener(this);


    }


    private void initWebPage(final String url) {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        mWebView.loadUrl(url);
        changeTextSize(2);

        mWebView.setWebViewClient(new WebViewClient(){
            //页面开始加载回调的方法
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pb_loading_web_view.setVisibility(View.VISIBLE);
            }

            //页面加载完毕回调的方法
            @Override
            public void onPageFinished(WebView view, String url) {
                pb_loading_web_view.setVisibility(View.INVISIBLE);
                super.onPageFinished(view, url);
            }



            //是否应该在webview内加载网页,因为页面内的链接跳转会跳转到手机自带的浏览器里
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        });

    }

    public static Intent newIntent(Context context,String url){
        Intent i = new Intent(context,NewsArticleActivity.class);
        i.putExtra(URL_KEY,url);
        return i;

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //智慧北京的项目中加载图片的连接是10.0.2.2,但是这里使用的是genymotion模拟器,应该
    //更改为10.0.3.2
    private String changeArticleUrl(String url) {
        //花生壳服务器请求链接
//        String newUrl = url.replace("10.0.2.2:8080", "16tk329255.iok.la:26456");
        //Genymotion模拟器请求链接
        String newUrl = url.replace("10.0.2.2:8080", "10.0.3.2:8080");
        return newUrl;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_text_size:
                showTextSizeChangeDialog();
                break;
            case R.id.iv_share:
                showShare();
                break;
            default:
                break;
        }
    }

    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle("标题");
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("这是一篇好文章,快来打开这个链接吧:"+mUrl.toString());
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
        oks.show(this);
    }
    private void showTextSizeChangeDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//        dialog.setTitle("字体设置");
        View titleView = View.inflate(this,R.layout.view_text_size_change_title,null);
        dialog.setCustomTitle(titleView);
        String[] sizeItems = new String[]{"超大号字体","大号字体","默认字体","小号字体","超小号字体"};
        dialog.setSingleChoiceItems(sizeItems, 2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCurrentIndex = which;
            }
        });

        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeTextSize(mCurrentIndex);
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void changeTextSize(int index){
        WebSettings settings = mWebView.getSettings();
        WebSettings.TextSize temp = WebSettings.TextSize.NORMAL;
        switch (index){
            case 0:
                temp = WebSettings.TextSize.LARGEST;
                break;
            case 1:
                temp = WebSettings.TextSize.LARGER;
                break;
            case 2:
                temp = WebSettings.TextSize.NORMAL;
                break;
            case 3:
                temp = WebSettings.TextSize.SMALLER;
                break;
            case 4:
                temp = WebSettings.TextSize.SMALLEST;
                break;
            default:break;
        }
        settings.setTextSize(temp);

    }
}
