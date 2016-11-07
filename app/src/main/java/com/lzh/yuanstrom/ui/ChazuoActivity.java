package com.lzh.yuanstrom.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;

import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.bean.DeviceInfo;
import com.lzh.yuanstrom.utils.CommandHelper;
import com.lzh.yuanstrom.utils.FullCommandHelper;
import com.lzh.yuanstrom.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import me.hekr.hekrsdk.action.HekrUser;
import me.hekr.hekrsdk.bean.OAuthBean;
import me.hekr.hekrsdk.listener.DataReceiverListener;
import me.hekr.hekrsdk.util.MsgUtil;
import me.hekr.hekrsdk.util.SpCache;

/**
 * Created by Vicent on 2016/8/17.
 */
public class ChazuoActivity extends BaseDevActivity {

    @BindView(R.id.radio_1)
    Switch switch1;

    @BindView(R.id.radio_2)
    Switch switch2;

    @BindView(R.id.radio_3)
    Switch switch3;

    @BindView(R.id.radio_4)
    Switch switch4;

    @BindView(R.id.chazuo_img)
    ImageView chazuoImg;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.diandong)
    RadioButton diandong;

    @BindView(R.id.husuo)
    RadioButton husuo;

    @BindView(R.id.zisuo)
    RadioButton zisuo;

//        hekrUserAction.registerAuth();
//        hekrUserAction.getOAuthInfoRequest();
//        hekrUserAction.agreeOAuth();

    @OnCheckedChanged(R.id.zisuo)
    void zisuo() {
        if (zisuo.isChecked()) {
            zisuo.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            zisuo.setTextColor(getResources().getColor(R.color.text_default_color));
        }
    }

    @OnCheckedChanged(R.id.husuo)
    void husuo() {
        if (husuo.isChecked()) {
            husuo.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            husuo.setTextColor(getResources().getColor(R.color.text_default_color));
        }
    }

    @OnCheckedChanged(R.id.diandong)
    void diandong() {
        if (diandong.isChecked()) {
            diandong.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            diandong.setTextColor(getResources().getColor(R.color.text_default_color));
        }
    }

    private boolean needSendMsg = true;

    @OnCheckedChanged(R.id.radio_1)
    void switch1Check() {
        String switchState = "";
        if (switch1.isChecked()) {
            switchState = "01";
        } else {
            switchState = "02";
        }
        if (needSendMsg) {
        sendData(CommandHelper.SWITCH_COMMAND, "01", switchState);//发送命令
        } else {
            needSendMsg = true;
        }
    }

    @OnCheckedChanged(R.id.radio_2)
    void switch2Check() {
        String switchState = "";
        if (switch2.isChecked()) {
            switchState = "01";
        } else {
            switchState = "02";
        }
        if (needSendMsg) {
        sendData(CommandHelper.SWITCH_COMMAND, "02", switchState);//发送命令
        } else {
            needSendMsg = true;
        }
    }

    @OnCheckedChanged(R.id.radio_3)
    void switch3Check() {
        String switchState = "";
        if (switch3.isChecked()) {
            switchState = "01";
        } else {
            switchState = "02";
        }
        if (needSendMsg) {
        sendData(CommandHelper.SWITCH_COMMAND, "03", switchState);//发送命令
        } else {
            needSendMsg = true;
        }
    }

    @OnCheckedChanged(R.id.radio_4)
    void switch4Check() {
        String switchState = "";
        if (switch4.isChecked()) {
            switchState = "01";
        } else {
            switchState = "02";
        }
        if (needSendMsg) {
        sendData(CommandHelper.SWITCH_COMMAND, "04", switchState);//发送命令
        } else {
            needSendMsg = true;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch);

        ButterKnife.bind(this);

        husuo.setChecked(true);

        initBar();
    }

    private void initBar() {
        toolbar.setTitle(getString(R.string.dev_control));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChazuoActivity.this.onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDevState();
    }

    private void getDevState() {
        sendData(CommandHelper.RETURN_COMMAND, "00", "00");//获取开关状态
    }

    @Override
    protected void detailData(String useful) {
        super.detailData(useful);
        if (useful.length() < 10) {
            return;
        }
        String firstCommand = useful.substring(0, 2);
        String secondCommand = useful.substring(2, 4);
        String thirdCommand = useful.substring(4, 6);
        if (firstCommand.equals(CommandHelper.RETURN_COMMAND)) {
            needSendMsg = false;
            int a = Integer.parseInt(secondCommand, 16);
            changeSwitchState(a);
        } else if(firstCommand.equals(CommandHelper.SWITCH_COMMAND)){
            getDevState();
        }
    }

    private void changeSwitchState(int x) {
        if ((x & 1) == 1) {
            switch1.setChecked(true);
        } else {
            switch1.setChecked(false);
        }
        if ((x & 2) == 2) {
            switch2.setChecked(true);
        } else {
            switch2.setChecked(false);
        }
        if ((x & 4) == 4) {
            switch3.setChecked(true);
        } else {
            switch3.setChecked(false);
        }
        if ((x & 8) == 8) {
            switch4.setChecked(true);
        } else {
            switch4.setChecked(false);
        }
        needSendMsg = true;
    }

    @Override
    protected void detailSendFailed() {
        super.detailSendFailed();
    }
}
