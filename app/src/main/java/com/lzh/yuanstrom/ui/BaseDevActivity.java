package com.lzh.yuanstrom.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.lzh.yuanstrom.MyApplication;
import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.bean.DeviceInfo;
import com.lzh.yuanstrom.bean.LocalDeviceBean;
import com.lzh.yuanstrom.service.SocketService;
import com.lzh.yuanstrom.utils.StringUtils;
import com.lzh.yuanstrom.utils.ToastUtil;

import java.io.IOException;

import me.hekr.hekrsdk.util.ConstantsUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Vicent on 2016/3/4.
 */
public class BaseDevActivity extends BaseActivity {

    private DataBroadcastReceiver receiver;

    protected String devTid;

    protected LocalDeviceBean local;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        devTid = getIntent().getStringExtra("devTid");
        local = LocalDeviceBean.findByTid(devTid);
    }

    @Override
    protected void onStart() {
        super.onStart();
        receiver = new DataBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstantsUtil.ActionStrUtil.ACTION_WS_DATA_RECEIVE);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(receiver);
        super.onStop();
    }

    class DataBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent && StringUtils.isNotBlank(intent.getAction())) {
                String backData=intent.getStringExtra(ConstantsUtil.HEKR_WS_PAYLOAD);
                Log.e("backData",backData);
            }
        }
    }

    protected void detailData(String data) {
        //TODO
    }
}
