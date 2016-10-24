package com.lzh.yuanstrom.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.litesuits.android.log.Log;
import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.adapter.TabPagerAdapter;
import com.lzh.yuanstrom.common.ExampleDataProvider;
import com.lzh.yuanstrom.utils.StringUtils;
import com.lzh.yuanstrom.utils.ToastUtil;
import com.lzh.yuanstrom.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.view.MaterialIntroView;
import me.hekr.hekrsdk.action.HekrUser;
import me.hekr.hekrsdk.bean.DeviceBean;
import me.hekr.hekrsdk.bean.TranslateBean;
import me.hekr.hekrsdk.util.HekrCodeUtil;
import me.hekr.hekrsdk.util.SpCache;

import java.util.LinkedList;

import java.util.ArrayList;

/**
 * Created by Vicent on 2016/10/12.
 */
public class MainActivity extends BaseActivity implements PageFragment.GetDevicesListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    private TabPagerAdapter mAdapter;

    private List<DeviceBean> cloundDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        cloundDevices = new LinkedList<>();

        initToolbar();
        initTab();
        initViewPager();

        setupDrawerContent(navigationView);

        ImageButton imgBtn = null;
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View v = toolbar.getChildAt(i);
            if (v instanceof ImageButton) {
                imgBtn = (ImageButton) v;
                break;
            }
        }
        showGuide(imgBtn, getString(R.string.navi_hint), new MaterialIntroListener() {
            @Override
            public void onUserClicked(String s) {
                showGuide(fab, getString(R.string.fab_hint), null, "fab_btn");
            }
        }, "nav_btn");

        fabClick();
    }

    private void fabClick() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tabLayout.getSelectedTabPosition() == 0 || tabLayout.getSelectedTabPosition() == -1) {
                    startActivity(new Intent(context, ConfigActivity.class));
                } else if (tabLayout.getSelectedTabPosition() == 1) {
                    Intent intent = new Intent(context, CreateGroupActivity.class);
                    intent.putExtra("provider", new ExampleDataProvider(cloundDevices));
                    startActivity(intent);
                }
            }
        });
    }

    private void showGuide(View v, String guideStr, MaterialIntroListener listener, String useageId) {
        new MaterialIntroView.Builder(this)
                .enableDotAnimation(true)
                .enableIcon(false)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.MINIMUM)
                .setDelayMillis(500)
                .enableFadeAnimation(true)
                .performClick(false)
                .setInfoText(guideStr)
                .setTarget(v)
                .dismissOnTouch(true)
                .setListener(listener)
                .setUsageId(useageId) //THIS SHOULD BE UNIQUE ID
                .show();
    }

    private void initViewPager() {
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        mViewPager.setAdapter(mAdapter);
    }

    private void initTab() {
        mAdapter = new TabPagerAdapter(this.getSupportFragmentManager(), this);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabsFromPagerAdapter(mAdapter);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                PageFragment pageFragment = mAdapter.getFragment(tab.getPosition());
                pageFragment.onRefresh();
                Log.e("Tab", "onTabSelected");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.e("Tab", "onTabUnselected");
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.e("Tab", "onTabReselected");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        PageFragment pageFragment = mAdapter.getFragment(tabLayout.getSelectedTabPosition());
        if (pageFragment != null) {
            Log.e("pageNo", "--->" + pageFragment.mPage);
            pageFragment.onRefresh();
        }
    }

    private void initToolbar() {
        if (toolbar != null) {
            toolbar.setTitle(getString(R.string.app_name));
            setSupportActionBar(toolbar);
        }
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            mDrawerLayout.setDrawerListener(toggle);
            toggle.syncState();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mAdapter.destroy();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gate_way, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Snackbar.make(coordinatorLayout, "Replace with your own action", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        if (menuItem.getItemId() == R.id.change_psw) {
                            showPswDialog();
                        }
                        return true;
                    }
                });
    }

    private void showPswDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.change_psw_view, null);
        final EditText editOld = (EditText) v.findViewById(R.id.edit_old);
        final EditText editNew = (EditText) v.findViewById(R.id.edit_new);
        final EditText editConfirm = (EditText) v.findViewById(R.id.confirm_new);
        final AlertDialog pswDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.change_psw))
                .setView(v)
                .setPositiveButton(getString(R.string.ensure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String oldStr = editOld.getText().toString();
                        final String newStr = editNew.getText().toString();
                        String confirmStr = editConfirm.getText().toString();
                        if (StringUtils.isBlank(oldStr) || StringUtils.isBlank(newStr) || StringUtils.isBlank(confirmStr)) {
                            ToastUtil.showMessage(MainActivity.this, getString(R.string.edit_all));
                            return;
                        } else {
                            if (!newStr.equals(confirmStr)) {
                                ToastUtil.showMessage(MainActivity.this, getString(R.string.psw_not_same));
                                return;
                            } else {
                                if (newStr.length() < 6) {
                                    ToastUtil.showMessage(MainActivity.this, getString(R.string.psw_max_than_6));
                                    return;
                                }
                                dialog.dismiss();
                                showLoading(true);
                                hekrUserAction.changePassword(newStr, oldStr, new HekrUser.ChangePwdListener() {
                                    @Override
                                    public void changeSuccess() {
                                        hideLoading();
                                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                        intent.putExtra("tag", "login");
                                        intent.putExtra("phone", SpCache.getString("HEKR_USER_NAME", ""));
                                        intent.putExtra("password", newStr);
                                        startActivity(intent);
                                        MainActivity.this.finish();
                                        Snackbar.make(coordinatorLayout, getString(R.string.change_psw_suc), Snackbar.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void changeFail(int i) {
                                        hideLoading();
                                        Utils.handErrorCode(i, context);
                                        Snackbar.make(coordinatorLayout, HekrCodeUtil.errorCode2Msg(i), Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        pswDialog.show();
    }

    @Override
    public void getDevicesSuc(List<DeviceBean> list) {
        if(null != list){
            this.cloundDevices = list;
        }
    }

    @Override
    public void getDeviceFailed() {
        cloundDevices = new LinkedList<>();
    }
}
