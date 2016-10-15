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

import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.adapter.FirstPageAdapter;
import com.lzh.yuanstrom.adapter.TabPagerAdapter;
import com.lzh.yuanstrom.utils.StringUtils;
import com.lzh.yuanstrom.utils.ToastUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.hekr.hekrsdk.action.HekrUser;
import me.hekr.hekrsdk.bean.DeviceBean;
import me.hekr.hekrsdk.util.HekrCodeUtil;
import me.hekr.hekrsdk.util.SpCache;

/**
 * Created by Vicent on 2016/10/12.
 */
public class MainActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        initToolbar();
        initTab();
        initViewPager();

        setupDrawerContent(navigationView);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ConfigActivity.class));
            }
        });
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
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
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
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    protected void onStop() {
        appBarLayout.removeOnOffsetChangedListener(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mAdapter.destroy();
        super.onDestroy();
    }

    int index = 0;

    private void getDevices() {
        hekrUserAction.getDevices(new HekrUser.GetDevicesListener() {
            @Override
            public void getDevicesSuccess(List<DeviceBean> list) {

            }

            @Override
            public void getDevicesFail(int i) {

            }
        });
    }

    /**
     * @param appBarLayout
     * @param i            barLayout偏移量
     */
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        index = i;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                PageFragment pageFragment = mAdapter.getFragment(mViewPager.getCurrentItem());
                if (pageFragment != null) {
                    if (index == 0) {
                        pageFragment.setSwipeToRefreshEnabled(true);
                    } else {
                        pageFragment.setSwipeToRefreshEnabled(false);
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
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
                            Snackbar.make(coordinatorLayout, "change_psw", Snackbar.LENGTH_SHORT).show();
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
                                dialog.dismiss();
                                hekrUserAction.changePassword(newStr, oldStr, new HekrUser.ChangePwdListener() {
                                    @Override
                                    public void changeSuccess() {
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
}
