/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.lzh.yuanstrom.ui;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.litesuits.android.log.Log;
import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.bean.GroupResult;
import com.lzh.yuanstrom.bean.SimpleDeviceBean;
import com.lzh.yuanstrom.bean.UploadDeviceBean;
import com.lzh.yuanstrom.common.AbstractExpandableDataProvider;
import com.lzh.yuanstrom.common.ExampleExpandableDataProvider;
import com.lzh.yuanstrom.common.ExpandableItemPinnedMessageDialogFragment;
import com.lzh.yuanstrom.utils.StringUtils;
import com.lzh.yuanstrom.utils.ToastUtil;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.hekr.hekrsdk.action.HekrUserAction;
import me.hekr.hekrsdk.util.ConstantsUtil;
import com.lzh.yuanstrom.utils.HekrCodeUtil;

public class GroupActivity extends BaseActivity implements ExpandableItemPinnedMessageDialogFragment.EventListener {
    private static final String FRAGMENT_LIST_VIEW = "list view";

    private GroupFragment myFragment;

    @OnClick(R.id.create_group)
    void create() {
        final String groupName = editGroupName.getText().toString();
        if (StringUtils.isBlank(groupName)) {
            ToastUtil.showMessage(context, getString(R.string.inout_group_name));
            return;
        }
        List<Pair<AbstractExpandableDataProvider.GroupData, List<SimpleDeviceBean>>> pair = myFragment.getMyItemAdapter().getProvider().getmData();
        for (Pair<AbstractExpandableDataProvider.GroupData, List<SimpleDeviceBean>> groupDataListPair : pair) {
            if (groupDataListPair.first.getText().equals(getString(R.string.added_list))) {
                List<SimpleDeviceBean> added = groupDataListPair.second;
                if (added.size() == 0) {
                    Snackbar.make(home, getString(R.string.at_least_one), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                final List<UploadDeviceBean> uploadDeviceBeanList = new LinkedList<>();
                for (SimpleDeviceBean simpleDeviceBean : added) {
                    UploadDeviceBean upload = new UploadDeviceBean();
                    upload.devTid = simpleDeviceBean.devTid;
                    upload.ctrlKey = simpleDeviceBean.ctrlKey;
                    uploadDeviceBeanList.add(upload);
                }

                String desc = editDesc.getText().toString();
                if (StringUtils.isBlank(desc)) {
                    desc = " ";
                }

                showLoading(true);
                if (!isNewCreate) {
                    final String finalDesc = desc;
                    deleteGroup(groupId, new DeleteGroupListener() {
                        @Override
                        public void deleteSuccess() {
                            Log.e("deleteGroup", "deleteGroup success");
                            createGroup(groupName, uploadDeviceBeanList, finalDesc, new CreateGroupListener() {
                                @Override
                                public void createSuccess(GroupResult result) {
                                    hideLoading();
                                    GroupActivity.this.finish();
                                }

                                @Override
                                public void createFailed(int errorCode) {
                                    hideLoading();
                                    Snackbar.make(home, HekrCodeUtil.errorCode2Msg(context,errorCode), Snackbar.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void deleteFailed(int errorCode) {
                            Log.e("deleteGroup", "deleteGroup deleteFailed");
                        }
                    });
                } else {
                    createGroup(groupName, uploadDeviceBeanList, desc, new CreateGroupListener() {
                        @Override
                        public void createSuccess(GroupResult result) {
                            hideLoading();
                            GroupActivity.this.finish();
                        }

                        @Override
                        public void createFailed(int errorCode) {
                            hideLoading();
                            Snackbar.make(home, HekrCodeUtil.errorCode2Msg(context,errorCode), Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.edit_group_name)
    EditText editGroupName;

    @BindView(R.id.create_group)
    Button createGroup;

    @BindView(R.id.home)
    LinearLayout home;

    @BindView(R.id.edit_group_desc)
    EditText editDesc;

    List<SimpleDeviceBean> notAdded;
    List<SimpleDeviceBean> added;

    private boolean isNewCreate;

    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        ButterKnife.bind(this);

        initData();

        setCanBackToolbar(getString(R.string.start_create_group));

        if (savedInstanceState == null) {
            myFragment = new GroupFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, myFragment, FRAGMENT_LIST_VIEW)
                    .commit();
        }
    }

    private void initData() {
        notAdded = (List<SimpleDeviceBean>) getIntent().getSerializableExtra("notAdded");
        added = (List<SimpleDeviceBean>) getIntent().getSerializableExtra("added");
        String groupName = getIntent().getStringExtra("groupName");
        String desc = getIntent().getStringExtra("desc");
        if (StringUtils.isNotBlank(groupName)) {
            editGroupName.setText(groupName);
        }
        if (StringUtils.isNotBlank(desc)) {
            editDesc.setText(desc);
        }
        isNewCreate = getIntent().getBooleanExtra("isNewCreate", true);
        groupId = getIntent().getStringExtra("groupId");
    }

    public void onGroupItemClicked(int groupPosition) {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
        AbstractExpandableDataProvider.GroupData data = getDataProvider().getGroupItem(groupPosition);

        if (data.isPinned()) {
            // unpin if tapped the pinned item
            data.setPinned(false);
            ((GroupFragment) fragment).notifyGroupItemChanged(groupPosition);
        }
    }

    public void onChildItemClicked(int groupPosition, int childPosition) {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
        SimpleDeviceBean data = getDataProvider().getChildItem(groupPosition, childPosition);

        if (data.isPinned()) {
            // unpin if tapped the pinned item
            data.setPinned(false);
            ((GroupFragment) fragment).notifyChildItemChanged(groupPosition, childPosition);
        }
    }

    @Override
    public void onNotifyExpandableItemPinnedDialogDismissed(int groupPosition, int childPosition, boolean ok) {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);

        if (childPosition == RecyclerView.NO_POSITION) {
            // group item
            getDataProvider().getGroupItem(groupPosition).setPinned(ok);
            ((GroupFragment) fragment).notifyGroupItemChanged(groupPosition);
        } else {
            // child item
            getDataProvider().getChildItem(groupPosition, childPosition).setPinned(ok);
            ((GroupFragment) fragment).notifyChildItemChanged(groupPosition, childPosition);
        }
    }

    public ExampleExpandableDataProvider getDataProvider() {
        ExampleExpandableDataProvider provider = new ExampleExpandableDataProvider(this, notAdded, added);
        return provider;
    }

    private void createGroup(String groupName, List<UploadDeviceBean> lists, String desc, final CreateGroupListener listener) {
        CharSequence url = TextUtils.concat(new CharSequence[]{ConstantsUtil.UrlUtil.BASE_USER_URL, "group"});
        JSONObject obj = new JSONObject();
        obj.put("groupName", groupName);
        obj.put("deviceList", lists);
        obj.put("desc", desc);
        hekrUserAction.postHekrData(url, obj.toString(), new HekrUserAction.GetHekrDataListener() {
            public void getSuccess(Object object) {
                listener.createSuccess(JSON.parseObject(object.toString(), GroupResult.class));
            }

            public void getFail(int errorCode) {
                listener.createFailed(errorCode);
            }
        });
    }

    public interface CreateGroupListener {

        void createSuccess(GroupResult result);

        void createFailed(int errorCode);

    }

    private void deleteGroup(String groupId, final DeleteGroupListener listener) {
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, "group?groupId=" + groupId);
        hekrUserAction.deleteHekrData(url, new HekrUserAction.GetHekrDataListener() {
            @Override
            public void getSuccess(Object o) {
                listener.deleteSuccess();
            }

            @Override
            public void getFail(int i) {
                listener.deleteFailed(i);
            }
        });
    }

    public interface DeleteGroupListener {

        void deleteSuccess();

        void deleteFailed(int errorCode);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isNewCreate) {
            getMenuInflater().inflate(R.menu.group_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.delete) {
            android.util.Log.e("Tag", "menu changed");
            deleteGroup(groupId, new DeleteGroupListener() {
                @Override
                public void deleteSuccess() {
                    ToastUtil.showMessage(GroupActivity.this, getString(R.string.delete_group_suc));
                    GroupActivity.this.finish();
                }

                @Override
                public void deleteFailed(int errorCode) {
                    ToastUtil.showMessage(GroupActivity.this, HekrCodeUtil.errorCode2Msg(context,errorCode));
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
