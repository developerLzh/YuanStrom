package com.lzh.yuanstrom.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.utils.PhotoHelper;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/11/8.
 */

public class ScanActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fl_my_container)
    FrameLayout fl_my_container;

    @BindView(R.id.flash)
    ImageView flash;

    @BindView(R.id.photo)
    ImageView photo;

    private CaptureFragment captureFragment;

    public static final int PICTURE = 0X09;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        ButterKnife.bind(this);

        initBar();

        initView();
    }

    public static boolean isOpen = false;

    private void initView() {
        captureFragment = new CaptureFragment();
        // 为二维码扫描界面设置定制化界面
        CodeUtils.setFragmentArgs(captureFragment, R.layout.my_camera);
        captureFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, captureFragment).commit();

        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOpen) {
                    CodeUtils.isLightEnable(true);
                    isOpen = true;
                    flash.setImageResource(R.mipmap.flash_open);
                } else {
                    CodeUtils.isLightEnable(false);
                    isOpen = false;
                    flash.setImageResource(R.mipmap.flash_close);
                }
            }
        });
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gallery();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == PICTURE){
                if (data != null) {
                    // 得到图片的全路径
                    if (data != null) {
                        Uri dataUri = data.getData();
                        String realPath = PhotoHelper.getRealFilePath(context, dataUri);
                        try {
                            CodeUtils.analyzeBitmap(realPath, new CodeUtils.AnalyzeCallback() {
                                @Override
                                public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                                    Intent resultIntent = new Intent();
                                    Bundle bundle = new Bundle();
                                    bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
                                    bundle.putString(CodeUtils.RESULT_STRING, result);
                                    resultIntent.putExtras(bundle);
                                    ScanActivity.this.setResult(RESULT_OK, resultIntent);
                                    ScanActivity.this.finish();
                                }

                                @Override
                                public void onAnalyzeFailed() {
                                    Intent resultIntent = new Intent();
                                    Bundle bundle = new Bundle();
                                    bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
                                    bundle.putString(CodeUtils.RESULT_STRING, "");
                                    resultIntent.putExtras(bundle);
                                    ScanActivity.this.setResult(RESULT_OK, resultIntent);
                                    ScanActivity.this.finish();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initBar() {
        toolbar.setTitle(getString(R.string.scan_qr_code));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanActivity.this.onBackPressed();
            }
        });
    }

    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
            bundle.putString(CodeUtils.RESULT_STRING, result);
            resultIntent.putExtras(bundle);
            ScanActivity.this.setResult(RESULT_OK, resultIntent);
            ScanActivity.this.finish();
        }

        @Override
        public void onAnalyzeFailed() {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            bundle.putString(CodeUtils.RESULT_STRING, "");
            resultIntent.putExtras(bundle);
            ScanActivity.this.setResult(RESULT_OK, resultIntent);
            ScanActivity.this.finish();
        }
    };

    /*
  * 从相册获取
  */
    public void gallery() {
//        // 激活系统图库，选择一张图片
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, PICTURE);
    }

}
