package com.lzh.yuanstrom.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.bean.LocalDeviceBean;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.hekr.hekrsdk.action.HekrUser;
import me.hekr.hekrsdk.bean.OAuthBean;

/**
 * Created by Administrator on 2016/11/7.
 */

public class AuthDevActivity extends BaseActivity {

    private LocalDeviceBean local;

    @BindView(R.id.QR_img)
    ImageView qrImg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        ButterKnife.bind(this);

        local = LocalDeviceBean.findByTid(getIntent().getStringExtra("devTid"));

        getAuthCode();
//        hekrUserAction.registerAuth();
//        hekrUserAction.getOAuthList();
//        hekrUserAction.agreeOAuth();
    }

    private void getAuthCode() {
        OAuthBean authBean = new OAuthBean();
        authBean.setCtrlKey(local.ctrlKey);
        authBean.setDevTid(local.devTid);
        authBean.setEnableIFTTT(true);
        authBean.setEnableScheduler(true);
        authBean.setExpire(100000000 * 100000000);
        authBean.setMode("ALL");
        authBean.setGrantor(hekrUserAction.getUserId());
        hekrUserAction.oAuthCreateCode(authBean, new HekrUser.CreateOAuthQRCodeListener() {
            @Override
            public void createSuccess(String s) {
                Log.e("oAuthCreateCode", s);
            }

            @Override
            public void createFail(int i) {

            }
        });
    }
}
