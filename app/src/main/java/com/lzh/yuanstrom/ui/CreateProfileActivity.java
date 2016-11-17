package com.lzh.yuanstrom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.adapter.FirstPageAdapter;
import com.lzh.yuanstrom.bean.LocalDeviceBean;
import com.lzh.yuanstrom.bean.ProfileData;
import com.lzh.yuanstrom.utils.CommandHelper;
import com.lzh.yuanstrom.utils.FullCommandHelper;
import com.lzh.yuanstrom.utils.StringUtils;
import com.lzh.yuanstrom.utils.ToastUtil;
import com.lzh.yuanstrom.widget.CusBottomSheetDialog;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/10/18.
 */

public class CreateProfileActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.edit_profile_name)
    EditText editProfileName;

    @BindView(R.id.device_list)
    RecyclerView mRecyclerView;

    @BindView(R.id.create_profile)
    Button createProfile;

    @BindView(R.id.home)
    LinearLayout home;

    private FirstPageAdapter firstPageAdapter;

    private List<LocalDeviceBean> deviceBeanList;

    private List<String> cmds = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);

        ButterKnife.bind(this);

        deviceBeanList = LocalDeviceBean.findALll();
        initBar();
        initRecycler();
        initView();
    }

    private void initView() {
        createProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editedName = editProfileName.getText().toString();
                if (StringUtils.isBlank(editedName)) {
                    ToastUtil.showMessage(CreateProfileActivity.this, getString(R.string.please_edit_profile_name));
                    return;
                }
                if (cmds.size() == 0) {
                    ToastUtil.showMessage(CreateProfileActivity.this, getString(R.string.empty_cmd));
                    return;
                }
                ProfileData profileData = new ProfileData();
                profileData.profileName = editedName;
                profileData.createTime = System.currentTimeMillis();
                profileData.profileDatas = cmds;
                Intent intent = new Intent();
                intent.putExtra("profileData", profileData);
                setResult(RESULT_OK, intent);
                CreateProfileActivity.this.finish();
            }
        });
    }

    private void initBar() {
        toolbar.setTitle(getString(R.string.create_profile));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateProfileActivity.this.onBackPressed();
            }
        });
    }

    private void initRecycler() {
        firstPageAdapter = new FirstPageAdapter(this);
        firstPageAdapter.setOnItemClickListener(new FirstPageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(LocalDeviceBean bean, View v) {
                if (bean.isClicked) {
                    v.setBackgroundResource(R.drawable.corners_stoke_self);
                    if (bean.categoryName.contains(getString(R.string.chazuo))) {
                        showChazuoDialog(bean);
                    } else if (bean.categoryName.contains(getString(R.string.lamp))) {
                        showLampDialog(bean);
                    }
                } else {
                    v.setBackgroundResource(R.drawable.corners_first_item);
                    Iterator<String> iterator = cmds.iterator();
                    while (iterator.hasNext()) {
                        String s = iterator.next();
                        if (s.contains(bean.devTid)) {
                            iterator.remove();
                        }
                    }
                }
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(firstPageAdapter);
        firstPageAdapter.setDevices(LocalDeviceBean.findALll());
    }

    private void showLampDialog(LocalDeviceBean bean) {
    }

    private void showChazuoDialog(final LocalDeviceBean bean) {
        final CusBottomSheetDialog dialog = new CusBottomSheetDialog(this);
        View root = getLayoutInflater().inflate(R.layout.chazuo_bottom_layout, null);
        final RadioButton c1_Open = (RadioButton) root.findViewById(R.id.chazuo_1_open);
        final RadioButton c1_Close = (RadioButton) root.findViewById(R.id.chazuo_1_close);
        final RadioButton c2_Open = (RadioButton) root.findViewById(R.id.chazuo_2_open);
        final RadioButton c2_Close = (RadioButton) root.findViewById(R.id.chazuo_2_close);
        final RadioButton c3_Open = (RadioButton) root.findViewById(R.id.chazuo_3_open);
        final RadioButton c3_Close = (RadioButton) root.findViewById(R.id.chazuo_3_close);
        final RadioButton c4_Open = (RadioButton) root.findViewById(R.id.chazuo_4_open);
        final RadioButton c4_Close = (RadioButton) root.findViewById(R.id.chazuo_4_close);
        TextView cancel = (TextView) root.findViewById(R.id.cancel);
        TextView ensure = (TextView) root.findViewById(R.id.ensure);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!c1_Open.isChecked() && !c1_Close.isChecked() && !c2_Open.isChecked() && !c2_Close.isChecked() && !c3_Open.isChecked() && !c3_Close.isChecked() && !c4_Open.isChecked() && !c4_Close.isChecked()) {
                    ToastUtil.showMessage(CreateProfileActivity.this, getString(R.string.at_least_one_action));
                    return;
                }
                String firstCommand = CommandHelper.SWITCH_COMMAND;
                String secondCommand;
                String thirdCommand;
                if (c1_Open.isChecked() || c1_Close.isChecked()) {
                    secondCommand = "01";
                    if (c1_Open.isChecked()) {
                        thirdCommand = "01";
                    } else {
                        thirdCommand = "02";
                    }
                    CommandHelper commandHelper = new CommandHelper.CommandBuilder().setFirstCommand(firstCommand).setSecondCommand(secondCommand).setThirdCommand(thirdCommand).build();
                    FullCommandHelper fullCommandHelper = new FullCommandHelper(bean.devTid, bean.ctrlKey, commandHelper.toString());
                    cmds.add(fullCommandHelper.toString());
                }
                if (c2_Open.isChecked() || c2_Close.isChecked()) {
                    secondCommand = "02";
                    if (c2_Open.isChecked()) {
                        thirdCommand = "01";
                    } else {
                        thirdCommand = "02";
                    }
                    CommandHelper commandHelper = new CommandHelper.CommandBuilder().setFirstCommand(firstCommand).setSecondCommand(secondCommand).setThirdCommand(thirdCommand).build();
                    FullCommandHelper fullCommandHelper = new FullCommandHelper(bean.devTid, bean.ctrlKey, commandHelper.toString());
                    cmds.add(fullCommandHelper.toString());
                }
                if (c3_Open.isChecked() || c3_Close.isChecked()) {
                    secondCommand = "03";
                    if (c3_Open.isChecked()) {
                        thirdCommand = "01";
                    } else {
                        thirdCommand = "02";
                    }
                    CommandHelper commandHelper = new CommandHelper.CommandBuilder().setFirstCommand(firstCommand).setSecondCommand(secondCommand).setThirdCommand(thirdCommand).build();
                    FullCommandHelper fullCommandHelper = new FullCommandHelper(bean.devTid, bean.ctrlKey, commandHelper.toString());
                    cmds.add(fullCommandHelper.toString());
                }
                if (c4_Open.isChecked() || c4_Close.isChecked()) {
                    secondCommand = "04";
                    if (c4_Open.isChecked()) {
                        thirdCommand = "01";
                    } else {
                        thirdCommand = "02";
                    }
                    CommandHelper commandHelper = new CommandHelper.CommandBuilder().setFirstCommand(firstCommand).setSecondCommand(secondCommand).setThirdCommand(thirdCommand).build();
                    FullCommandHelper fullCommandHelper = new FullCommandHelper(bean.devTid, bean.ctrlKey, commandHelper.toString());
                    cmds.add(fullCommandHelper.toString());
                }
                dialog.dismiss();
            }
        });
        dialog.setContentView(root);
        dialog.show();
    }
}
