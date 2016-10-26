package com.lzh.yuanstrom.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.adapter.FirstPageAdapter;
import com.lzh.yuanstrom.bean.LocalDeviceBean;
import com.lzh.yuanstrom.utils.StringUtils;
import com.lzh.yuanstrom.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/10/25.
 */

public class DevControlActivity extends BaseActivity {

    @BindView(R.id.dev_cate)
    TextView devCate;

    @BindView(R.id.current_wifi)
    TextView currentWifi;

    @BindView(R.id.to_control)
    Button toControl;

    @BindView(R.id.delete_dev)
    Button deleteDev;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.dev_name)
    TextView devName;

    private String devTid;

    private LocalDeviceBean local;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_set);
        ButterKnife.bind(this);

        initBar();

        devTid = getIntent().getStringExtra("devTid");
        local = LocalDeviceBean.findByTid(devTid);
        if(StringUtils.isBlank(local.devTid)){
            ToastUtil.showMessage(this,context.getString(R.string.dev_not_exist));
            finish();
            return;
        }
        initView();
    }

    private void initView() {
        devCate.setText(local.categoryName);
        devName.setText(local.deviceName +" ");
        currentWifi.setText("");
        toControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirstPageAdapter.toWhatActivityByCateName(context,local.categoryName,devTid);
            }
        });
        deleteDev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSureDialog();
            }
        });
        devName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeNameDialog(local.deviceName);
            }
        });
    }

    private void initBar() {
        toolbar.setTitle(getString(R.string.start_create_group));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DevControlActivity.this.onBackPressed();
            }
        });
    }

    private void showSureDialog() {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.hint))
                .setMessage(context.getString(R.string.sure_delete))
                .setPositiveButton(context.getString(R.string.ensure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO delete device
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }
    private void showChangeNameDialog(String devName){
        final EditText edit = new EditText(this);
        edit.setText(devName);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.change_dev))
                .setView(edit)
                .setPositiveButton(getString(R.string.ensure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String s = edit.getText().toString();
                        if(StringUtils.isNotBlank(s)){
                            //TODO change devName
                            dialog.dismiss();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }
}
