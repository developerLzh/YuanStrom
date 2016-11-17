package com.lzh.yuanstrom.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.utils.Utils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/11/8.
 */

public class MyCodeActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.QR_img)
    ImageView qrImg;

    String shareUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_code);

        ButterKnife.bind(this);

        initBar();

        shareUrl = getString(R.string.company_url) + hekrUserAction.getUserId();

        new Thread(new Runnable() {
            @Override
            public void run() {
                final File file = new File(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/YuanStrom"), "MyQrCode.png");
                final Bitmap bitmap;
                int radius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getResources().getDimension(R.dimen.QR_img_size) / 4, getResources().getDisplayMetrics());
                if (file.exists()) {
                    bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/YuanStrom/MyQrCode.png");
                } else {
                    bitmap = Utils.createQRImage(shareUrl, radius, radius, BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
                }
                Utils.saveBitmap(context, "MyQrCode.png", bitmap);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        qrImg.setImageBitmap(bitmap);
                    }
                });
            }
        }).start();
    }

    private void initBar() {
        toolbar.setTitle(getString(R.string.my_code));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyCodeActivity.this.onBackPressed();
            }
        });
    }
}
