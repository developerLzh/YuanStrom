package com.lzh.yuanstrom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.lzh.yuanstrom.MyApplication;
import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.adapter.GateWayAdapter;
import com.lzh.yuanstrom.animator.CustomItemAnimator;
import com.lzh.yuanstrom.bean.DeviceKeyBean;
import com.lzh.yuanstrom.bean.GateWayBean;
import com.lzh.yuanstrom.service.SocketService;
import com.lzh.yuanstrom.utils.RetrofitUtils;
import com.lzh.yuanstrom.utils.StringUtils;
import com.lzh.yuanstrom.utils.ToastUtil;
import com.lzh.yuanstrom.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Request;

public class GateWayAct extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.container)
    RelativeLayout container;

    private Handler handler;

    private GateWayAdapter adapter;

    private List<GateWayBean> beans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gate_way);

        ButterKnife.bind(this);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        beans = new LinkedList<>();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        mSwipeRefreshLayout.setRefreshing(false);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        adapter.setBeans(beans);
                        break;
                    case 1:
                        mRecyclerView.setVisibility(View.GONE);
                        break;
                }
                return false;
            }
        });

        initRecyler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getKeyFromHerk();
        getGateList();
//        getGateList2();
    }

    private void initRecyler() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new CustomItemAnimator());
        adapter = new GateWayAdapter(GateWayAct.this, R.layout.gateway_item, GateWayAct.this);
        mRecyclerView.setAdapter(adapter);

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.barColor));
        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getGateList();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gate_way, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Snackbar.make(coordinatorLayout, "Replace with your own action", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * helper class to start the new detailActivity animated
     *
     * @param card
     * @param appIcon
     */
    public void animateActivity(GateWayBean card, View appIcon) {
        Intent i = new Intent(this, DevActivity.class);
        i.putExtra("card", card);

        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, Pair.create((View) fab, "fab"), Pair.create(appIcon, "appIcon"));
        GateWayAct.this.startActivity(i, transitionActivityOptions.toBundle());
    }

    private void getKeyFromHerk() {
        Map<String, String> map = new HashMap<>();
        map.put("_csrftoken_", "abcd");
        map.put("type", "DEVICE");
        Request request = new Request.Builder().url(RetrofitUtils.BASE_URL + "token/generate.json?" + Utils.hashToGetParam(map)).get().build();
        MyApplication.getInstance().getOkHttpClient().newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                MyApplication.getInstance().getEditor().putString("DEVACCESSKEY", "").apply();
                MyApplication.getInstance().getEditor().putString("uid", "").apply();
                ToastUtil.showMessage(GateWayAct.this, "请求DEVACCESSKEY失败");
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if(response.code() != 200){
                    onFailure(null,null);
                    return;
                }
                String data = response.body().string();
                if (StringUtils.isNotBlank(data)) {
                    DeviceKeyBean deviceKeyBean = new Gson().fromJson(data, DeviceKeyBean.class);
                    MyApplication.getInstance().getEditor().putString("DEVACCESSKEY", deviceKeyBean.token).apply();
                    MyApplication.getInstance().getEditor().putString("uid", deviceKeyBean.uid).apply();
                }
            }
        });

        map.put("type", "USER");
        request = new Request.Builder().url(RetrofitUtils.BASE_URL + "token/generate.json?" + Utils.hashToGetParam(map)).get().build();
        MyApplication.getInstance().getOkHttpClient().newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                ToastUtil.showMessage(GateWayAct.this, "请求USERACCESSKEY失败");
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if(response.code() != 200){
                    onFailure(null,null);
                    return;
                }
                String data = response.body().string();
                if (StringUtils.isNotBlank(data)) {
                    try {
                        JSONObject jb = new JSONObject(data);
                        MyApplication.getInstance().getEditor().putString("USERACCESSKEY", jb.optString("token")).apply();
                    } catch (JSONException e) {
                        MyApplication.getInstance().getEditor().putString("USERACCESSKEY", "").apply();
                        ToastUtil.showMessage(GateWayAct.this, "解析USERACCESSKEY数据异常");
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getGateList() {
        handler.sendEmptyMessage(1);
        Map<String, String> map = new HashMap<>();
        map.put("_csrftoken_", "abcd");
        Request request = new Request.Builder().url(RetrofitUtils.BASE_URL + "device/list.json?" + Utils.hashToGetParam(map)).get().build();
        MyApplication.getInstance().getOkHttpClient().newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                beans.clear();
                ToastUtil.showMessage(GateWayAct.this, "加载GateWay列表失败");
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                String data = response.body().string();
                Gson gson = new Gson();
                try {
                    JSONArray ja = new JSONArray(data);
                    beans.clear();
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jsonObject = (JSONObject) ja.get(i);
                        String a = jsonObject.toString();
                        GateWayBean bean = gson.fromJson(a, GateWayBean.class);
                        beans.add(bean);
                    }
                    handler.sendEmptyMessage(0);
                } catch (JSONException e) {
                    ToastUtil.showMessage(GateWayAct.this, "解析数据异常");
                    beans.clear();
                    handler.sendEmptyMessage(0);
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void doHasNetWork() {
        super.doHasNetWork();
        getGateList();
    }

    @Override
    protected void doLoseNetWork() {
        super.doLoseNetWork();
        Snackbar.make(coordinatorLayout, "失去网络连接，是否前往设置中心设置网络", Snackbar.LENGTH_SHORT).setAction("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
                startActivity(wifiSettingsIntent);
            }
        }).show();
    }

    //    private void getGateList2(){
//        RetrofitUtils.createApi(ApiService.class).getDeviceList("abcd").enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                String a = response.body();
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                Log.e("t","失败");
//            }
//        });
//    }

}
