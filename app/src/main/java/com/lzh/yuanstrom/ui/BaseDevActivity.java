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
import com.lzh.yuanstrom.service.SocketService;
import com.lzh.yuanstrom.utils.StringUtils;
import com.lzh.yuanstrom.utils.ToastUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Vicent on 2016/3/4.
 */
public class BaseDevActivity extends AppCompatActivity {

    protected String tid;

    protected String shortAddr;

    public String currentDecCate;

    private DataBroadcastReceiver receiver;

    protected DeviceInfo deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceInfo = getIntent().getParcelableExtra("deviceInfo");
        if(deviceInfo == null){
            return;
        }
        tid = deviceInfo.gateWaySerialNumber;
        shortAddr = deviceInfo.devShortAddr;
        currentDecCate = deviceInfo.devCategory;
    }

    @Override
    protected void onStart() {
        super.onStart();
        receiver = new DataBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SocketService.DETAIL_DATA);
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
                if (intent.getAction().equals(SocketService.DETAIL_DATA)) {
                    String data = intent.getStringExtra("data");
                    String gateWay = intent.getStringExtra("gateWay");
                    detailData(data, gateWay);
                }
            }
        }
    }

    protected void detailData(String data, String gateWay) {
        //TODO
    }

//    protected void updateCloudDev(String jsonDevsList) {
//        FormBody.Builder bodyBuilder = new FormBody.Builder();
//        bodyBuilder.add("preferences_json", jsonDevsList);
//        FormBody formBody = bodyBuilder.build();
//        Request request = new Request.Builder()
//                .url("http://user.hekr.me/user/setPreferences.json?accesskey=" + MyApplication.getInstance().getSharedPreferences().getString("USERACCESSKEY", ""))
//                .post(formBody)
//                .build();
//        MyApplication.getInstance().getOkHttpClient().newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                ToastUtil.showMessage(BaseDevActivity.this, getResources().getString(R.string.upload_dev_failed));
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.code() != 200) {
//                    onFailure(null, null);
//                    return;
//                }
//                Log.e("updateCloudDev", "upload success");
//            }
//        });
//    }

    public void writeMessage(String message){
        String fullCommand = "(@devcall " + "\"" + tid + "\" (uartdata \"" + message + "\") (lambda x x))\n";
        Intent intent = new Intent(BaseDevActivity.this, SocketService.class);
        intent.setPackage(getPackageName());
        intent.setAction(SocketService.WRITE_MESSAGE);
        intent.putExtra("msg", fullCommand);
        startService(intent);
    }

}
