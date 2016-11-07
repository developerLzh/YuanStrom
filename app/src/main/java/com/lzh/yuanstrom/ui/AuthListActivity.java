package com.lzh.yuanstrom.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.litesuits.android.log.Log;
import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.adapter.AuthListAdapter;
import com.lzh.yuanstrom.bean.AuthBean;
import com.lzh.yuanstrom.bean.LocalDeviceBean;
import com.lzh.yuanstrom.utils.StringUtils;
import com.lzh.yuanstrom.utils.ToastUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.hekr.hekrsdk.action.HekrUser;
import me.hekr.hekrsdk.action.HekrUserAction;
import me.hekr.hekrsdk.bean.OAuthListBean;
import me.hekr.hekrsdk.util.ConstantsUtil;
import me.hekr.hekrsdk.util.HekrCodeUtil;

/**
 * Created by Administrator on 2016/11/7.
 */

public class AuthListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.contentView)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.recycler)
    RecyclerView recyclerView;

    @BindView(R.id.grant_btn)
    Button grantBtn;

    @OnClick(R.id.grant_btn)
    void grantBtn() {
        showAuthDialog();
    }

    private AuthListAdapter adapter;

    private String devTid;

    private LocalDeviceBean local;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_list);

        devTid = getIntent().getStringExtra("devTid");
        local = LocalDeviceBean.findByTid(devTid);

        ButterKnife.bind(this);

        initToolbar();

        initRecycler();

        initSwipe();

        initDelete();

    }

    private void initDelete() {
        adapter.setDeleteClick(new AuthListAdapter.OnClickListener() {
            @Override
            public void OnClick(String grantee) {
                hekrUserAction.cancelOAuth(hekrUserAction.getUserId(), local.ctrlKey, grantee, local.devTid, new HekrUser.CancelOAuthListener() {
                    @Override
                    public void CancelOAuthSuccess() {
                        ToastUtil.showMessage(AuthListActivity.this,"取消授权成功");
                        swipeRefreshLayout.setRefreshing(true);
                        onRefresh();
                    }

                    @Override
                    public void CancelOauthFail(int i) {
                        ToastUtil.showMessage(AuthListActivity.this,HekrCodeUtil.errorCode2Msg(i));
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        onRefresh();
        swipeRefreshLayout.setRefreshing(true);
    }

    private void initSwipe() {
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void initRecycler() {
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new AuthListAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void initToolbar() {
        toolbar.setTitle(getString(R.string.share_info));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthListActivity.this.onBackPressed();
            }
        });
    }

    private void getAuthList() {
        hekrUserAction.getOAuthList(hekrUserAction.getUserId(), local.ctrlKey, devTid, new HekrUser.GetOAuthListener() {
            @Override
            public void getOAuthListSuccess(List<OAuthListBean> list) {
                swipeRefreshLayout.setRefreshing(false);
                adapter.setAuthListAdapter(list,devTid);
                Log.e("tag", "getOAuthListSuccess");
            }

            @Override
            public void getOAuthListFail(int i) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        getAuthList();
    }

    private void showAuthDialog() {
        final EditText editName = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(AuthListActivity.this)
                .setView(editName)
                .setTitle(getString(R.string.shouquan_device))
                .setPositiveButton(getString(R.string.ensure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String uid = editName.getText().toString();
                        if (StringUtils.isNotBlank(uid)) {
                            dialogInterface.dismiss();
                            shouquanDevice(uid);
                        } else {
                            ToastUtil.showMessage(AuthListActivity.this, getString(R.string.please_uid));
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();
        dialog.show();
    }

    private void shouquanDevice(String granteeUid) {
        AuthBean authBean = new AuthBean();
        authBean.ctrlKey = local.ctrlKey;
        authBean.devTid = local.devTid;
        authBean.grantor = hekrUserAction.getUserId();
        authBean.grantee = granteeUid;
        authBean.expire = (long) Math.pow(10, 10);
        authBean.mode = "ALL";
        authBean.desc = "";
        authBean.enableScheduler = true;
        authBean.enableIFTTT = true;
        CharSequence url = TextUtils.concat(new CharSequence[]{ConstantsUtil.UrlUtil.BASE_USER_URL, "authorization"});
        hekrUserAction.postHekrData(url, JSON.toJSONString(authBean), new HekrUserAction.GetHekrDataListener() {
            public void getSuccess(Object object) {
                Log.e("postHekrData",object.toString());
                swipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }

            public void getFail(int errorCode) {
                ToastUtil.showMessage(AuthListActivity.this, HekrCodeUtil.errorCode2Msg(errorCode));
            }
        });
    }
}
