package com.lzh.yuanstrom.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.bean.DeviceInfo;
import com.lzh.yuanstrom.utils.CommandHelper;
import com.lzh.yuanstrom.utils.ToastUtil;

import net.qiujuer.genius.ui.widget.Button;
import net.qiujuer.genius.ui.widget.SeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vicent on 2016/8/17.
 */
public class LampActivity extends BaseDevActivity implements View.OnClickListener {

    @BindView(R.id.lamp_state)
    ImageView lampView;

    @BindView(R.id.red_seekbar)
    SeekBar redBar;

    @BindView(R.id.green_seekbar)
    SeekBar greenBar;

    @BindView(R.id.blue_seekbar)
    SeekBar blueBar;

    @BindView(R.id.turn_off)
    Button turnOff;

    @BindView(R.id.turn_on)
    Button turnOn;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private DeviceInfo deviceInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lamp);

        ButterKnife.bind(this);

        deviceInfo =  getIntent().getParcelableExtra("deviceInfo");

        if(deviceInfo.state == 0){
            lampView.setImageResource(R.mipmap.lamp_off);
        }else{
            lampView.setImageResource(R.mipmap.lamp_on);
        }

        getLampDetail();

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LampActivity.this.onBackPressed();
            }
        });

        setListener();
    }

    private void getLampDetail() {
        String command = new CommandHelper.CommandBuilder().setFrameCommand("0009").setShortAddr(deviceInfo.devShortAddr).build();
//        writeMessage(command);
        Log.e("command", command);
    }

    private void setListener() {
        redBar.setOnSeekBarChangeListener(new MySeekBarChangeListener(0));
        greenBar.setOnSeekBarChangeListener(new MySeekBarChangeListener(1));
        blueBar.setOnSeekBarChangeListener(new MySeekBarChangeListener(2));

        turnOn.setOnClickListener(this);
        turnOff.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.turn_on) {
            ToastUtil.showMessage(LampActivity.this, "open lamp");
            String command = new CommandHelper.CommandBuilder().setFrameCommand("02").setAction("0201").setShortAddr(deviceInfo.devShortAddr).build();
            //writeMessage(command);
            Log.e("command", command);
        } else if (v.getId() == R.id.turn_off) {
            String command = new CommandHelper.CommandBuilder().setFrameCommand("02").setAction("0200").setShortAddr(deviceInfo.devShortAddr).build();
            Log.e("command", command);
            //writeMessage(command);
        }
    }

    class MySeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        public int cate;

        public MySeekBarChangeListener(int cate) {
            this.cate = cate;//0 red 1 green 2 blue
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (cate == 0) {
                deviceInfo.rValue = seekBar.getProgress();
            } else if (cate == 1) {
                deviceInfo.gValue = seekBar.getProgress();
            } else if (cate == 2) {
                deviceInfo.bValue = seekBar.getProgress();
            }
            String rStr;
            String gStr;
            String bStr;

            if (deviceInfo.rValue == 10) {
                rStr = "0A";
            } else {
                rStr = "0" + deviceInfo.rValue;
            }

            if (deviceInfo.gValue == 10) {
                gStr = "0A";
            } else {
                gStr = "0" + deviceInfo.gValue;
            }

            if (deviceInfo.bValue == 10) {
                bStr = "0A";
            } else {
                bStr = "0" + deviceInfo.bValue;
            }
            String action = "FFFFFFFF" + rStr + gStr + bStr;
            String command = new CommandHelper.CommandBuilder().setFrameCommand("07").setAction(action).setShortAddr(deviceInfo.devShortAddr).build();
            Log.e("command", command);
            //writeMessage(command);
        }
    }

    @Override
    protected void detailData(String data) {
        super.detailData(data);
        if (data.length() <= 8) {
            return;
        }
        String command = data.substring(4, 8);
        if (command.equals("0009") || command.equals("0007")) {
            String[] useableData = data.split("FFFFFFFF");
            String redStr = useableData[1].substring(0, 2);
            if (redStr.equals("0A")) {
                deviceInfo.rValue = 10;
            } else {
                deviceInfo.rValue = Integer.parseInt(redStr);
            }
            String greenStr = useableData[1].substring(2, 4);
            if (greenStr.equals("0A")) {
                deviceInfo.gValue = 10;
            } else {
                deviceInfo.gValue = Integer.parseInt(greenStr);
            }
            String blueStr = useableData[1].substring(4, 6);
            if (blueStr.equals("0A")) {
                deviceInfo.bValue = 10;
            } else {
                deviceInfo.bValue = Integer.parseInt(blueStr);
            }
            handler.sendEmptyMessage(0);
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    redBar.setProgress(deviceInfo.rValue);
                    greenBar.setProgress(deviceInfo.gValue);
                    blueBar.setProgress(deviceInfo.bValue);
                    break;
            }
            return false;
        }
    });
}
