package com.lzh.yuanstrom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.httphelper.NormalSubscriber;
import com.lzh.yuanstrom.httphelper.SubscriberListener;
import com.lzh.yuanstrom.utils.AppManager;
import com.lzh.yuanstrom.utils.StringUtils;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.hekr.hekrsdk.action.HekrUser;
import com.lzh.yuanstrom.utils.HekrCodeUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/10/15.
 */

public class ResetActivity extends BaseActivity {

    @BindView(R.id.home)
    LinearLayout home;

    @BindView(R.id.edit_account)
    EditText editAccount;

    @BindView(R.id.edit_psw)
    EditText editPsw;

    @BindView(R.id.edit_code)
    EditText editCode;

    @BindView(R.id.login_btn)
    Button loginBtn;

    @BindView(R.id.get_code)
    Button getCode;
    @OnClick(R.id.get_code)
    void getCode(){
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

    private void herkGetCode(String phone) {
        showLoading(true);
        hekrUserAction.getVerifyCode(phone, 2, new HekrUser.GetVerifyCodeListener() {
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

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @OnClick(R.id.login_btn)
    void loginOrRegister() {
        String phone = editAccount.getText().toString();
        String psw = editPsw.getText().toString();
        String code = editCode.getText().toString();
        if (phone.length() < 11 || !phone.matches("[0-9]+")) {
            Snackbar.make(home, getString(R.string.please_legal_phone), Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (StringUtils.isBlank(phone) || StringUtils.isBlank(psw) || StringUtils.isBlank(code)) {
            Snackbar.make(home, getString(R.string.blank_account_or_psw_or_code), Snackbar.LENGTH_SHORT).show();
            return;
        }
        resetPsw(phone, code, psw);
    }

    private void resetPsw(final String phone, String code, final String psw) {
        showLoading(true);
        hekrUserAction.resetPwd(phone, code, psw, new HekrUser.ResetPwdListener() {
            @Override
            public void resetSuccess() {
                hideLoading();
                AppManager.getAppManager().finishActivity(LoginActivity.class);
                Intent intent = new Intent(ResetActivity.this,LoginActivity.class);
                intent.putExtra("tag","login");
                intent.putExtra("phone",phone);
                intent.putExtra("password",psw);
                startActivity(intent);
            }

            @Override
            public void resetFail(int i) {
                hideLoading();
                Snackbar.make(home, HekrCodeUtil.errorCode2Msg(context,i), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_psw);
        ButterKnife.bind(this);

        setCanBackToolbar(getString(R.string.reset_psw));
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

}
