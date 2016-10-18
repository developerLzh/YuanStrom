package com.lzh.yuanstrom.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.utils.AppManager;
import com.lzh.yuanstrom.widget.ProgressHUD;

import me.hekr.hekrsdk.action.HekrUserAction;

/**
 * Created by Vicent on 2016/6/19.
 */
public class BaseActivity extends AppCompatActivity {
    protected ProgressHUD progressHUD;

    protected HekrUserAction hekrUserAction;

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
        AppManager.getAppManager().finishActivity(this);
        super.finish();
    }

    protected void showLoading(boolean cancelable){
        progressHUD = new ProgressHUD.Builder(this)
                .setTitle("")
                .setMessage(this.getString(R.string.wait))
                .setTouchOnOutSide(cancelable)//全部设置成可取消
                .create();
        if (!progressHUD.isShowing()) {
            progressHUD.show();
        }
    }

    protected void hideLoading(){
        if(null != progressHUD && progressHUD.isShowing()){
            progressHUD.dismiss();
        }
    }
}
