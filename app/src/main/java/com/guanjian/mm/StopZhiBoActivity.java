package com.guanjian.mm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Administrator on 2017/1/21.
 */

public class StopZhiBoActivity extends Activity {

    private String url;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stop_zhibo);
        getData();
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
        mWebView.addJavascriptInterface(StopZhiBoActivity.this, "android");
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
        mWebView.loadUrl(url);
    }

    private void getData() {
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
    }

    @JavascriptInterface
    public void confirm(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });

    }


}
