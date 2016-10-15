package com.lzh.yuanstrom.ui;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.utils.AppManager;
import com.lzh.yuanstrom.utils.AssetsDatabaseManager;
import com.lzh.yuanstrom.utils.MySettingsHelper;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vicent on 2016/8/20.
 */
public class LoginWebActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.web_view)
    WebView webView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private AssetsDatabaseManager mg;

    private SQLiteDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_web);

        ButterKnife.bind(this);

        toolbar.setTitle(getString(R.string.auth_login));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        initWeb();
    }

    private void initWeb() {
        Intent intent = getIntent();
        final String url = intent.getStringExtra("url");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setWebViewClient(new MyWebviewClient());

        mg = AssetsDatabaseManager.getManager();
        db = mg.getDatabase("db");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(url);
            }
        }, 1000);
    }

    class MyWebviewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            progressBar.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            if (url.contains("success.htm")) {
                CookieManager cookieManager = CookieManager.getInstance();
                String cookiestr = cookieManager.getCookie(url);

                HashMap<String, String> cookieMap = new HashMap<>();
                if (!TextUtils.isEmpty(cookiestr)) {
                    String cookieParams[] = cookiestr.split(";");

                    if (cookieParams.length > 0) {
                        for (int i = 0; i < cookieParams.length; i++) {
                            String kvParam[] = cookieParams[i].split("=");

                            if (kvParam.length == 2) {
                                cookieMap.put(kvParam[0].toString().trim(), kvParam[1].toString().trim());
                            }
                        }
                    }
                    if (cookieMap.containsKey("u")) {

                        if (TextUtils.isEmpty(MySettingsHelper.getCookieUser())) {
                            db.execSQL("INSERT INTO settings VALUES('user_credential','" + cookieMap.get("u") + "');");
                        }

                        Intent webToMain = new Intent(LoginWebActivity.this, MainActivity.class);
                        startActivity(webToMain);
                        AppManager.getAppManager().finishActivity(SplashActivity.class);
                        LoginWebActivity.this.finish();

                    }
                }
            }

            super.onPageFinished(view, url);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
