package com.guanjian.mm;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.guanjian.mm.wxapi.BaseActivity;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.rtmp.TXLivePusher;

import java.io.File;

public class MyActivity extends BaseActivity {
    public static final String TAG = "MainActivity";
    ValueCallback<Uri> mUploadMessage;
    ValueCallback<Uri[]> mFilePathCallback;
    private WebView mWebView;
    private Boolean isAlbum;//是否打开相册
    private long clickTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!NetWorkUtils.isNetworkConnected(this)){
            Toast.makeText(this, "网络异常,请检查网络状况", Toast.LENGTH_LONG).show();
            return;
        }
        initView();
        int[] sdkver = TXLivePusher.getSDKVersion();
        if (sdkver != null && sdkver.length >= 3) {
            Log.e("rtmpsdk","rtmp sdk version is:" + sdkver[0] + "." + sdkver[1] + "." + sdkver[2]);
        }
    }

    private void initView() {
        mWebView = (WebView) findViewById(R.id.webView1);
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
        mWebView.addJavascriptInterface(MyActivity.this, "android");
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
        mWebView.setWebViewClient(new MyWebViewClient(this));
        mWebView.setWebChromeClient(new MyWebChromeClient());
//        mWebView.setWebChromeClient(new WebChromeClient());
//        mWebView.loadUrl("http://www.zhonghaonan.com/");
        mWebView.loadUrl("https://app.cv-china.com");

    }

    @JavascriptInterface
    public void startZhiBo(final String rtmpUrl, final String href){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MyActivity.this,ZhiBoActivity.class);
                intent.putExtra("rtmpUrl",rtmpUrl);
                intent.putExtra("href",href);
                startActivity(intent);
            }
        });
    }

    /**
     * 调用微信支付接口
     * @param params
     */
    @JavascriptInterface
    public void callWeinXinPay(String params){

        String[] arr = params.split(",");
        final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
        // 将该app注册到微信
        msgApi.registerApp(arr[0]);
        PayReq request = new PayReq();
        request.appId = "arr[0]";
        request.partnerId = "arr[3]";
        request.prepayId= "arr[4]";
        request.packageValue = "arr[2]";
        request.nonceStr= "arr[1]";
        request.timeStamp= "arr[5]";
        request.sign= "arr[6]";
        msgApi.sendReq(request);
    }


    private class MyWebViewClient extends WebViewClient {
        private Context mContext;

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        public MyWebViewClient(Context context) {
            super();
            mContext = context;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(TAG, "URL地址:" + url);
            view.getSettings().setJavaScriptEnabled(true);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.i(TAG, "onPageFinished");

            view.getSettings().setJavaScriptEnabled(true);
            super.onPageFinished(view, url);
            CookieManager cm = CookieManager.getInstance();
            String cookie = cm.getCookie(url);
            cm.setAcceptCookie(true);
            cm.setCookie(url, cookie);
            CookieSyncManager.getInstance().sync();
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            return super.shouldInterceptRequest(view, url);
        }
    }

    public static final int FILECHOOSER_RESULTCODE = 1;
    private static final int REQ_CAMERA = FILECHOOSER_RESULTCODE + 1;
    private static final int REQ_CHOOSE = REQ_CAMERA + 1;

    private class MyWebChromeClient extends WebChromeClient {

        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                                 final JsResult result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(message)
                    .setNeutralButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.dismiss();
                        }
                    }).show();
            result.cancel();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            new AlertDialog.Builder(MyActivity.this)
                    .setTitle("App Titler")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    result.confirm();
                                }
                            })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    result.cancel();
                                }
                            })
                    .create()
                    .show();

            return true;
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message,
                                  String defaultValue, JsPromptResult result) {
            // TODO Auto-generated method stub
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }

        public boolean onShowFileChooser(
                WebView webView, ValueCallback<Uri[]> filePathCallback,
                WebChromeClient.FileChooserParams fileChooserParams) {
            if (mFilePathCallback != null) return true;
            mFilePathCallback = filePathCallback;

            /*requestPermission(FORCE_REQUIRE_PERMISSIONS, true, new PermissionsResultListener() {
                @Override
                public void onPermissionGranted() {
                    selectImage();
                }

                @Override
                public void onPermissionDenied() {
                    Toast.makeText(MyActivity.this, "拒绝申请权限", Toast.LENGTH_LONG).show();
                }
            });*/
            selectImage();
            return true;
        }

        // For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            if (mUploadMessage != null) return;
            mUploadMessage = uploadMsg;
            /*requestPermission(FORCE_REQUIRE_PERMISSIONS, true, new PermissionsResultListener() {
                @Override
                public void onPermissionGranted() {
                    selectImage();
                }

                @Override
                public void onPermissionDenied() {
                    Toast.makeText(MyActivity.this, "拒绝申请权限", Toast.LENGTH_LONG).show();
                }
            });*/
            selectImage();
//               Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//               i.addCategory(Intent.CATEGORY_OPENABLE);
//               i.setType("*/*");
//                   startActivityForResult( Intent.createChooser( i, "File Chooser" ), FILECHOOSER_RESULTCODE );
        }

        // For Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "");
        }

        // For Android  > 4.1.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            openFileChooser(uploadMsg, acceptType);
        }

    }

    /**
     * 检查SD卡是否存在
     *
     * @return
     */
    public final boolean checkSDcard() {
        boolean flag = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (!flag) {
            Toast.makeText(this, "请插入手机存储卡再使用本功能", Toast.LENGTH_SHORT).show();
        }
        return flag;
    }

    String compressPath = "";

    protected final void selectImage() {
        if (!checkSDcard())
            return;
        String[] selectPicTypeStr = {"拍照", "从相册选择"};
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setItems(selectPicTypeStr,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                switch (which) {
                                    // 相机拍摄
                                    case 0:
                                        openCarcme();
                                        break;
                                    // 手机相册
                                    case 1:
                                        chosePic();
                                        break;
                                    default:
                                        break;
                                }
                                compressPath = Environment
                                        .getExternalStorageDirectory()
                                        .getPath()
                                        + "/fuiou_wmp/temp";
                                new File(compressPath).mkdirs();
                                compressPath = compressPath + File.separator
                                        + "compress.jpg";
                            }
                        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        if (mFilePathCallback != null) {
                            Uri[] uris = new Uri[1];
                            uris[0] = Uri.parse("");
                            mFilePathCallback.onReceiveValue(uris);
                            mFilePathCallback = null;
                        } else {
                            mUploadMessage.onReceiveValue(Uri.parse(""));
                            mUploadMessage = null;
                        }
                    }
                }).show();
    }

    String imagePaths;
    Uri cameraUri;

    /**
     * 打开照相机
     */
    private void openCarcme() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        imagePaths = Environment.getExternalStorageDirectory().getPath()
                + "/fuiou_wmp/temp/"
                + (System.currentTimeMillis() + ".jpg");
        // 必须确保文件夹路径存在，否则拍照后无法完成回调
        File vFile = new File(imagePaths);
        if (!vFile.exists()) {
            File vDirPath = vFile.getParentFile();
            vDirPath.mkdirs();
        } else {
            if (vFile.exists()) {
                vFile.delete();
            }
        }
        cameraUri = Uri.fromFile(vFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(intent, REQ_CAMERA);
    }

    /**
     * 拍照结束后
     */
    private void afterOpenCamera() {
        File f = new File(imagePaths);
        addImageGallery(f);
    }

    /**
     * 解决拍照后在相册中找不到的问题
     */
    private void addImageGallery(File file) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    /**
     * 本地相册选择图片
     */
    private void chosePic() {
        isAlbum = true;
        FileUtils.delFile(compressPath);
        Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); // "android.intent.action.GET_CONTENT"
        String IMAGE_UNSPECIFIED = "image/*";
        innerIntent.setType(IMAGE_UNSPECIFIED); // 查看类型
        Intent wrapperIntent = Intent.createChooser(innerIntent, null);
        startActivityForResult(wrapperIntent, REQ_CHOOSE);
    }

    /**
     * 选择照片后结束
     *
     * @param data
     */
    private Uri afterChosePic(Intent data) {

        // 获取图片的路径：
        String[] proj = {MediaStore.Images.Media.DATA};
        // 好像是android多媒体数据库的封装接口，具体的看Android文档
        Cursor cursor = managedQuery(data.getData(), proj, null, null, null);
        if (cursor == null) {
            Toast.makeText(this, "上传的图片仅支持png或jpg格式", Toast.LENGTH_SHORT).show();
            return null;
        }
        // 按我个人理解 这个是获得用户选择的图片的索引值
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        // 将光标移至开头 ，这个很重要，不小心很容易引起越界
        cursor.moveToFirst();
        // 最后根据索引值获取图片路径
        String path = cursor.getString(column_index);
        if (path != null && (path.endsWith(".png") || path.endsWith(".PNG") || path.endsWith(".jpg") || path.endsWith(".JPG"))) {
            File newFile = FileUtils.compressFile(path, compressPath);
            return Uri.fromFile(newFile);
        } else {
            Toast.makeText(this, "上传的图片仅支持png或jpg格式", Toast.LENGTH_SHORT).show();
        }
        return null;
    }


    /**
     * 返回文件选择
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (null == mUploadMessage && mFilePathCallback == null) {
            return;
        }
        Uri uri = null;
        if (requestCode == REQ_CAMERA) {
            File file = new File(cameraUri.getPath());
            if (!file.exists()) {
                cameraUri = Uri.parse("");
            }
            afterOpenCamera();
            uri = cameraUri;
        } else if (requestCode == REQ_CHOOSE) {
            if (null == intent) {
                uri = Uri.parse("");
            } else {
                uri = afterChosePic(intent);
            }
        }
        if (mFilePathCallback != null) {
            Uri[] uris = new Uri[1];
            uris[0] = uri;
            mFilePathCallback.onReceiveValue(uris);
        } else {
            mUploadMessage.onReceiveValue(uri);
        }
        mFilePathCallback = null;
        mUploadMessage = null;
        super.onActivityResult(requestCode, resultCode, intent);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        String url = mWebView.getUrl();
        if ("".equals(url)){
            if (System.currentTimeMillis() - clickTime > 2000L){
                Toast.makeText(getApplicationContext(), "再按一次后退键退出程序", Toast.LENGTH_SHORT).show();
                this.clickTime = System.currentTimeMillis();
                return true;
            }
            finish();
            return true;
        }
        if (!mWebView.canGoBack())
        {
            if (System.currentTimeMillis() - this.clickTime > 2000L)
            {
                Toast.makeText(getApplicationContext(), "再按一次后退键退出程序", Toast.LENGTH_SHORT).show();
                clickTime = System.currentTimeMillis();
                return true;
            }
            finish();
            return true;
        }
        if ("https://app.cv-china.com/Mall".equals(url) || "https://app.cv-china.com/Chaguan".equals(url) || "https://app.cv-china.com/Mall/home".equals(url))
        {
            if (System.currentTimeMillis() - this.clickTime > 2000L)
            {
                Toast.makeText(getApplicationContext(), "再按一次后退键退出程序", Toast.LENGTH_SHORT).show();
                this.clickTime = System.currentTimeMillis();
                return true;
            }
            finish();
            return true;
        }

        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        } else {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}