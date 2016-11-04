package com.lzh.yuanstrom.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.widget.ImageView;

import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.utils.Utils;
import com.lzh.yuanstrom.widget.ShareBottomDialog;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.tencent.tauth.Tencent;

import java.io.File;

import butterknife.BindView;

/**
 * Created by Administrator on 2016/11/4.
 */

public class AboutUsActivity extends BaseActivity {

    private ShareBottomDialog.BaseUiListener txListener;
    private IWeiboShareAPI weiboShareAPI;
    private IWeiboHandler.Response weiboResponse;

    private ShareBottomDialog dialog;

    String shareUrl = "www.baidu.com";

    @BindView(R.id.QR_img)
    ImageView qrImg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        dialog = new ShareBottomDialog(this);
        txListener = dialog.getTxListener();
        weiboShareAPI = dialog.getmWeiboShareAPI();
        weiboResponse = dialog.getWeiboResponse();

        new Thread(new Runnable() {
            @Override
            public void run() {
                final File file = new File(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/YuanStrom"), "YuanStrom.png");
                if (file.exists()) {
//                    qrImg.setImageURI(Uri.fromFile(file));
                } else {
                    int radius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getResources().getDimension(R.dimen.QR_img_size), getResources().getDisplayMetrics());
                    final Bitmap bitmap = Utils.createQRImage(shareUrl, radius, radius, BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
                    Utils.saveBitmap(context, "YuanStrom.png", bitmap);
                    bitmap.recycle();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        qrImg.setImageURI(Uri.fromFile(file));
                    }
                });

            }
        }).start();
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

}
