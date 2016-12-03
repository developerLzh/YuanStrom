package com.lzh.yuanstrom.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.litesuits.android.log.Log;
import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.adapter.TabPagerAdapter;
import com.lzh.yuanstrom.contract.MainContract;
import com.lzh.yuanstrom.presenter.MainPresenter;
import com.lzh.yuanstrom.utils.AppManager;
import com.lzh.yuanstrom.utils.HekrCodeUtil;
import com.lzh.yuanstrom.utils.StringUtils;
import com.lzh.yuanstrom.utils.ToastUtil;
import com.lzh.yuanstrom.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.hekr.hekrsdk.action.HekrUser;
import me.hekr.hekrsdk.util.SpCache;

/**
 * Created by Administrator on 2016/12/3.
 */

public class MMainActivity extends MBaseActivity implements MainContract.View {

    private MainContract.Presenter mPresenter;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.collapse_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.header)
    ImageView header;

    TextView uid;

    ImageView cusPhoto;

    TextView cusName;

    TextView cusAge;

    TextView cusGender;

    private TabPagerAdapter mAdapter;

    @Override
    int setLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    String setTitle() {
        return null;
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void initView() {
        //bind presenter
        this.setPresenter(new MainPresenter(hekrUserAction));
        //butterKnife
        ButterKnife.bind(this);

        initToolbar();
        initTab();
        initViewPager();
        initDrawer();
        setupDrawerContent(navigationView);
    }

    public void initToolbar() {
        collapsingToolbarLayout.setTitleEnabled(false);
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

    public void initDrawer() {
        //drawer
        View drawer = navigationView.getHeaderView(0);
        cusName = (TextView) drawer.findViewById(R.id.name);
        cusAge = (TextView) drawer.findViewById(R.id.age);
        cusGender = (TextView) drawer.findViewById(R.id.gender);
        cusPhoto = (ImageView) drawer.findViewById(R.id.photo);
        uid = (TextView) drawer.findViewById(R.id.uid);
    }

    public void initViewPager() {
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

    public void initTab() {
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

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        if (menuItem.getItemId() == R.id.change_psw) {
                            showPswDialog();
                        } else if (menuItem.getItemId() == R.id.about_us) {
                            startActivity(new Intent(MMainActivity.this, AboutUsActivity.class));
                        } else if (menuItem.getItemId() == R.id.zhuxiao) {
                            MMainActivity.this.finish();
                            SpCache.clear();
                            try {
                                AppManager.getAppManager().AppExit(MMainActivity.this);
                            } catch (Exception e) {
                                Log.e("exit", "exit exception");
                            }
                        } else if (menuItem.getItemId() == R.id.my_code) {
                            startActivity(new Intent(MMainActivity.this, MyCodeActivity.class));
                        } else if (menuItem.getItemId() == R.id.change_language) {
                            startActivity(new Intent(MMainActivity.this, ChangeLanguageActivity.class));
                        }
                        return true;
                    }
                });
    }

    @Override
    public void showPswDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.change_psw_view, null);
        final EditText editOld = (EditText) v.findViewById(R.id.edit_old);
        final EditText editNew = (EditText) v.findViewById(R.id.edit_new);
        final EditText editConfirm = (EditText) v.findViewById(R.id.confirm_new);
        final AlertDialog pswDialog = new AlertDialog.Builder(MMainActivity.this)
                .setTitle(getString(R.string.change_psw))
                .setView(v)
                .setPositiveButton(getString(R.string.ensure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String oldStr = editOld.getText().toString();
                        final String newStr = editNew.getText().toString();
                        String confirmStr = editConfirm.getText().toString();
                        if (StringUtils.isBlank(oldStr) || StringUtils.isBlank(newStr) || StringUtils.isBlank(confirmStr)) {
                            ToastUtil.showMessage(MMainActivity.this, getString(R.string.edit_all));
                            return;
                        } else {
                            if (!newStr.equals(confirmStr)) {
                                ToastUtil.showMessage(MMainActivity.this, getString(R.string.psw_not_same));
                                return;
                            } else {
                                if (newStr.length() < 6) {
                                    ToastUtil.showMessage(MMainActivity.this, getString(R.string.psw_max_than_6));
                                    return;
                                }
                                dialog.dismiss();
                                showLoading(true);
                                mPresenter.changePsw(newStr,oldStr, new HekrUser.ChangePwdListener() {
                                    @Override
                                    public void changeSuccess() {
                                        hideLoading();
                                        Intent intent = new Intent(MMainActivity.this, LoginActivity.class);
                                        intent.putExtra("tag", "login");
                                        intent.putExtra("phone", SpCache.getString("HEKR_USER_NAME", ""));
                                        intent.putExtra("password", newStr);
                                        startActivity(intent);
                                        MMainActivity.this.finish();
                                        Snackbar.make(coordinatorLayout, getString(R.string.change_psw_suc), Snackbar.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void changeFail(int i) {
                                        hideLoading();
                                        Utils.handErrorCode(i, context);
                                        Snackbar.make(coordinatorLayout, HekrCodeUtil.errorCode2Msg(context, i), Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        pswDialog.show();
    }

    /**
     * 重写屏幕参数
     */
    @Override
    public void setScreenOption() {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
    }
}
