package com.lzh.yuanstrom.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.adapter.FirstPageAdapter;
import com.lzh.yuanstrom.bean.LocalDeviceBean;

import java.util.List;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);

        ButterKnife.bind(this);

        deviceBeanList = LocalDeviceBean.findALll();

        firstPageAdapter = new FirstPageAdapter(this);
        firstPageAdapter.setOnItemClickListener(new FirstPageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(LocalDeviceBean bean, View v) {
                if (bean.isClicked) {
                    v.setBackgroundResource(R.drawable.corners_stoke_orange);
                } else {
                    v.setBackgroundColor(getResources().getColor(R.color.alpha_white));
                }
            }
        });
    }
}
