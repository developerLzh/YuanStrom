package com.lzh.yuanstrom.ui;

import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.bean.GroupResult;
import com.lzh.yuanstrom.bean.SimpleDeviceBean;
import com.lzh.yuanstrom.bean.UploadDeviceBean;
import com.lzh.yuanstrom.common.AbstractExpandableDataProvider;
import com.lzh.yuanstrom.common.ExampleDataProvider;
import com.lzh.yuanstrom.common.ExampleExpandableDataProvider;
import com.lzh.yuanstrom.utils.StringUtils;
import com.lzh.yuanstrom.utils.ToastUtil;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.hekr.hekrsdk.action.HekrUserAction;
import me.hekr.hekrsdk.util.ConstantsUtil;
import me.hekr.hekrsdk.util.HekrCodeUtil;

/**
 * Created by Administrator on 2016/10/18.
 */

public class CreateGroupActivity extends BaseActivity {

//    @BindView(R.id.toolbar)
//    Toolbar toolbar;
//
//    @BindView(R.id.edit_group_name)
//    EditText editGroupName;
//
//    @BindView(R.id.device_list)
//    RecyclerView mRecyclerView;
//
//    @BindView(R.id.create_group)
//    Button createGroup;
//
//    @BindView(R.id.home)
//    LinearLayout home;
//
//    @BindView(R.id.edit_group_desc)
//    EditText editDesc;
//
//    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;
//    private RecyclerView.Adapter mWrappedAdapter;
//
//    private ExampleExpandableDataProvider provider;
//
//    private LinearLayoutManager mLayoutManager;
//
//    private DraggableExampleItemAdapter myItemAdapter;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_group);
//        ButterKnife.bind(this);
//
////        provider = (ExampleDataProvider) getIntent().getSerializableExtra("provider");
//
//        provider = new ExampleExpandableDataProvider();
//
//        initBar();
//
//        initRecycler();
//
//        initBtn();
//    }
//
//    private void initBtn() {
//        createGroup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String groupName = editGroupName.getText().toString();
//                if (StringUtils.isBlank(groupName)) {
//                    ToastUtil.showMessage(context, getString(R.string.inout_group_name));
//                    return;
//                }
//                List<SimpleDeviceBean> simpleDevices = myItemAdapter.getmProvider().getmData();
//
//                List<SimpleDeviceBean> added = new LinkedList<>();
//
//                List<UploadDeviceBean> uploadDeviceBeanList = new LinkedList<>();
//
//                for (int i = 0; i < simpleDevices.size(); i++) {
//                    SimpleDeviceBean simpleDevice = simpleDevices.get(i);
//                    if (simpleDevice.getId() == simpleDevices.size() - 1) {
//                        if (i != simpleDevices.size() - 1) {
//                            added = simpleDevices.subList(i + 1, simpleDevices.size());
//                        }
//                    }
//                }
//
//                if(added.size() == 0){
//                    Snackbar.make(home,getString(R.string.at_least_one),Snackbar.LENGTH_SHORT).show();
//                    return;
//                }
//
//                for (SimpleDeviceBean simpleDeviceBean : added) {
//                    UploadDeviceBean upload = new UploadDeviceBean();
//                    upload.devTid = simpleDeviceBean.devTid;
//                    upload.ctrlKey = simpleDeviceBean.ctrlKey;
//                    uploadDeviceBeanList.add(upload);
//                }
//
//                String desc = editDesc.getText().toString();
//                if(StringUtils.isBlank(desc)){
//                    desc = "  ";
//                }
//
//                showLoading(true);
//                createGroup(groupName, uploadDeviceBeanList,desc , new CreateGroupListener() {
//                    @Override
//                    public void createSuccess(GroupResult result) {
//                        hideLoading();
//                        CreateGroupActivity.this.finish();
//                    }
//
//                    @Override
//                    public void createFailed(int errorCode) {
//                        hideLoading();
//                        Snackbar.make(home,HekrCodeUtil.errorCode2Msg(errorCode),Snackbar.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
//    }
//
//    private void initBar() {
//        toolbar.setTitle(getString(R.string.start_create_group));
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CreateGroupActivity.this.onBackPressed();
//            }
//        });
//    }
//
//    private void initRecycler() {
//
//        mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
//
//        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
//        mRecyclerViewDragDropManager.setDraggingItemShadowDrawable((NinePatchDrawable) ContextCompat.getDrawable(context, R.drawable.material_shadow_z3));
//
//        myItemAdapter = new DraggableExampleItemAdapter(provider, this);
//
//        mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(myItemAdapter);      // wrap for dragging
//
//        final GeneralItemAnimator animator = new RefactoredDefaultItemAnimator();
//
//        mRecyclerView.setLayoutManager(mLayoutManager);
//        mRecyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
//        mRecyclerView.setItemAnimator(animator);
//
//        // additional decorations
//        //noinspection StatementWithEmptyBody
//        if (supportsViewElevation()) {
//            // Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
//        } else {
//            mRecyclerView.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) ContextCompat.getDrawable(context, R.drawable.material_shadow_z1)));
//        }
//        mRecyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(context, R.drawable.list_divider_h), true));
//
//        mRecyclerViewDragDropManager.attachRecyclerView(mRecyclerView);
//    }
//
//    private boolean supportsViewElevation() {
//        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
//    }
//
//    @Override
//    public void onPause() {
//        mRecyclerViewDragDropManager.cancelDrag();
//        super.onPause();
//    }
//
//    @Override
//    public void onDestroy() {
//        if (mRecyclerViewDragDropManager != null) {
//            mRecyclerViewDragDropManager.release();
//            mRecyclerViewDragDropManager = null;
//        }
//
//        if (mRecyclerView != null) {
//            mRecyclerView.setItemAnimator(null);
//            mRecyclerView.setAdapter(null);
//            mRecyclerView = null;
//        }
//
//        if (mWrappedAdapter != null) {
//            WrapperAdapterUtils.releaseAll(mWrappedAdapter);
//            mWrappedAdapter = null;
//        }
//        mLayoutManager = null;
//
//        super.onDestroy();
//    }
//
//    private void createGroup(String groupName, List<UploadDeviceBean> lists, String desc, final CreateGroupListener listener) {
//        CharSequence url = TextUtils.concat(new CharSequence[]{ConstantsUtil.UrlUtil.BASE_USER_URL, "group"});
//        JSONObject obj = new JSONObject();
//        obj.put("groupName", groupName);
//        obj.put("deviceList", lists);
//        obj.put("desc", desc);
//        hekrUserAction.postHekrData(url, obj.toString(), new HekrUserAction.GetHekrDataListener() {
//            public void getSuccess(Object object) {
//                listener.createSuccess(JSON.parseObject(object.toString(), GroupResult.class));
//            }
//
//            public void getFail(int errorCode) {
//                listener.createFailed(errorCode);
//            }
//        });
//    }
//
//    public interface CreateGroupListener {
//
//        void createSuccess(GroupResult result);
//
//        void createFailed(int errorCode);
//
//    }
}
