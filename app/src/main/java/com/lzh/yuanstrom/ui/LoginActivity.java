package com.lzh.yuanstrom.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.httphelper.NormalSubscriber;
import com.lzh.yuanstrom.httphelper.SubscriberListener;
import com.lzh.yuanstrom.utils.AppManager;
import com.lzh.yuanstrom.utils.StringUtils;
import com.lzh.yuanstrom.utils.ToastUtil;
import com.lzh.yuanstrom.utils.Utils;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import me.hekr.hekrsdk.HekrOAuthLoginActivity;
import me.hekr.hekrsdk.action.HekrUser;
import me.hekr.hekrsdk.action.HekrUserAction;
import me.hekr.hekrsdk.bean.JWTBean;
import me.hekr.hekrsdk.bean.MOAuthBean;
import me.hekr.hekrsdk.util.BaseHttpUtil;
import me.hekr.hekrsdk.util.ConstantsUtil;
import com.lzh.yuanstrom.utils.HekrCodeUtil;
import me.hekr.hekrsdk.util.SpCache;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vicent on 2016/10/10.
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
    void loginOrRegister() {
        String phone = editAccount.getText().toString();
        String psw = editPsw.getText().toString();
        String code = editCode.getText().toString();
        if (phone.length() < 11 || !phone.matches("[0-9]+")) {
            Snackbar.make(home, getString(R.string.please_legal_phone), Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (type.equals("login")) {
            if (StringUtils.isBlank(phone) || StringUtils.isBlank(psw)) {
                Snackbar.make(home, getString(R.string.blank_account_or_psw), Snackbar.LENGTH_SHORT).show();
                return;
            }
            login(phone, psw);
        } else {
            if (StringUtils.isBlank(phone) || StringUtils.isBlank(psw) || StringUtils.isBlank(code)) {
                Snackbar.make(home, getString(R.string.blank_account_or_psw_or_code), Snackbar.LENGTH_SHORT).show();
                return;
            }
            checkVerifyCode(phone, code, psw);
        }
    }

    @BindView(R.id.forget_psw)
    TextView forgetPsw;

    @OnClick(R.id.forget_psw)
    void forgetPsw() {
        startActivity(new Intent(LoginActivity.this, ResetActivity.class));
    }

    @BindView(R.id.login_qq)
    LinearLayout loginQQ;

    @OnClick(R.id.login_qq)
    void loginQQ() {
        Intent intent = new Intent(LoginActivity.this, HekrOAuthLoginActivity.class);
        intent.putExtra(HekrOAuthLoginActivity.OAUTH_TYPE, HekrUserAction.OAUTH_QQ);
        startActivityForResult(intent, HekrUserAction.OAUTH_QQ);
    }

    @BindView(R.id.login_weibo)
    LinearLayout loginWeibo;

    @OnClick(R.id.login_weibo)
    void loginWeibo() {
        Intent intent3 = new Intent(LoginActivity.this, HekrOAuthLoginActivity.class);
        intent3.putExtra(HekrOAuthLoginActivity.OAUTH_TYPE, HekrUserAction.OAUTH_SINA);
        startActivityForResult(intent3, HekrUserAction.OAUTH_SINA);
    }

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
        herkGetCode(phone);
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

        hekrUserAction = HekrUserAction.getInstance(this);

        type = getIntent().getStringExtra("tag");

        changeUiBytype(type);

        String phone = getIntent().getStringExtra("phone");
        String password = getIntent().getStringExtra("password");
        if (StringUtils.isNotBlank(phone) && StringUtils.isNotBlank(password)) {
            editAccount.setText(phone);
            editPsw.setText(password);
            login(phone, password);
        }
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

    private void login(String userName, String password) {
        showLoading(true);
        hekrUserAction.login(userName, password, new HekrUser.LoginListener() {
            @Override
            public void loginSuccess(String s) {
                Log.e("login", "login success");
                hideLoading();
                AppManager.getAppManager().finishActivity(SplashActivity.class);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void loginFail(int i) {
                hideLoading();
                Snackbar.make(home, HekrCodeUtil.errorCode2Msg(context,i), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void checkVerifyCode(final String phone, final String code, final String password) {
        showLoading(true);
        hekrUserAction.checkVerifyCode(phone, code, new HekrUser.CheckVerifyCodeListener() {
            @Override
            public void checkVerifyCodeSuccess(String s, String s1, String s2, String s3) {
                String token = s2;
                String phone = s;
                register(phone, password, token);
            }

            @Override
            public void checkVerifyCodeFail(int i) {
                hideLoading();
                Snackbar.make(home, HekrCodeUtil.errorCode2Msg(context,i), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void register(final String phone, final String psw, String token) {
        hekrUserAction.registerByPhone(phone, psw, token, new HekrUser.RegisterListener() {
            @Override
            public void registerSuccess(String s) {
                hideLoading();
                login(phone, psw);
                Log.e("register", "register success");
            }

            @Override
            public void registerFail(int i) {
                hideLoading();
                Snackbar.make(home, HekrCodeUtil.errorCode2Msg(context,i), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void herkGetCode(final String phone) {
        showLoading(true);


        final String rid = Utils.random17();
        hekrUserAction.getImgCaptcha(rid, new HekrUser.GetImgCaptchaListener() {
            @Override
            public void getImgCaptchaSuccess(Bitmap bitmap) {
                hideLoading();
                showImgDialog(rid, phone, bitmap);
            }

            @Override
            public void getImgCaptchaFail(int i) {
                hideLoading();
                Snackbar.make(home, HekrCodeUtil.errorCode2Msg(context,i), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void showImgDialog(final String rid, final String phone, Bitmap bitmap) {
        View v = LayoutInflater.from(LoginActivity.this).inflate(R.layout.img_code_layout, null);
        final EditText editText = (EditText) v.findViewById(R.id.edit_img_code);
        ImageView imgCode = (ImageView) v.findViewById(R.id.code_img);
        imgCode.setImageBitmap(bitmap);
        AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
                .setTitle(getString(R.string.please_img_code))
                .setView(v)
                .setPositiveButton(getString(R.string.ensure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        String code = editText.getText().toString();
                        if (StringUtils.isBlank(code)) {
                            ToastUtil.showMessage(LoginActivity.this, getString(R.string.please_img_code));
                        } else {
                            hekrUserAction.checkCaptcha(code, rid, new HekrUser.CheckCaptcha() {
                                @Override
                                public void checkCaptchaSuccess(String s) {
                                    dialog.dismiss();
                                    getMessageCode(phone, s);
                                }

                                @Override
                                public void checkCaptchaFail(int i) {
                                    Snackbar.make(home, HekrCodeUtil.errorCode2Msg(context,i), Snackbar.LENGTH_SHORT).show();
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
                }).create();
        dialog.show();
    }

    private void getMessageCode(String phone, String s) {
        showLoading(true);
        hekrUserAction.getVerifyCode(phone, 1, s, new HekrUser.GetVerifyCodeListener() {
            @Override
            public void getVerifyCodeSuccess() {
                hideLoading();
                getCode.setEnabled(false);
                Snackbar.make(home, getString(R.string.get_code_success), Snackbar.LENGTH_SHORT).show();
                startCountDown();
            }

            @Override
            public void getVerifyCodeFail(int i) {
                hideLoading();
                Snackbar.make(home, HekrCodeUtil.errorCode2Msg(context,i), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String certificate = data.getStringExtra(HekrOAuthLoginActivity.OAUTH_CODE);
            if (!TextUtils.isEmpty(certificate)) {
                showLoading(true);
                loginOAuth(resultCode, certificate);
            }
        }
    }

    //移动端OAuth接口
    private void loginOAuth(final int type, String certificate) {
        //通过上一步拿到的certificate进行第三方登录
        hekrUserAction.OAuthLogin(type, certificate, new HekrUser.MOAuthListener() {
            @Override
            public void mOAuthSuccess(MOAuthBean moAuthBean) {
                //该OAuth账号还未和主账号绑定
                createUserAndBind(type, moAuthBean.getBindToken());
            }

            @Override
            public void mOAuthSuccess(JWTBean jwtBean) {
                //该OAuth账号已经和主账号绑定
                hideLoading();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void mOAuthFail(int errorCode) {
                //失败
                hideLoading();
                Snackbar.make(home, HekrCodeUtil.errorCode2Msg(context,errorCode), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    //创建匿名帐号并且绑定
    private void createUserAndBind(final int type, final String bindToken) {
        hekrUserAction.createUserAndBind(type, bindToken, new HekrUser.CreateUserAndBindListener() {
            @Override
            public void createSuccess(String str) {
                hideLoading();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void createFail(int errorCode) {
                //失败
                hideLoading();
                Snackbar.make(home, HekrCodeUtil.errorCode2Msg(context,errorCode), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

}
