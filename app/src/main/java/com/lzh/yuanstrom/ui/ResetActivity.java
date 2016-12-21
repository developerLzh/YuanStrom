package com.lzh.yuanstrom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.utils.AppManager;
import com.lzh.yuanstrom.utils.HekrCodeUtil;
import com.lzh.yuanstrom.utils.StringUtils;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.hekr.hekrsdk.action.HekrUser;

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

    private Timer timer;
    private TimerTask timerTask;

    private void startCountDown() {
        if(timer != null || timerTask != null){
            timer.cancel();
            timerTask.cancel();
            time = 0;
            timer = null;
            timerTask = null;
        }
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                time++;
                if (time < 60) {
                    handler.sendEmptyMessage(0);
                } else {
                    handler.sendEmptyMessage(1);
                }
            }
        };
        timer.schedule(timerTask,0,1000);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    getCode.setText(String.valueOf(60 - time));
                    break;
                case 1:
                    getCode.setText(getString(R.string.get_code));
                    getCode.setEnabled(true);

                    timer.cancel();
                    timerTask.cancel();
                    time = 0;
                    timer = null;
                    timerTask = null;
                    break;
            }
            return true;
        }
    });

    int time;

}
