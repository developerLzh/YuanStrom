package com.lzh.yuanstrom.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.gson.Gson;
import com.litesuits.android.log.Log;
import com.lzh.yuanstrom.MyApplication;
import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.adapter.TabPagerAdapter;
import com.lzh.yuanstrom.bean.CustomerBean;
import com.lzh.yuanstrom.bean.ExtraProperties;
import com.lzh.yuanstrom.bean.LocalDeviceBean;
import com.lzh.yuanstrom.bean.ProfileData;
import com.lzh.yuanstrom.common.ExampleDataProvider;
import com.lzh.yuanstrom.common.GetCustomerListener;
import com.lzh.yuanstrom.utils.PhotoHelper;
import com.lzh.yuanstrom.utils.StringUtils;
import com.lzh.yuanstrom.utils.ToastUtil;
import com.lzh.yuanstrom.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.view.MaterialIntroView;
import me.hekr.hekrsdk.action.HekrUser;
import me.hekr.hekrsdk.action.HekrUserAction;
import me.hekr.hekrsdk.bean.DeviceBean;
import me.hekr.hekrsdk.bean.FileBean;
import me.hekr.hekrsdk.bean.ProfileBean;
import me.hekr.hekrsdk.bean.TranslateBean;
import me.hekr.hekrsdk.util.ConstantsUtil;
import me.hekr.hekrsdk.util.HekrCodeUtil;
import me.hekr.hekrsdk.util.SpCache;

import java.util.LinkedList;

import java.util.ArrayList;

/**
 * Created by Vicent on 2016/10/12.
 */
public class MainActivity extends BaseActivity  {

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

    ImageView cusPhoto;

    TextView cusName;

    TextView cusAge;

    TextView cusGender;

    private TabPagerAdapter mAdapter;

    private PhotoHelper photoHelper;

    private static final int CAMERA = 0x01;
    private static final int PICTURE = 0x02;
    private static final int CROP = 0x03;

    private CustomerBean customerBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        View drawer = navigationView.getHeaderView(0);
        cusName = (TextView) drawer.findViewById(R.id.name);
        cusAge = (TextView) drawer.findViewById(R.id.age);
        cusGender = (TextView) drawer.findViewById(R.id.gender);
        cusPhoto = (ImageView) drawer.findViewById(R.id.photo);

        initToolbar();
        initTab();
        initViewPager();
        initDrawer();

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

        getCustomerInfo();
    }

    private void getCustomerInfo() {
        getCustomerInfo(new GetCustomerListener() {
            @Override
            public void getSuccess(CustomerBean bean) {
                customerBean = bean;
                if (StringUtils.isNotBlank(bean.getLastName())) {
                    cusName.setText(bean.getLastName());
                } else {
                    cusName.setText(getString(R.string.set_name));
                }
                if ( 0 != customerBean.getAge()) {
                    cusAge.setText("  " + customerBean.getAge() + getString(R.string.years_old));
                } else {
                    cusAge.setText(getString(R.string.set_age));
                }
                if (StringUtils.isNotBlank(bean.getGender())) {
                    if (customerBean.getGender().equals("MAN")) {
                        cusGender.setText("  " + getString(R.string.boy));
                    } else if (customerBean.getGender().equals("WOMAN")) {
                        cusGender.setText("  " + getString(R.string.girl));
                    } else {
                        cusGender.setText(getString(R.string.set_gender));
                    }
                } else {
                    cusGender.setText(getString(R.string.set_gender));
                }
                if (null != bean.getAvatarUrl()) {
                    MyApplication.getInstance().getImageLoader().get(bean.getAvatarUrl().getSmall(), new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer response, boolean b) {
                            Bitmap bitmap = response.getBitmap();
                            if (null != bitmap) {
                                cusPhoto.setImageBitmap(Utils.toRoundBitmap(bitmap));
                            } else {
                                cusPhoto.setImageResource(R.mipmap.personal_center);
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            cusPhoto.setImageResource(R.mipmap.personal_center);
                        }
                    });
                } else {
                    cusPhoto.setImageResource(R.mipmap.personal_center);
                }
            }

            @Override
            public void getFailed(int errCode) {

            }
        });
    }

    private void initDrawer() {
        photoHelper = new PhotoHelper(this, CAMERA, PICTURE, CROP);
        cusPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCustomerNull()) {
                    ToastUtil.showMessage(MainActivity.this, getString(R.string.no_user));
                    return;
                }
                photoHelper.choosePic(getString(R.string.choice_photo));
            }
        });
        cusAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCustomerNull()) {
                    ToastUtil.showMessage(MainActivity.this, getString(R.string.no_user));
                    return;
                }
                showNumberPicker();
            }
        });
        cusGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCustomerNull()) {
                    ToastUtil.showMessage(MainActivity.this, getString(R.string.no_user));
                    return;
                }
                showGenderDialog();
            }
        });
        cusName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCustomerNull()) {
                    ToastUtil.showMessage(MainActivity.this, getString(R.string.no_user));
                    return;
                }
                showNameDialog();
            }
        });
    }

    private void showNameDialog() {
        final EditText editName = new EditText(this);
        if (StringUtils.isNotBlank(customerBean.getLastName())) {
            editName.setText(customerBean.getLastName());
        }
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setView(editName)
                .setTitle(getString(R.string.set_name))
                .setPositiveButton(getString(R.string.ensure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (StringUtils.isNotBlank(editName.getText().toString())) {
                            dialogInterface.dismiss();
                            customerBean.setLastName(editName.getText().toString());
                            uploadCustomer();
                        } else {
                            ToastUtil.showMessage(MainActivity.this, getString(R.string.please_name));
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();
        dialog.show();
    }

    private void showGenderDialog() {
        RadioGroup radioGroup = new RadioGroup(MainActivity.this);
        final RadioButton boy = new RadioButton(context);
        boy.setText(getString(R.string.boy));
        boy.setTextColor(MainActivity.this.getResources().getColor(R.color.blue_500));
        final RadioButton girl = new RadioButton(context);
        girl.setText(getString(R.string.girl));
        girl.setTextColor(MainActivity.this.getResources().getColor(R.color.pink_500));
        radioGroup.addView(boy);
        radioGroup.addView(girl);
        radioGroup.setPadding(100, 50, 100, 50);
        if (StringUtils.isNotBlank(customerBean.getGender())) {
            if (customerBean.getGender().equals("MAN")) {
                boy.setChecked(true);
            } else if (customerBean.getGender().equals("WOMAN")) {
                girl.setChecked(true);
            } else {

            }
        }
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setView(radioGroup)
                .setTitle(getString(R.string.set_gender))
                .setPositiveButton(getString(R.string.ensure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String gender = "";
                        if (boy.isChecked()) {
                            gender = "MAN";
                        } else if (girl.isChecked()) {
                            gender = "WOMAN";
                        }
                        if (StringUtils.isBlank(gender)) {
                            ToastUtil.showMessage(MainActivity.this, getString(R.string.please_gender));
                            return;
                        } else {
                            dialogInterface.dismiss();
                            customerBean.setGender(gender);
                            uploadCustomer();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();
        dialog.show();
    }

    private void showNumberPicker() {
        final NumberPicker numberPicker = new NumberPicker(MainActivity.this);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(100);
        if (0 != customerBean.getAge()) {
            numberPicker.setValue(customerBean.getAge());
        } else {
            numberPicker.setValue(23);
        }
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setView(numberPicker)
                .setTitle(getString(R.string.set_age))
                .setPositiveButton(getString(R.string.ensure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        int pickNo = numberPicker.getValue();
                        ExtraProperties extraProperties = new ExtraProperties();
                        extraProperties.age = pickNo;
                        customerBean.setExtraProperties(extraProperties);
                        uploadCustomer();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();
        dialog.show();
    }

    private boolean isCustomerNull() {
        if (customerBean == null) {
            return true;
        } else {
            return false;
        }
    }

    private void fabClick() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tabLayout.getSelectedTabPosition() == 0 || tabLayout.getSelectedTabPosition() == -1) {
                    startActivity(new Intent(context, ConfigActivity.class));
                } else if (tabLayout.getSelectedTabPosition() == 1) {
                    Intent intent = new Intent(context, CreateGroupActivity.class);
                    intent.putExtra("provider", new ExampleDataProvider(LocalDeviceBean.findALll()));
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

    public void getCustomerInfo(final GetCustomerListener listener) {
        CharSequence url = TextUtils.concat(new CharSequence[]{ConstantsUtil.UrlUtil.BASE_USER_URL, "user/profile"});
        hekrUserAction.getHekrData(url, new HekrUserAction.GetHekrDataListener() {
            public void getSuccess(Object object) {
                ProfileBean profileBean = JSONObject.parseObject(object.toString(), ProfileBean.class);
                setUserCache(profileBean);
                CustomerBean customerBean = JSONObject.parseObject(object.toString(), CustomerBean.class);
                listener.getSuccess(customerBean);
            }

            public void getFail(int errorCode) {
                listener.getFailed(errorCode);
            }
        });
    }

    public void setUserCache(ProfileBean profileBean) {
        try {
            if (!TextUtils.isEmpty(profileBean.getAvatarUrl().getSmall())) {
                SpCache.putString("avatarUrl", profileBean.getAvatarUrl().getSmall());
            }
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        if (!TextUtils.isEmpty(profileBean.getPhoneNumber())) {
            SpCache.putString("PhoneNumber", profileBean.getPhoneNumber());
        }

        if (!TextUtils.isEmpty(profileBean.getEmail())) {
            SpCache.putString("email", profileBean.getEmail());
        }

        if (!TextUtils.isEmpty(profileBean.getLastName())) {
            SpCache.putString("lastName", profileBean.getLastName());
        }

        if (!TextUtils.isEmpty(profileBean.getGender())) {
            SpCache.putString("gender", profileBean.getGender());
        }

        if (!TextUtils.isEmpty(profileBean.getAge())) {
            SpCache.putString("age", profileBean.getAge());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        photoHelper.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CROP) {
            if (data == null) {
                return;
            }
            if (StringUtils.isNotBlank(photoHelper.getFileUri().toString())) {
                uploadPhoto(photoHelper.getTempPath());
            } else if (StringUtils.isNotBlank(photoHelper.getCameraPath())) {
                uploadPhoto(photoHelper.getCameraPath());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void uploadCustomer() {
        String str = new Gson().toJson(customerBean);
        JSONObject json = JSON.parseObject(str);
        showLoading(true);
        hekrUserAction.setProfile(json, new HekrUser.SetProfileListener() {
            @Override
            public void setProfileSuccess() {
                hideLoading();
                photoHelper.deleteTemp(photoHelper.getTempPath());
                photoHelper.deleteTemp(photoHelper.getCameraPath());
                photoHelper.setCameraPath("");
                photoHelper.setTempPath("");
                getCustomerInfo();
                ToastUtil.showMessage(context, getString(R.string.set_suc));
            }

            @Override
            public void setProfileFail(int i) {
                hideLoading();
                photoHelper.deleteTemp(photoHelper.getTempPath());
                photoHelper.deleteTemp(photoHelper.getCameraPath());
                photoHelper.setCameraPath("");
                photoHelper.setTempPath("");
                ToastUtil.showMessage(context, getString(R.string.set_failed) + "：" + HekrCodeUtil.errorCode2Msg(i));
            }
        });
    }

    public void uploadPhoto(String uri) {
        try {
            hekrUserAction.uploadFile(uri, new HekrUser.UploadFileListener() {
                @Override
                public void uploadFileSuccess(FileBean fileBean) {
                    ProfileBean.AvatarUrl avatarUrl = new ProfileBean.AvatarUrl();
                    avatarUrl.setSmall(fileBean.getFileSourceUrl());
                    customerBean.setAvatarUrl(avatarUrl);
                    uploadCustomer();
                }

                @Override
                public void uploadFileFail(int i) {

                }

                @Override
                public void uploadProgress(int i) {

                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
