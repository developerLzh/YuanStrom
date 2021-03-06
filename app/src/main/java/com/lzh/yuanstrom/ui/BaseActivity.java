package com.lzh.yuanstrom.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.utils.AppManager;

import me.hekr.hekrsdk.action.HekrUserAction;

/**
 * Created by Vicent on 2016/6/19.
 */
public class BaseActivity extends AppCompatActivity {
    protected AlertDialog progressHUD;

    public HekrUserAction hekrUserAction;

    protected Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        hekrUserAction = HekrUserAction.getInstance(this);
        AppManager.getAppManager().addActivity(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void finish() {
        super.finish();
    }

    public void showLoading(boolean cancelable) {
        progressHUD = new ProgressDialog(this);
        progressHUD.setTitle("");
        progressHUD.setMessage(this.getString(R.string.wait));
        progressHUD.setCanceledOnTouchOutside(cancelable);
        if (!progressHUD.isShowing() && !BaseActivity.this.isFinishing()) {
            progressHUD.show();
        }
    }

    public void hideLoading() {
        if (null != progressHUD && progressHUD.isShowing()) {
            progressHUD.dismiss();
        }
    }

    protected void setCanBackToolbar(String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.this.onBackPressed();
            }
        });
    }
}
