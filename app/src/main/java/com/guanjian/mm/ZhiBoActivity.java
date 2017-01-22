package com.guanjian.mm;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.guanjian.mm.wxapi.BaseActivity;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;

/**
 * Created by Administrator on 2017/1/11.
 */

public class ZhiBoActivity extends BaseActivity {

    private Button btn_start;
    private Button btn_switch;
//    private Button btn_stop;
    private TXLivePusher mLivePusher;
    private TXLivePushConfig mLivePushConfig;
    private TXCloudVideoView mCaptureView;
    private String rtmpUrl;
    private String href;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zhibo);
        Intent intent = getIntent();
        rtmpUrl = intent.getStringExtra("rtmpUrl");
        href = intent.getStringExtra("href");
        Log.e("href",href);
        /*requestPermission(FORCE_REQUIRE_PERMISSIONS, true, new PermissionsResultListener() {
            @Override
            public void onPermissionGranted() {
                initView();
                initZhiBo();
            }

            @Override
            public void onPermissionDenied() {
                Toast.makeText(ZhiBoActivity.this, "拒绝申请权限", Toast.LENGTH_LONG).show();
            }
        });*/
        initView();
        initZhiBo();
        initWebView();
    }

    private void initWebView() {
        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        WebSettings settings = mWebView.getSettings();

        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setJavaScriptEnabled(true);
        settings.setSavePassword(true);
        settings.setSaveFormData(true);
        /***打开本地缓存提供JS调用**/
        mWebView.getSettings().setDomStorageEnabled(true);
        // Set cache size to 8 mb by default. should be more than enough
        mWebView.getSettings().setAppCacheMaxSize(1024*1024*8);
        // This next one is crazy. It's the DEFAULT location for your app's cache
        // But it didn't work for me without this line.
        // UPDATE: no hardcoded path. Thanks to Kevin Hawkins
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        settings.setAppCachePath(appCachePath);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        mWebView.addJavascriptInterface(ZhiBoActivity.this, "android");
        settings.setBuiltInZoomControls(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSavePassword(true);
        settings.setSaveFormData(true);
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        mWebView.clearHistory();
        mWebView.clearFormData();
        mWebView.clearCache(true);

        mWebView.requestFocusFromTouch();
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return true;
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.loadUrl(href);
    }

    @JavascriptInterface
    public void stopZhiBo(final String url){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLivePusher.stopCameraPreview(true); //停止摄像头预览
                mLivePusher.stopPusher();            //停止推流
                mLivePusher.setPushListener(null);   //解绑 listener
                Intent intent = new Intent(ZhiBoActivity.this, StopZhiBoActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
                finish();
            }
        });

    }

    private void initZhiBo() {
        mLivePusher = new TXLivePusher(this);
        mLivePushConfig = new TXLivePushConfig();
        mLivePusher.setConfig(mLivePushConfig);
        //设置视频水印
        mLivePushConfig.setWatermark(BitmapFactory.decodeResource(getResources(),R.drawable.watermark), 10, 10);
        mLivePusher.setConfig(mLivePushConfig);
    }

    private void initView() {
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_switch = (Button) findViewById(R.id.btn_switch);
//        btn_stop = (Button) findViewById(btn_stop);
        mCaptureView = (TXCloudVideoView) findViewById(R.id.video_view);


        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLivePusher.startCameraPreview(mCaptureView);
                mLivePusher.startPusher(rtmpUrl);
                mCaptureView.setVisibility(View.VISIBLE);
            }
        });

        btn_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 默认是前置摄像头
                mLivePusher.switchCamera();
            }
        });

/*        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLivePusher.stopCameraPreview(true); //停止摄像头预览
                mLivePusher.stopPusher();            //停止推流
                mLivePusher.setPushListener(null);   //解绑 listener
            }
        });*/
    }
}
