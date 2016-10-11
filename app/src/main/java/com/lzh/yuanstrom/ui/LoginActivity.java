package com.lzh.yuanstrom.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
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
import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.bean.CheckVerifyResult;
import com.lzh.yuanstrom.bean.GetCodeModel;
import com.lzh.yuanstrom.bean.LoginModel;
import com.lzh.yuanstrom.bean.LoginResult;
import com.lzh.yuanstrom.bean.RegisterModel;
import com.lzh.yuanstrom.bean.RegisterResult;
import com.lzh.yuanstrom.httphelper.ApiService;
import com.lzh.yuanstrom.httphelper.HttpResultFunc;
import com.lzh.yuanstrom.httphelper.InternetSubscriber;
import com.lzh.yuanstrom.httphelper.NormalSubscriber;
import com.lzh.yuanstrom.httphelper.SubscriberListener;
import com.lzh.yuanstrom.utils.RetrofitUtils;
import com.lzh.yuanstrom.utils.StringUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/10/10.
 */

public class LoginActivity extends BaseActivity {

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
    @OnClick(R.id.login_btn)
    void loginOrRegister(){
        String phone = editAccount.getText().toString();
        String psw = editPsw.getText().toString();
        String code = editCode.getText().toString();
        if (phone.length() < 11 || !phone.matches("[0-9]+")) {
            Snackbar.make(home, getString(R.string.please_legal_phone), Snackbar.LENGTH_SHORT).show();
            return;
        }
        if(type.equals("login")){
            if(StringUtils.isBlank(phone) || StringUtils.isBlank(psw)){
                Snackbar.make(home,getString(R.string.blank_account_or_psw),Snackbar.LENGTH_SHORT).show();
                return;
            }
            LoginModel model = new LoginModel();
            model.setPid(pid);
            model.setPassword(psw);
            model.setClientType("ANDROID");
            model.setUsername(phone);
            login(new Gson().toJson(model));
        }else{
            if(StringUtils.isBlank(phone) || StringUtils.isBlank(psw) || StringUtils.isBlank(code)){
                Snackbar.make(home,getString(R.string.blank_account_or_psw_or_code),Snackbar.LENGTH_SHORT).show();
                return;
            }
            checkVerifyCode(phone,code,psw);
        }
    }

    @BindView(R.id.forget_psw)
    TextView forgetPsw;

    @BindView(R.id.login_qq)
    LinearLayout loginQQ;

    @BindView(R.id.login_weibo)
    LinearLayout loginWeibo;

    @BindView(R.id.get_code)
    Button getCode;

    @BindView(R.id.home)
    LinearLayout home;

    @BindView(R.id.yanzheng_con)
    LinearLayout yanzhengCon;

    @BindView(R.id.edit_code)
    EditText editCode;

    @OnClick(R.id.get_code)
    void getCode() {
        String phone = editAccount.getText().toString();
        if (StringUtils.isBlank(phone)) {
            Snackbar.make(home, getString(R.string.please_phone), Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (phone.length() < 11 || !phone.matches("[0-9]+")) {
            Snackbar.make(home, getString(R.string.please_legal_phone), Snackbar.LENGTH_SHORT).show();
            return;
        }
        code(phone);
    }

    private void startCountDown() {
        Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe(new NormalSubscriber<>(new SubscriberListener<Long>() {
                    @Override
                    public void onNext(Long aLong) {
                        time++;
                        if (time < 60) {
                            getCode.setText(String.valueOf(60 - time));
                        } else {
                            getCode.setText(getString(R.string.get_code));
                            getCode.setEnabled(true);
                        }
                    }

                }));
    }

    int time;

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
            loginBtn.setText(getString(R.string.login));
            yanzhengCon.setVisibility(View.GONE);
            forgetPsw.setVisibility(View.VISIBLE);
        } else {
            toolbar.setTitle(getResources().getString(R.string.register));
            accountLayout.setHint(getString(R.string.phone_no));
            loginBtn.setText(getString(R.string.register));
            yanzhengCon.setVisibility(View.VISIBLE);
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

    private void login(String jsonStr) {
        RetrofitUtils.createRxApi(ApiService.class, RetrofitUtils.HEKR_NEW)
                .login(RequestBody.create(TYPE_JSON, jsonStr))
                .map(new HttpResultFunc<LoginResult>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new InternetSubscriber<>(this, true, false, new SubscriberListener<LoginResult>() {
                    @Override
                    public void onNext(LoginResult result) {
                        Log.e("data", result.getAccess_token());
                    }

                }));
    }

    private void register(String jsonStr) {
        RetrofitUtils.createRxApi(ApiService.class, RetrofitUtils.HEKR_NEW)
                .register(RequestBody.create(TYPE_JSON, jsonStr))
                .map(new HttpResultFunc<RegisterResult>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new InternetSubscriber<>(this, true, false, new SubscriberListener<RegisterResult>() {
                    @Override
                    public void onNext(RegisterResult result) {

                    }

                }));
    }

    private void checkVerifyCode(final String phone, String code, final String password) {
        RetrofitUtils.createRxApi(ApiService.class, RetrofitUtils.HEKR_NEW)
                .checkVerifyCode(phone, code)
                .map(new HttpResultFunc<CheckVerifyResult>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new InternetSubscriber<>(this, true, false, new SubscriberListener<CheckVerifyResult>() {
                    @Override
                    public void onNext(CheckVerifyResult result) {
                        RegisterModel model = new RegisterModel();
                        model.setPassword(password);
                        model.setToken(result.token);
                        model.setPhoneNumber(phone);
                        model.setPid(pid);
                        String jsonStr = new Gson().toJson(model);
                        register(jsonStr);
                    }

                }));
    }

    private void code(String phone) {
//        GetCodeModel model = new GetCodeModel();
//        model.setPhoneNumber(phone);
//        model.setPid(pid);
//        model.setType("register");
//        RequestBody requestBody = RequestBody.create(TYPE_JSON,new Gson().toJson(model));
        RetrofitUtils.createRxApi(ApiService.class, RetrofitUtils.HEKR_NEW)
                .getVerifyCode(phone,pid,"register")
                .map(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new InternetSubscriber<>(this, true, false, new SubscriberListener<Object>() {
                    @Override
                    public void onNext(Object o) {
                        getCode.setEnabled(false);
                        Snackbar.make(home, getString(R.string.get_code_success), Snackbar.LENGTH_SHORT).show();
                        startCountDown();
                    }
                }));
    }

    private void okGetCode(String phone){
        final Request request = new Request.Builder()
                .url("http://uaa.openapi.hekr.me/sms/getVerifyCode?phoneNumber="+phone+"&pid=00000000000&type=register")
                .build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("response",response.code()+"");
                Log.e("response",response.body().string()+"");
            }
        });
    }
}
