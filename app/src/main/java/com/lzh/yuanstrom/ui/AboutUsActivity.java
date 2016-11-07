package com.lzh.yuanstrom.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.utils.Utils;
import com.lzh.yuanstrom.widget.ShareBottomDialog;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.tencent.tauth.Tencent;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/11/4.
 */

public class AboutUsActivity extends BaseActivity {

    private ShareBottomDialog.BaseUiListener txListener;
    private IWeiboShareAPI weiboShareAPI;
    private IWeiboHandler.Response weiboResponse;

    private ShareBottomDialog dialog;

    String shareUrl = "http://yuanjifeng.iok.la:51400/HTML/html/index.html#";

    @BindView(R.id.QR_img)
    ImageView qrImg;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);

        initToolbar();

        new Thread(new Runnable() {
            @Override
            public void run() {
                final File file = new File(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/YuanStrom"), "QrCode.png");
                final Bitmap bitmap;
                if (file.exists()) {
//                    qrImg.setImageURI(Uri.fromFile(file));
                    bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/YuanStrom/QrCode.png");
                } else {
                    int radius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getResources().getDimension(R.dimen.QR_img_size), getResources().getDisplayMetrics());
                    bitmap = Utils.createQRImage(shareUrl, radius, radius, BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
                    Utils.saveBitmap(context, "QrCode.png", bitmap);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        qrImg.setImageBitmap(bitmap);
                    }
                });

            }
        }).start();
    }

    private void initToolbar() {
        if (toolbar != null) {
            toolbar.setTitle(getString(R.string.about_us));
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AboutUsActivity.this.onBackPressed();
                }
            });
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (null != weiboShareAPI) {
            weiboShareAPI.handleWeiboResponse(intent, weiboResponse); //当前应用唤起微博分享后，返回当前应用
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode, resultCode, data, txListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            dialog = new ShareBottomDialog(this);
            txListener = dialog.getTxListener();
            weiboShareAPI = dialog.getmWeiboShareAPI();
            weiboResponse = dialog.getWeiboResponse();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
