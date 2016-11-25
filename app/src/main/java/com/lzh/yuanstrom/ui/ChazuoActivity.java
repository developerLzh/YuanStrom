package com.lzh.yuanstrom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.bean.LocalDeviceBean;
import com.lzh.yuanstrom.utils.CommandHelper;
import com.lzh.yuanstrom.utils.FullCommandHelper;
import com.lzh.yuanstrom.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.hekr.hekrsdk.listener.DataReceiverListener;
import me.hekr.hekrsdk.util.MsgUtil;

/**
 * Created by Vicent on 2016/8/17.
 */
public class ChazuoActivity extends BaseDevActivity {

    @BindView(R.id.radio_1)
    CheckBox switch1;

    @BindView(R.id.radio_2)
    CheckBox switch2;

    @BindView(R.id.radio_3)
    CheckBox switch3;

    @BindView(R.id.radio_4)
    CheckBox switch4;

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

    @BindView(R.id.radio_group)
    RadioGroup radioGroup;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @OnClick(R.id.zisuo)
    void zisuo() {
        zisuo.setTextColor(getResources().getColor(R.color.colorPrimary));
        sendSetData("01", "00", zisuo);
    }

    @OnClick(R.id.husuo)
    void husuo() {
        sendSetData("02", "00", husuo);
    }

    @OnClick(R.id.diandong)
    void diandong() {
        sendSetData("00", "00", diandong);
    }

    private RadioButton lastCheckedSet;

    private boolean needSendMsg = true;

    @OnClick(R.id.radio_1)
    void switch1Check() {
        String switchState = "";
        if (switch1.isChecked()) {
            switchState = "01";
        } else {
            switchState = "02";
        }
        if (needSendMsg) {
            sendSwitchData(CommandHelper.SWITCH_COMMAND, "01", switchState, switch1);//发送命令
        } else {
            needSendMsg = true;
        }
    }

    @OnClick(R.id.radio_2)
    void switch2Check() {
        String switchState = "";
        if (switch2.isChecked()) {
            switchState = "01";
        } else {
            switchState = "02";
        }
        if (needSendMsg) {
            sendSwitchData(CommandHelper.SWITCH_COMMAND, "02", switchState, switch2);//发送命令
        } else {
            needSendMsg = true;
        }
    }

    @OnClick(R.id.radio_3)
    void switch3Check() {
        String switchState = "";
        if (switch3.isChecked()) {
            switchState = "01";
        } else {
            switchState = "02";
        }
        if (needSendMsg) {
            sendSwitchData(CommandHelper.SWITCH_COMMAND, "03", switchState, switch3);//发送命令
        } else {
            needSendMsg = true;
        }
    }

    @OnClick(R.id.radio_4)
    void switch4Check() {
        String switchState = "";
        if (switch4.isChecked()) {
            switchState = "01";
        } else {
            switchState = "02";
        }
        if (needSendMsg) {
            sendSwitchData(CommandHelper.SWITCH_COMMAND, "04", switchState, switch4);//发送命令
        } else {
            needSendMsg = true;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch);

        ButterKnife.bind(this);

        setCanBackToolbar(getString(R.string.dev_control));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChazuoActivity.this,TimingActivity.class);
                intent.putExtra("devTid",devTid);
                intent.putExtra("ctrlKey", LocalDeviceBean.findByTid(devTid).ctrlKey);
                startActivity(intent);
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

            int b = Integer.parseInt(thirdCommand, 16);
            if (b == 0) {
                lastCheckedSet = diandong;
                changeSet(diandong, true);
            } else if (b == 1) {
                lastCheckedSet = zisuo;
                changeSet(zisuo, true);
            } else if (b == 2) {
                lastCheckedSet = husuo;
                changeSet(husuo, true);
            }
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

    private void sendSwitchData(String firstCommand, String secondCommand, final String thirdCommand, final CheckBox switchA) {
        CommandHelper commandHelper = new CommandHelper.CommandBuilder().setFirstCommand(firstCommand).setSecondCommand(secondCommand).setThirdCommand(thirdCommand).build();
        FullCommandHelper fullCommandHelper = new FullCommandHelper(devTid, local.ctrlKey, commandHelper.toString());
        Log.e("commandHelper", fullCommandHelper.toString());
        showLoading(true);
        try {
            MsgUtil.sendMsg(ChazuoActivity.this, devTid, new JSONObject(fullCommandHelper.toString()), new DataReceiverListener() {
                @Override
                public void onReceiveSuccess(String s) {
                    Log.e("onReceiveSuccess", s);
                    hideLoading();
                    ToastUtil.showMessage(ChazuoActivity.this, getString(R.string.send_suc));
                    changeSwitch(thirdCommand, switchA, true);
                }

                @Override
                public void onReceiveTimeout() {
                    hideLoading();
                    detailSendFailed();
                    ToastUtil.showMessage(ChazuoActivity.this, getString(R.string.send_failed));
                    changeSwitch(thirdCommand, switchA, false);
                }
            }, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendSetData(String secondCommand, final String thirdCommand, final RadioButton radioButton) {
        CommandHelper commandHelper = new CommandHelper.CommandBuilder().setFirstCommand("04").setSecondCommand(secondCommand).setThirdCommand(thirdCommand).build();
        FullCommandHelper fullCommandHelper = new FullCommandHelper(devTid, local.ctrlKey, commandHelper.toString());
        Log.e("commandHelper", fullCommandHelper.toString());
        showLoading(true);
        try {
            MsgUtil.sendMsg(ChazuoActivity.this, devTid, new JSONObject(fullCommandHelper.toString()), new DataReceiverListener() {
                @Override
                public void onReceiveSuccess(String s) {
                    Log.e("onReceiveSuccess", s);
                    hideLoading();
                    ToastUtil.showMessage(ChazuoActivity.this, getString(R.string.send_suc));
                    getDevState();
                }

                @Override
                public void onReceiveTimeout() {
                    hideLoading();
                    detailSendFailed();
                    ToastUtil.showMessage(ChazuoActivity.this, getString(R.string.send_failed));
                    changeSet(radioButton, false);
                }
            }, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void changeSet(RadioButton radioButton, boolean success) {
        if (success) {
            diandong.setTextColor(getResources().getColor(R.color.text_default_color));
            zisuo.setTextColor(getResources().getColor(R.color.text_default_color));
            husuo.setTextColor(getResources().getColor(R.color.text_default_color));
            if (radioButton.getId() == R.id.zisuo) {
                zisuo.setChecked(true);
                zisuo.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else if (radioButton.getId() == R.id.husuo) {
                husuo.setChecked(true);
                husuo.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else if (radioButton.getId() == R.id.diandong) {
                diandong.setChecked(true);
                diandong.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        } else {
            diandong.setTextColor(getResources().getColor(R.color.text_default_color));
            zisuo.setTextColor(getResources().getColor(R.color.text_default_color));
            husuo.setTextColor(getResources().getColor(R.color.text_default_color));
            if (lastCheckedSet.getId() == R.id.zisuo) {
                zisuo.setChecked(true);
                zisuo.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else if (lastCheckedSet.getId() == R.id.husuo) {
                husuo.setChecked(true);
                husuo.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else if (lastCheckedSet.getId() == R.id.diandong) {
                diandong.setChecked(true);
                diandong.setTextColor(getResources().getColor(R.color.colorPrimary));
            }

        }
    }

    private void changeSwitch(String thirdCommand, CheckBox switchA, boolean success) {
        if (success) {
            if (diandong.isChecked()) {
                switch1.setChecked(false);
                switch2.setChecked(false);
                switch3.setChecked(false);
                switch4.setChecked(false);
            } else if (zisuo.isChecked()) {
                if (thirdCommand.equals("01")) {
                    switchA.setChecked(true);
                } else {
                    switchA.setChecked(false);
                }
            } else if (husuo.isChecked()) {
                switch1.setChecked(false);
                switch2.setChecked(false);
                switch3.setChecked(false);
                switch4.setChecked(false);
                switchA.setChecked(true);
            }

        } else {
            if (thirdCommand.equals("02")) {
                switchA.setChecked(true);
            } else {
                switchA.setChecked(false);
            }
        }
    }

}
