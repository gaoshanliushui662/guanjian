package com.guanjian.mm;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
    private Button btn_stop;
    private TXLivePusher mLivePusher;
    private TXLivePushConfig mLivePushConfig;
    private TXCloudVideoView mCaptureView;
    private String rtmpUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zhibo);
        Intent intent = getIntent();
        rtmpUrl = intent.getStringExtra("rtmpUrl");
        requestPermission(FORCE_REQUIRE_PERMISSIONS, true, new PermissionsResultListener() {
            @Override
            public void onPermissionGranted() {
                initView();
                initZhiBo();
            }

            @Override
            public void onPermissionDenied() {
                Toast.makeText(ZhiBoActivity.this, "拒绝申请权限", Toast.LENGTH_LONG).show();
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
        btn_stop = (Button) findViewById(R.id.btn_stop);
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

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLivePusher.stopCameraPreview(true); //停止摄像头预览
                mLivePusher.stopPusher();            //停止推流
                mLivePusher.setPushListener(null);   //解绑 listener
            }
        });
    }
}
