package com.lzh.yuanstrom.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.bean.DeviceResult;
import com.lzh.yuanstrom.bean.LocalDeviceBean;
import com.lzh.yuanstrom.utils.CommandHelper;
import com.lzh.yuanstrom.utils.FullCommandHelper;
import com.lzh.yuanstrom.utils.StringUtils;
import com.lzh.yuanstrom.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import me.hekr.hekrsdk.listener.DataReceiverListener;
import me.hekr.hekrsdk.util.ConstantsUtil;
import me.hekr.hekrsdk.util.MsgUtil;

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

    public void sendData(String firstCommand,String secondCommand,String thirdCommand){
        CommandHelper commandHelper = new CommandHelper.CommandBuilder().setFirstCommand(firstCommand).setSecondCommand(secondCommand).setThirdCommand(thirdCommand).build();
        FullCommandHelper fullCommandHelper = new FullCommandHelper(devTid, local.ctrlKey, commandHelper.toString());
        Log.e("commandHelper", fullCommandHelper.toString());
        showLoading(true);
        try {
            MsgUtil.sendMsg(BaseDevActivity.this, devTid, new JSONObject(fullCommandHelper.toString()), new DataReceiverListener() {
                @Override
                public void onReceiveSuccess(String s) {
                    Log.e("onReceiveSuccess", s);
                    hideLoading();
                    ToastUtil.showMessage(BaseDevActivity.this,getString(R.string.send_suc));
                }

                @Override
                public void onReceiveTimeout() {
                    hideLoading();
                    detailSendFailed();
                    ToastUtil.showMessage(BaseDevActivity.this,getString(R.string.send_failed));
                }
            }, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class DataBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent && StringUtils.isNotBlank(intent.getAction())) {
                String backData=intent.getStringExtra(ConstantsUtil.HEKR_WS_PAYLOAD);
                DeviceResult result = new Gson().fromJson(backData,DeviceResult.class);
                if(result != null && result.params != null && result.params.data != null && result.params.data.raw.length() != 0) {
                    String data = result.params.data.raw;
                    String useful = data.substring(8,data.length() - 2);
                    detailData(useful);
                } else {
                    ToastUtil.showMessage(context,getString(R.string.dev_error));
                }
                Log.e("backData",backData);
            }
        }
    }

    protected void detailData(String useful) {
        Log.e("usefudata",useful);
    }

    protected void detailSendFailed(){

    }
}
