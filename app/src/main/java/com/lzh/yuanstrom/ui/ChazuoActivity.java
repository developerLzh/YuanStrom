package com.lzh.yuanstrom.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.Switch;

import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.bean.DeviceInfo;
import com.lzh.yuanstrom.utils.CommandHelper;
import com.lzh.yuanstrom.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/8/17.
 */
public class ChazuoActivity extends BaseDevActivity {

    @BindView(R.id.switch1)
    Switch switch1;

    @BindView(R.id.switch2)
    Switch switch2;

    @BindView(R.id.switch3)
    Switch switch3;

    @BindView(R.id.chazuo_img)
    ImageView chazuoImg;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private DeviceInfo deviceInfo;

    @OnCheckedChanged(R.id.switch1)
    void switch1Check() {
        String switchState = "";
        if (switch1.isChecked()) {
            switchState = "01";
        } else {
            switchState = "02";
        }
        String command = new CommandHelper.CommandBuilder().setFrameCommand("0002").setShortAddr(deviceInfo.devShortAddr).setAction("01" + switchState).build();
        writeMessage(command);
    }

    @OnCheckedChanged(R.id.switch2)
    void switch2Check() {
        String switchState = "";
        if (switch2.isChecked()) {
            switchState = "01";
        } else {
            switchState = "02";
        }
        String command = new CommandHelper.CommandBuilder().setFrameCommand("0002").setShortAddr(deviceInfo.devShortAddr).setAction("02" + switchState).build();
        writeMessage(command);
    }

    @OnCheckedChanged(R.id.switch3)
    void switch3Check() {
        String switchState = "";
        if (switch3.isChecked()) {
            switchState = "01";
        } else {
            switchState = "02";
        }
        String command = new CommandHelper.CommandBuilder().setFrameCommand("0002").setShortAddr(deviceInfo.devShortAddr).setAction("04" + switchState).build();
        writeMessage(command);
    }

    @OnClick(R.id.fab)
    void fabClick() {
        ToastUtil.showMessage(ChazuoActivity.this, "fab on clicked");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch);

        ButterKnife.bind(this);

        deviceInfo =  getIntent().getParcelableExtra("deviceInfo");
        getSwitchState();

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }

    private void getSwitchState() {
        String command = new CommandHelper.CommandBuilder().setFrameCommand("0010").setShortAddr(deviceInfo.devShortAddr).build();
        writeMessage(command);
    }

    @Override
    protected void detailData(String data, String gateWay) {
        super.detailData(data, gateWay);
        if (data.length() <= 8) {
            return;
        }
        String command = data.substring(6, 10);
        if (command.equals("0010")) {
            String stateStr = data.substring(14, 16);
            int chazuoState = Integer.parseInt(stateStr);
            handler.sendEmptyMessage(chazuoState);
        } else if (command.equals("0002")) {
            int position = Integer.parseInt(data.substring(14, 16));
            int switchState = Integer.parseInt(data.substring(16, 18));
            Message message = new Message();
            message.what = 8;
            message.arg1 = position;
            message.arg2 = switchState;
            handler.sendMessage(message);
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    switch1.setChecked(true);
                    switch2.setChecked(true);
                    switch3.setChecked(true);
                    break;
                case 1:
                    switch1.setChecked(false);
                    switch2.setChecked(true);
                    switch3.setChecked(true);
                    break;
                case 2:
                    switch1.setChecked(true);
                    switch2.setChecked(false);
                    switch3.setChecked(true);
                    break;
                case 3:
                    switch1.setChecked(false);
                    switch2.setChecked(false);
                    switch3.setChecked(true);
                    break;
                case 4:
                    switch1.setChecked(true);
                    switch2.setChecked(true);
                    switch3.setChecked(false);
                    break;
                case 5:
                    switch1.setChecked(false);
                    switch2.setChecked(true);
                    switch3.setChecked(false);
                    break;
                case 6:
                    switch1.setChecked(true);
                    switch2.setChecked(false);
                    switch3.setChecked(false);
                    break;
                case 7:
                    switch1.setChecked(false);
                    switch2.setChecked(false);
                    switch3.setChecked(false);
                    break;
                case 8:
                    int pos = msg.arg1;
                    int sws = msg.arg2;
                    if (pos == 1) {
                        if (sws == 1) {
                            switch1.setChecked(false);
                        } else if (sws == 2) {
                            switch1.setChecked(true);
                        }
                    }
                    if (pos == 2) {
                        if (sws == 1) {
                            switch2.setChecked(false);
                        } else if (sws == 2) {
                            switch2.setChecked(true);
                        }
                    }
                    if (pos == 4) {
                        if (sws == 1) {
                            switch3.setChecked(false);
                        } else if (sws == 2) {
                            switch3.setChecked(true);
                        }
                    }
                    break;
            }
            return false;
        }
    });
}
