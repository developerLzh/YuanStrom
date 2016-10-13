package com.lzh.yuanstrom.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzh.yuanstrom.MyApplication;
import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.adapter.DevAdapter2;
import com.lzh.yuanstrom.adapter.OnItemClickLinsenter;
import com.lzh.yuanstrom.animator.CustomItemAnimator;
import com.lzh.yuanstrom.bean.CardBean;
import com.lzh.yuanstrom.bean.DeviceInfo;
import com.lzh.yuanstrom.bean.GateWayBean;
import com.lzh.yuanstrom.service.SocketService;
import com.lzh.yuanstrom.utils.StringUtils;
import com.lzh.yuanstrom.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Vicent on 2016/5/9.
 */
public class DevActivity extends BaseDevActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.dev_swipe)
    SwipeRefreshLayout devSwipe;

    @BindView(R.id.dev_recycler)
    RecyclerView devRecycler;

    @BindView(R.id.fab_normal)
    FloatingActionButton fab;

    @BindView(R.id.rel_con)
    RelativeLayout rel_con;

    private GateWayBean gateWayBean;

    private List<DeviceInfo> deviceInfos;

    private DevAdapter2 adapter;

    private Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dev_activity);

        gateWayBean = (GateWayBean) getIntent().getSerializableExtra("card");
        ButterKnife.bind(this);

        deviceInfos = new ArrayList<>();

        startSocket();

        toolbar.setTitle(gateWayBean.name);
//        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DevActivity.this.onBackPressed();
            }
        });

        if (gateWayBean != null) {
            fillRow(rel_con, gateWayBean.tid, gateWayBean.detail);
            ((ImageView) rel_con.findViewById(R.id.appIcon)).setImageResource(R.mipmap.house_icon);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(coordinatorLayout, "o0o0o0o0o0", Snackbar.LENGTH_SHORT).show();
            }
        });
        initHandler();
        initRecyler();
    }

    private void startSocket() {
        Intent intent = new Intent(this, SocketService.class);
        intent.setPackage(getPackageName());
        intent.setAction(SocketService.OPEN_SOCKET);
        startService(intent);
    }

    private void initHandler() {
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 0://加载完成
                        devSwipe.setRefreshing(false);
                        devRecycler.setVisibility(View.VISIBLE);
                        adapter.setBeans(deviceInfos);
                        /**    同步云端数据       **/
//                        updateCloudDev(DeviceInfo.list2Json(DeviceInfo.findALll()));
                        break;
                    case 1://加载中
                        devRecycler.setVisibility(View.GONE);
                        devSwipe.setRefreshing(true);
                        break;
                }
                return false;
            }
        });
    }

    /**
     * fill the rows with some information
     *
     * @param view
     * @param title
     * @param description
     */
    public void fillRow(View view, final String title, final String description) {
        TextView titleView = (TextView) view.findViewById(R.id.title);
        titleView.setText(title);

        TextView descriptionView = (TextView) view.findViewById(R.id.description);
        descriptionView.setText(description);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("AppInfo", description);
                clipboard.setPrimaryClip(clip);

                Snackbar.make(coordinatorLayout, "Copied " + title, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void initRecyler() {
        devRecycler.setLayoutManager(new LinearLayoutManager(this));
        devRecycler.setItemAnimator(new CustomItemAnimator());
        adapter = new DevAdapter2(DevActivity.this, R.layout.dev_item_2);
        devRecycler.setAdapter(adapter);
        adapter.setLinsenter(new OnItemClickLinsenter() {
            @Override
            public void onItemClick(int position) {
                DeviceInfo deviceInfo = deviceInfos.get(position);

                if (deviceInfo.devCategory.equals(getString(R.string.lamp))) {
                    Intent intent = new Intent(DevActivity.this, LampActivity.class);
                    intent.putExtra("deviceInfo", deviceInfo);
                    DevActivity.this.startActivity(intent);
                } else if (deviceInfo.devCategory.equals(getString(R.string.chazuo))) {
                    Intent intent = new Intent(DevActivity.this, ChazuoActivity.class);
                    intent.putExtra("deviceInfo", deviceInfo);
                    DevActivity.this.startActivity(intent);
                } else if (deviceInfo.devCategory.equals(getString(R.string.yaokong))) {
                    Intent intent = new Intent(DevActivity.this, YaoKong2Activity.class);
                    intent.putExtra("deviceInfo", deviceInfo);
                    DevActivity.this.startActivity(intent);
                }

            }
        });
        devSwipe.setColorSchemeColors(getResources().getColor(R.color.barColor));
        devSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                getDevFromCloud();
            }
        });
//        getDevFromCloud();
    }

//    public void getDevFromCloud() {
//        deviceInfos.clear();
//        Request request = new Request.Builder()
//                .url("http://user.hekr.me/user/getPreferences.json?accesskey=" + MyApplication.getInstance().getSharedPreferences().getString("USERACCESSKEY", ""))
//                .get()
//                .build();
//        handler.sendEmptyMessage(0);
//        MyApplication.getInstance().getOkHttpClient().newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                ToastUtil.showMessage(DevActivity.this, getResources().getString(R.string.get_dev_from_cloud_failed));
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.code() != 200) {
//                    onFailure(null, null);
//                    return;
//                }
//                String data = response.body().string();
//                if (StringUtils.isNotBlank(data)) {
//                    try {
//                        JSONObject jbroot = new JSONObject(data.replace("\\", "").replace("\"[", "[").replace("]\"", "]"));
//                        JSONArray ja = jbroot.getJSONArray("preferences_json");
//                        DeviceInfo.deletAll();
//                        for (int i = 0; i < ja.length(); i++) {
//                            JSONObject jb = ja.optJSONObject(i);
//                            String a = jb.toString();
//                            Gson gson = new Gson();
//                            DeviceInfo deviceInfo = gson.fromJson(a, DeviceInfo.class);
//                            deviceInfo.isChecked = false;//未对比
//                            deviceInfo.saveNew();
//                        }
//                        generateCateList();
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                if (MyApplication.getInstance().isLoginSuccessed()) {
//                    String getAllDevComm = "(@devcall " + "\"" + gateWayBean.tid + "\" (uartdata \"" + "48070200100060" + "\") (lambda x x))\n";
//                    Intent intent = new Intent(DevActivity.this, SocketService.class);
//                    intent.setPackage(getPackageName());
//                    intent.setAction(SocketService.WRITE_MESSAGE);
//                    intent.putExtra("msg", getAllDevComm);
//                    startService(intent);
//                    //获取网关下所有设备信息
//                }
//            }
//        });
//    }

    protected void detailData(String data, String gateWay) {
        //TODO 处理列表数据  以下逻辑可能有问题  先不管了
        if (true) {
            return;
        }
        if (data.length() < 12) {
            return;
        }
        String command = data.substring(6, 10);
        if (command.equals("0010")) {
            DeviceInfo info = DeviceInfo.findByShortAddrTid(data.substring(10, 14), tid);
            if (info == null) {//不存在  说明是新设备
                info = new DeviceInfo();
                info.devShortAddr = data.substring(10, 14);
                info.isChecked = true;
                info.gateWaySerialNumber = gateWay;
                info.state = Integer.parseInt(data.substring(14, 16));
            } else {
                info.devShortAddr = data.substring(10, 14);
                info.isChecked = true;
                info.gateWaySerialNumber = gateWay;
                info.state = Integer.parseInt(data.substring(14, 16));
                info.updateSomething();
            }//流程是这样的  发送个获取所有设备的指令  他会返回一条一条的指令  每条指令代表不同的设备
        }
    }

    private void generateCateList() {
        deviceInfos.clear();
        deviceInfos = DeviceInfo.findALll();
        handler.sendEmptyMessage(0);//加载对比完成  展示
    }

    private void cloudListCompareSocketDeviceInfo(DeviceInfo SocketList) {

    }

    /**
     * helper class to start the new detailActivity animated
     *
     * @param card
     * @param appIcon
     */
    public void animateActivity(CardBean card, View appIcon) {
        Intent i = new Intent(this, DevActivity.class);
        i.putExtra("card", card);

        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, Pair.create((View) fab, "fab"), Pair.create(appIcon, "appIcon"));
        startActivity(i, transitionActivityOptions.toBundle());
    }
}
