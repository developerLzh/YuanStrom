package com.lzh.yuanstrom.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.bean.DeviceInfo;
import com.lzh.yuanstrom.utils.CommandHelper;
import com.lzh.yuanstrom.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vicent on 2016/10/5.
 */

public class YaoKong2Activity extends BaseDevActivity implements View.OnClickListener {


    @BindView(R.id.red_btn)
    TextView redBtn;

    @BindView(R.id.pur_btn)
    TextView purBtn;

    @BindView(R.id.green_btn)
    TextView greenBtn;

    @BindView(R.id.blue_btn)
    TextView blueBtn;

    @BindView(R.id.pink_btn)
    TextView pinkBtn;

    @BindView(R.id.yellow_btn)
    TextView yellowBtn;

    @BindView(R.id.mode_btn)
    Button modeBtn;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private boolean MODE = false;//false 代表学习模式  true 代表发送模式

    private DeviceInfo deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yaokong);
        ButterKnife.bind(this);

        deviceInfo = getIntent().getParcelableExtra("deviceInfo");

        if (deviceInfo == null) {
            ToastUtil.showMessage(this, "设备不存在");
            YaoKong2Activity.this.finish();
            return;
        }

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YaoKong2Activity.this.onBackPressed();
            }
        });

        redBtn.setOnClickListener(this);
        blueBtn.setOnClickListener(this);
        yellowBtn.setOnClickListener(this);
        purBtn.setOnClickListener(this);
        greenBtn.setOnClickListener(this);
        pinkBtn.setOnClickListener(this);

        modeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MODE) {
                    MODE = true;
                    modeBtn.setText("发送模式");
                } else {
                    MODE = false;
                    modeBtn.setText("学习模式");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
//        String action;
//        if(MODE){
//            action = "0002";
//        }else {
//            action = "0005";
//        }
        String command = "";
        switch (v.getId()) {
            case R.id.red_btn:
                //TODO 设置command,action
//                command = new CommandHelper.CommandBuilder().setFrameCommand("").setAction("").setShortAddr(deviceInfo.devShortAddr).build();
                break;
            case R.id.blue_btn:
                //TODO 设置command,action
//                command = new CommandHelper.CommandBuilder().setFrameCommand("").setAction("").setShortAddr(deviceInfo.devShortAddr).build();
                break;
            case R.id.yellow_btn:
                //TODO 设置command,action
//                command = new CommandHelper.CommandBuilder().setFrameCommand("").setAction("").setShortAddr(deviceInfo.devShortAddr).build();
                break;
            case R.id.pur_btn:
                //TODO 设置command,action
//                command = new CommandHelper.CommandBuilder().setFrameCommand("").setAction("").setShortAddr(deviceInfo.devShortAddr).build();
                break;
            case R.id.green_btn:
                //TODO 设置command,action
//                command = new CommandHelper.CommandBuilder().setFrameCommand("").setAction("").setShortAddr(deviceInfo.devShortAddr).build();
                break;
            case R.id.pink_btn:
                //TODO 设置command,action
//                command = new CommandHelper.CommandBuilder().setFrameCommand("").setAction("").setShortAddr(deviceInfo.devShortAddr).build();
                break;
        }
//        writeMessage(command);
    }
}
