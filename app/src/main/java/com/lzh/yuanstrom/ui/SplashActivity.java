package com.lzh.yuanstrom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.utils.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.hekr.hekrsdk.util.SpCache;

/**
 * Created by Vicent on 2016/6/19.
 */
public class SplashActivity extends BaseActivity {

    @BindView(R.id.home_bg)
    LinearLayout homeBg;

    @BindView(R.id.btn_container)
    LinearLayout btnCon;

    @OnClick(R.id.register)
    void register() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        intent.putExtra("tag", "register");
        startActivity(intent);
    }

    @OnClick(R.id.login)
    void login() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        intent.putExtra("tag", "login");
        startActivity(intent);
    }

    private AlphaAnimation alphaAnimation;

    private AlphaAnimation btnAnimation;

    private String refresheToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ButterKnife.bind(this);
        refresheToken = SpCache.getString("refresh_TOKEN", "");
        if (StringUtils.isNotBlank(refresheToken)) {
            hekrUserAction.refresh_token();
        }
        setAnimation();
    }

    private void setAnimation() {
        alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(2000);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.e("tag", "animation finish");
                refresheToken = SpCache.getString("refresh_TOKEN", "");
                if (StringUtils.isBlank(refresheToken)) {
                    btnCon.setVisibility(View.VISIBLE);
                    btnCon.setAnimation(btnAnimation);
                } else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        homeBg.setAnimation(alphaAnimation);

        btnAnimation = new AlphaAnimation(0.0f, 1.0f);
        btnAnimation.setDuration(1000);
    }

}