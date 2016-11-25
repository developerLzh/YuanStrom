package com.lzh.yuanstrom.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.bean.LocalDeviceBean;
import com.lzh.yuanstrom.utils.MySmartConfig;
import com.lzh.yuanstrom.widget.ProgressHUD;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.hekr.hekrsdk.bean.NewDeviceBean;

public class ConfigActivity extends BaseActivity implements View.OnClickListener {

    //private static final String TAG = "ConfigActivity";
    @BindView(R.id.ssid)
    TextView ssid;

    @BindView(R.id.pwd_input)
    EditText pwd_input;

    @BindView(R.id.device_connect_btn)
    Button connect;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.command_text)
    TextView commandText;

    @BindView(R.id.nested_scroll_view)
    NestedScrollView scrollView;

    @BindView(R.id.clear_txt)
    Button clearTxt;

    private BroadcastReceiver connectionReceiver;
    private MySmartConfig smartConfig;
    //示例demo逻辑跳转处理flag可自行根据逻辑修改
    private AtomicBoolean isSuccess = new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        ButterKnife.bind(this);
        setCanBackToolbar(getString(R.string.add_device));
        initView();
        initData();
    }

    private void initView() {
        if (connect != null) {
            connect.setOnClickListener(this);
        }
        clearTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commandText.setText("");
            }
        });
    }

    private void initData() {
        createReceiver();
        smartConfig = new MySmartConfig(ConfigActivity.this, new MySmartConfig.SetStrListener() {
            @Override
            public void setStr(final String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setAppendStr(s);
                    }
                });
            }

            @Override
            public void setStr(final String s, final int color) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setAppendStr(s, color);
                    }
                });
            }
        });

        discoverCallBack();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private int allDeviceNo = 0;
    private int newDeviceNo = 0;

    private void discoverCallBack() {

        smartConfig.setNewDeviceListener(new MySmartConfig.NewDeviceListener() {

            //单次配网时间内查询到的所有新设备(回调每次查询到的新设备列表)
            @Override
            public void getDeviceList(List<NewDeviceBean> newDeviceList) {

            }

            //单次配网时间内查询到的新设备(一旦有新的设备就会触发该回调接口)
            //只有newDeviceBean中属性bindResultCode值为0才算真正将该设备绑定到了自己账号下
            @Override
            public void getNewDevice(NewDeviceBean newDeviceBean) {
                setAppendStr(getString(R.string.found_device), Color.GREEN);
                setAppendStr(getString(R.string.device_serial_no) + newDeviceBean.getDevTid(), Color.GREEN);
                if (LocalDeviceBean.isChinese()) {
                    setAppendStr(getString(R.string.device_cate) + newDeviceBean.getCategoryName().getZh_CN(), Color.GREEN);
                } else {
                    setAppendStr(getString(R.string.device_cate) + newDeviceBean.getCategoryName().getEn_US(), Color.GREEN);
                }
                allDeviceNo++;
                if (newDeviceBean.getBindResultCode() == 0) {
                    //绑定成功的设备信息
                    newDeviceNo++;
                    isSuccess.set(true);
                    setAppendStr(getString(R.string.bind_suc), Color.GREEN);
                } else {
                    setAppendStr(getString(R.string.bind_failed) + newDeviceBean.getBindResultCode(), Color.RED);
                }
                setAppendStr(getString(R.string.continue_query_device), Color.GREEN);
            }

            //单次配网时间内查到存在新设备
            @Override
            public void getDeviceSuccess() {
                setAppendStr(getString(R.string.add_device_complete));
                setAppendStr(getString(R.string.total_founded) + allDeviceNo + getString(R.string.ge_device_new_bind) + newDeviceNo + getString(R.string.ge), Color.RED);
                if (isSuccess.get()) {
                    setAppendStr(getString(R.string.add_suc_will_exit), Color.GREEN);
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent i = new Intent();
                                    setResult(RESULT_OK, i);
                                    finish();
                                }
                            });
                        }
                    }, 2000);
                } else {
                    setAppendStr(getString(R.string.not_founded_any_device), Color.RED);
                }
                cancelProgressDialog();
            }

            //单次配网时间内未查询到任何新设备
            @Override
            public void getDeviceFail() {
                setAppendStr(getString(R.string.add_device_complete));
                if (allDeviceNo != 0) {
                    setAppendStr(getString(R.string.total_founded) + allDeviceNo + getString(R.string.ge_device_new_bind) + newDeviceNo + getString(R.string.ge), Color.RED);
                } else {
                    setAppendStr(getString(R.string.total_founded) + allDeviceNo + getString(R.string.error_cause));
                }
                setAppendStr(getString(R.string.not_founded_any_device), Color.RED);
                cancelProgressDialog();
            }
        });
    }

    boolean canAppend = true;

    private void cancelProgressDialog() {
        hideLoading();
        canAppend = false;
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
        hideInput();
        canAppend = true;
        clearTxt.setVisibility(View.VISIBLE);
        showLoading();
        setAppendStr(getString(R.string.ready));
        smartConfig.startConfig(ssid.getText().toString(), pwd_input.getText().toString(), 30);
        setAppendStr(getString(R.string.start_founded));
        isSuccess.set(false);
    }

    private void setAppendStr(String a) {
        if (canAppend) {
            SpannableStringBuilder builder = new SpannableStringBuilder(a);
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, a.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE); //设置指定位置文字的颜色
            commandText.append(builder);
            commandText.append("\n");
        }
//        scrollView.fullScroll(NestedScrollView.FOCUS_DOWN);
    }

    private void setAppendStr(String a, int color) {
        if (canAppend) {
            SpannableStringBuilder builder = new SpannableStringBuilder(a);
            builder.setSpan(new ForegroundColorSpan(color), 0, a.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE); //设置指定位置文字的颜色
            commandText.append(builder);
            commandText.append("\n");
        }
//        scrollView.fullScroll(NestedScrollView.FOCUS_DOWN);
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
//                        showLoading(false);
                        config();
                    }
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle(getResources().getString(R.string.app_name));
                    alert.setMessage(getString(R.string.empty_psw));
                    alert.setPositiveButton(getString(R.string.ensure), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (netWorkCheck()) {
                                if (!isFinishing()) {
//                                    showLoading(false);
                                    config();
                                }
                            } else {
                                Toast.makeText(ConfigActivity.this, getString(R.string.no_net), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    alert.setNegativeButton(getString(R.string.cancel), null).create().show();
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

    private void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),
                    0);
        }
    }

    protected void showLoading() {
        progressHUD = new ProgressDialog(this);
        progressHUD.setTitle("");
        progressHUD.setMessage(this.getString(R.string.query_ing));
        progressHUD.setCanceledOnTouchOutside(false);
        if (!progressHUD.isShowing() && !isFinishing()) {
            progressHUD.show();
        }
    }

}
