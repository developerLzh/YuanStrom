package com.lzh.yuanstrom.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.utils.AppManager;
import com.lzh.yuanstrom.utils.MySettingsHelper;
import com.lzh.yuanstrom.utils.RetrofitUtils;
import com.lzh.yuanstrom.widget.ProgressHUD;

import me.hekr.hekrsdk.action.HekrUserAction;
import me.hekr.hekrsdk.util.SpCache;
import okhttp3.MediaType;

/**
 * Created by Vicent on 2016/6/19.
 */
public class BaseActivity extends AppCompatActivity {
    BroadcastReceiver connectionReceiver;

    public static final MediaType TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String pid = SpCache.getString("HEKR_PID", "");

    protected ProgressHUD progressHUD;

    protected HekrUserAction hekrUserAction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hekrUserAction = HekrUserAction.getInstance(this);
        AppManager.getAppManager().addActivity(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        createReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(connectionReceiver);
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
                .setCancelable(cancelable)
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

    private void createReceiver() {
        // 创建网络监听广播
        connectionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

                    ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
                    if (netInfo != null && netInfo.isAvailable()) {
                        //网络连接
                        doHasNetWork();
                    } else {
                        doLoseNetWork();
                    }
                }
            }
        };
        // 注册网络监听广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectionReceiver, intentFilter);

    }

    protected void doLoseNetWork() {

    }

    protected void doHasNetWork() {
    }

}
