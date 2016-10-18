package com.lzh.yuanstrom.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lzh.yuanstrom.R;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.hekr.hekrsdk.bean.NewDeviceBean;
import me.hekr.hekrsdk.util.SmartConfig;

public class ConfigActivity extends AppCompatActivity implements View.OnClickListener {

    //private static final String TAG = "ConfigActivity";
    @BindView(R.id.ssid)
    TextView ssid;

    @BindView(R.id.pwd_input)
    EditText pwd_input;

    @BindView(R.id.device_connect_btn)
    Button connect;

    @BindView(R.id.device_info)
    TextView deviceInfoTxt;

    private BroadcastReceiver connectionReceiver;
    private SmartConfig smartConfig;
    private ProgressDialog progressDialog;
    //示例demo逻辑跳转处理flag可自行根据逻辑修改
    private AtomicBoolean isSuccess = new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        ssid = (TextView) findViewById(R.id.ssid);
        pwd_input = (EditText) findViewById(R.id.pwd_input);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("正在配网...");
        progressDialog.setCanceledOnTouchOutside(false);

        if (connect != null) {
            connect.setOnClickListener(this);
        }
    }

    private void initData() {
        createReceiver();
        smartConfig = new SmartConfig(ConfigActivity.this);

        discoverCallBack();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void discoverCallBack() {

        smartConfig.setNewDeviceListener(new SmartConfig.NewDeviceListener() {

            //单次配网时间内查询到的所有新设备(回调每次查询到的新设备列表)
            @Override
            public void getDeviceList(List<NewDeviceBean> newDeviceList) {

            }

            //单次配网时间内查询到的新设备(一旦有新的设备就会触发该回调接口)
            //只有newDeviceBean中属性bindResultCode值为0才算真正将该设备绑定到了自己账号下
            @Override
            public void getNewDevice(NewDeviceBean newDeviceBean) {
                deviceInfoTxt.setText(newDeviceBean.toString());
                if (newDeviceBean.getBindResultCode() == 0) {
                    //绑定成功的设备信息
                    isSuccess.set(true);
                    Toast.makeText(ConfigActivity.this, newDeviceBean.toString(), Toast.LENGTH_LONG).show();
                }
            }

            //单次配网时间内查到存在新设备
            @Override
            public void getDeviceSuccess() {
                cancelProgressDialog();
                Toast.makeText(ConfigActivity.this, "配网成功!", Toast.LENGTH_LONG).show();
                if (isSuccess.get()) {
                    Toast.makeText(ConfigActivity.this, "成功配置设备!", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(ConfigActivity.this, MainActivity.class);
                    setResult(RESULT_OK, i);
                    finish();
                }
            }

            //单次配网时间内未查询到任何新设备
            @Override
            public void getDeviceFail() {
                cancelProgressDialog();
                Toast.makeText(ConfigActivity.this, "配网失败!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cancelProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * 监听网络变化
     */
    public void createReceiver() {
        // 创建网络监听广播
        if (connectionReceiver == null) {
            connectionReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
                        if (netInfo != null && netInfo.isAvailable()) {
                            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                            String nowWifi = wifiManager.getConnectionInfo().getSSID().replace("\"", "");
                            if (!TextUtils.isEmpty(nowWifi)) {
                                ssid.setText(nowWifi);
                            }
                        } else {
                            ssid.setText("");
                            pwd_input.setText("");
                        }
                    }
                }
            };
            // 注册网络监听广播
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(connectionReceiver, intentFilter);
        }
    }

    /**
     * 点击配网按钮之后
     * 1、发送ssid &&pwd
     * 2、启动发现服务
     */
    private void config() {
        smartConfig.startConfig(ssid.getText().toString(), pwd_input.getText().toString(), 30);
        isSuccess.set(false);
    }

    /**
     * @return 当前网络是否是wifi
     */
    private boolean netWorkCheck() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isAvailable() && netInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.device_connect_btn:
                if (!TextUtils.isEmpty(pwd_input.getText().toString().trim())) {
                    if (!isFinishing()) {
                        progressDialog.show();
                        config();
                    }
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle(getResources().getString(R.string.app_name));
                    alert.setMessage("密码为空?");
                    alert.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (netWorkCheck()) {
                                if (!isFinishing()) {
                                    progressDialog.show();
                                    config();
                                }
                            } else {
                                Toast.makeText(ConfigActivity.this, "无可用网络!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    alert.setNegativeButton("取消", null).create().show();
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelProgressDialog();
        smartConfig.stopConfig();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectionReceiver != null) {
            unregisterReceiver(connectionReceiver);
        }
    }
}
