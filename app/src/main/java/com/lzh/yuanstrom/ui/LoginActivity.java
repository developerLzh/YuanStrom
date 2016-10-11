package com.lzh.yuanstrom.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzh.yuanstrom.MyApplication;
import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.bean.DeviceKeyBean;
import com.lzh.yuanstrom.utils.RetrofitUtils;
import com.lzh.yuanstrom.utils.StringUtils;
import com.lzh.yuanstrom.utils.ToastUtil;
import com.lzh.yuanstrom.utils.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.http.FormUrlEncoded;

/**
 * Created by Administrator on 2016/10/10.
 */

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.input_layout_account)
    TextInputLayout accountLayout;

    @BindView(R.id.input_layout_psw)
    TextInputLayout pswLayout;

    @BindView(R.id.edit_account)
    EditText editAccount;

    @BindView(R.id.edit_psw)
    EditText editPsw;

    @BindView(R.id.login_btn)
    Button loginBtn;

    @BindView(R.id.forget_psw)
    TextView forgetPsw;

    @BindView(R.id.login_qq)
    LinearLayout loginQQ;

    @BindView(R.id.login_weibo)
    LinearLayout loginWeibo;

    @BindView(R.id.get_code)
    Button getCode;

    @OnClick(R.id.get_code)
    void getCode() {
        getCode.setEnabled(false);
        getUid();
        if (timer != null) {
            timer.cancel();
        }
        if (timerTask != null) {
            timerTask.cancel();
        }
        time = 0;
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                time++;
                if (time < 60) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getCode.setText(String.valueOf(60 - time));
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getCode.setText(getString(R.string.get_code));
                            getCode.setEnabled(true);
                        }
                    });
                }
            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }

    int time;
    Timer timer;
    TimerTask timerTask;

    String type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        type = getIntent().getStringExtra("tag");

        changeUiBytype(type);

    }

    private void changeUiBytype(String type) {
        if (type.equals("login")) {
            toolbar.setTitle(getResources().getString(R.string.login));
            accountLayout.setHint(getString(R.string.login_account_hint));
            pswLayout.setHint(getString(R.string.login_psw_hint));
            getCode.setVisibility(View.GONE);
            forgetPsw.setVisibility(View.VISIBLE);
        } else {
            toolbar.setTitle(getResources().getString(R.string.register));
            accountLayout.setHint(getString(R.string.phone_no));
            pswLayout.setHint(getString(R.string.verification_code));
            getCode.setVisibility(View.VISIBLE);
            forgetPsw.setVisibility(View.GONE);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.change) {
            Log.e("Tag", "menu changed");
            if (type.equals("login")) {
                type = "register";
            } else {
                type = "login";
            }
            changeUiBytype(type);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override//这里getItem()这个却是通过item的索引
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (type.equals("login")) {
            menu.add(0, R.id.change, 0, getString(R.string.register)).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        } else {
            menu.add(0, R.id.change, 0, getString(R.string.login)).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return true;
    }

    private void getUid(){
        RequestBody requestBody = new FormBody.Builder().add("token","bcb6fb9e-1ba8-4ef6-9235-2a65e2aaf8c5").build();
        Request.Builder builder = new Request.Builder()
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json");
        Request request = builder.url("http://uaa.openapi.hekr.me/"+ "register?type=phone")
                .post(requestBody)
                .build();
        new OkHttpClient().newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.e("failure","failure");
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if(response.code() != 200){
                    onFailure(null,null);
                    return;
                }
                String data = response.body().string();
                Log.e("data",data);
            }
        });
    }

}
