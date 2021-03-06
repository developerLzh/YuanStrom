package com.lzh.yuanstrom.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.lzh.yuanstrom.MyApplication;
import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.adapter.FirstPageAdapter;
import com.lzh.yuanstrom.bean.LocalDeviceBean;
import com.lzh.yuanstrom.utils.BitmapCache;
import com.lzh.yuanstrom.utils.StringUtils;
import com.lzh.yuanstrom.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.hekr.hekrsdk.action.HekrUser;

import com.lzh.yuanstrom.utils.HekrCodeUtil;

/**
 * Created by Administrator on 2016/10/25.
 */

public class DevControlActivity extends BaseActivity {

    @BindView(R.id.root_view)
    LinearLayout rootView;

    @BindView(R.id.dev_cate)
    TextView devCate;

    @BindView(R.id.current_wifi)
    TextView currentWifi;

    @BindView(R.id.to_control)
    Button toControl;

    @BindView(R.id.delete_dev)
    Button deleteDev;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.dev_name)
    TextView devName;

    @BindView(R.id.dev_img)
    ImageView devImg;

    @BindView(R.id.auth_share)
    RelativeLayout authShare;

    @BindView(R.id.timing_task)
    RelativeLayout timingTask;

    private String devTid;

    private LocalDeviceBean local;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_set);
        ButterKnife.bind(this);
        devTid = getIntent().getStringExtra("devTid");
        local = LocalDeviceBean.findByTid(devTid);
        setCanBackToolbar(getString(R.string.device_info));

        MyApplication.getInstance().getImageLoader().get(local.logo, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean b) {
                Bitmap bitmap = response.getBitmap();
                if (null != bitmap) {
                    devImg.setImageBitmap(bitmap);
                } else {
                    devImg.setImageBitmap(null);
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                devImg.setImageBitmap(null);
            }
        });

        if (StringUtils.isBlank(local.devTid)) {
            ToastUtil.showMessage(this, context.getString(R.string.dev_not_exist));
            finish();
            return;
        }
        initView();
        createReceiver();
    }

    private void initView() {
        devCate.setText(local.categoryName);
        devName.setText(local.deviceName + " ");
        currentWifi.setText("");
        toControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirstPageAdapter.toWhatActivityByCateName(context, local.categoryName, devTid);
            }
        });
        deleteDev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSureDialog();
            }
        });
        devName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeNameDialog(local.deviceName);
            }
        });
        authShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DevControlActivity.this, AuthListActivity.class);
                intent.putExtra("devTid", devTid);
                startActivity(intent);
            }
        });
        timingTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DevControlActivity.this, TimingActivity.class);
                intent.putExtra("devTid", devTid);
                intent.putExtra("ctrlKey", LocalDeviceBean.findByTid(devTid).ctrlKey);
                startActivity(intent);
            }
        });
    }

    private BroadcastReceiver connectionReceiver;

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
                                currentWifi.setText(nowWifi);
                            }
                        } else {
                            currentWifi.setText("");
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

    private void showSureDialog() {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.hint))
                .setMessage(context.getString(R.string.sure_delete))
                .setPositiveButton(context.getString(R.string.ensure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        //TODO delete device
                        hekrUserAction.deleteDevice(devTid, local.bindKey, new HekrUser.DeleteDeviceListener() {
                            @Override
                            public void deleteDeviceSuccess() {
                                Snackbar.make(rootView, getString(R.string.delete_dev_suc), Snackbar.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void deleteDeviceFail(int i) {
                                Snackbar.make(rootView, HekrCodeUtil.errorCode2Msg(context, i), Snackbar.LENGTH_SHORT).show();
                            }
                        });
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    private void showChangeNameDialog(final String name) {
        final EditText edit = new EditText(this);
        edit.setText(name);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.change_dev))
                .setView(edit)
                .setPositiveButton(getString(R.string.ensure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        final String s = edit.getText().toString();
                        if (StringUtils.isNotBlank(s)) {
                            hekrUserAction.renameDevice(devTid, local.ctrlKey, s, local.desc, new HekrUser.RenameDeviceListener() {
                                @Override
                                public void renameDeviceSuccess() {
                                    dialog.dismiss();
                                    Snackbar.make(rootView, getString(R.string.change_dev_suc), Snackbar.LENGTH_SHORT).show();
                                    devName.setText(s);
                                }

                                @Override
                                public void renameDeviceFail(int i) {
                                    Snackbar.make(rootView, HekrCodeUtil.errorCode2Msg(context, i), Snackbar.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectionReceiver != null) {
            unregisterReceiver(connectionReceiver);
        }
    }
}
