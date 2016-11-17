package com.lzh.yuanstrom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzh.yuanstrom.MyApplication;
import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.utils.AppManager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/11/15.
 */

public class ChangeLanguageActivity extends BaseActivity {

    @BindView(R.id.flow_system)
    RelativeLayout flowSystem;

    @BindView(R.id.zh)
    RelativeLayout zh;

    @BindView(R.id.en)
    RelativeLayout en;

    @BindView(R.id.flow_txt)
    TextView flowTxt;

    @BindView(R.id.zh_txt)
    TextView zhTxt;

    @BindView(R.id.en_txt)
    TextView enTxt;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);
        ButterKnife.bind(this);

        initToolbar();

        initView();

        initClick();
    }

    private void initClick() {
        flowSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reloadLanguage("default");
            }
        });
        en.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reloadLanguage("en");
            }
        });
        zh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reloadLanguage("zh");
            }
        });
    }

    private void initView() {
        String language = MyApplication.getInstance().getSharedPreferences().getString("language", "default");
        if (language.equals("zh")) {
            zh.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
            zhTxt.setTextColor(getResources().getColor(R.color.white));
        } else if (language.equals("en")) {
            en.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
            enTxt.setTextColor(getResources().getColor(R.color.white));
        } else {
            flowSystem.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
            flowTxt.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void reloadLanguage(String language) {
        MyApplication.getInstance().getEditor().putString("language", language).apply();
        AppManager.getAppManager().finishAllActivity();
        startActivity(new Intent(this, SplashActivity.class));
    }

    private void initToolbar() {
        if (toolbar != null) {
            toolbar.setTitle(getString(R.string.change_language));
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChangeLanguageActivity.this.onBackPressed();
                }
            });
        }
    }
}
