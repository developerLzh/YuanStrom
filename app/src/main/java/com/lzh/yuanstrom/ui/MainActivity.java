package com.lzh.yuanstrom.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
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
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.igexin.sdk.PushManager;
import com.litesuits.android.log.Log;
import com.lzh.yuanstrom.MyApplication;
import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.adapter.TabPagerAdapter;
import com.lzh.yuanstrom.bean.CustomerBean;
import com.lzh.yuanstrom.bean.ExtraProperties;
import com.lzh.yuanstrom.bean.ProfileData;
import com.lzh.yuanstrom.common.GetCustomerListener;
import com.lzh.yuanstrom.service.GeTuiService;
import com.lzh.yuanstrom.service.PushCoreIntentService;
import com.lzh.yuanstrom.utils.AppManager;
import com.lzh.yuanstrom.utils.PhotoHelper;
import com.lzh.yuanstrom.utils.StringUtils;
import com.lzh.yuanstrom.utils.TimeUtil;
import com.lzh.yuanstrom.utils.ToastUtil;
import com.lzh.yuanstrom.utils.Utils;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.view.MaterialIntroView;
import me.hekr.hekrsdk.action.HekrUser;
import me.hekr.hekrsdk.action.HekrUserAction;
import me.hekr.hekrsdk.bean.FileBean;
import me.hekr.hekrsdk.bean.ProfileBean;
import me.hekr.hekrsdk.util.ConstantsUtil;

import com.lzh.yuanstrom.utils.HekrCodeUtil;

import me.hekr.hekrsdk.util.SpCache;

/**
 * Created by Vicent on 2016/10/12.
 */
public class MainActivity extends BaseActivity {

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

    private PhotoHelper photoHelper;

    private static final int CAMERA = 0x01;
    private static final int PICTURE = 0x02;
    private static final int CROP = 0x03;
    private static final int ADD_PROFILE = 0x04;


    private CustomerBean customerBean;

    public void setCustomerBean(CustomerBean customerBean) {
        this.customerBean = customerBean;
    }

    public CustomerBean getCustomerBean() {
        return customerBean;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        View drawer = navigationView.getHeaderView(0);
        cusName = (TextView) drawer.findViewById(R.id.name);
        cusAge = (TextView) drawer.findViewById(R.id.age);
        cusGender = (TextView) drawer.findViewById(R.id.gender);
        cusPhoto = (ImageView) drawer.findViewById(R.id.photo);
        uid = (TextView) drawer.findViewById(R.id.uid);

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

        initGetui();
    }

    private void initGetui() {
        PushManager.getInstance().initialize(this.getApplicationContext(), GeTuiService.class);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), PushCoreIntentService.class);
    }

    private void initHeaderImg() {
        String time = TimeUtil.getTime(TimeUtil.HM, System.currentTimeMillis());
        int hour = Integer.parseInt(time.split(":")[0]);
//        int hour = Integer.parseInt("15:20".split(":")[0]);
        if ((hour > 18 && hour < 24) || (hour >= 0 && hour <= 6)) {
            header.setBackgroundResource(R.drawable.night);
        } else {
            header.setBackgroundResource(R.drawable.day);
        }
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
                if (0 != customerBean.getAge()) {
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
                PageFragment pageFragment = mAdapter.getFragment(tabLayout.getSelectedTabPosition());
                if (null != pageFragment && pageFragment.thirdPageAdapter != null && null != bean) {
                    if (bean.profileDatas != null && bean.profileDatas.size() > 0) {
                        pageFragment.thirdPageAdapter.setDatas(bean.profileDatas);
                        pageFragment.emptyCon.setVisibility(View.GONE);
                    } else {
                        pageFragment.thirdPageAdapter.setDatas(new ArrayList<ProfileData>());
                        pageFragment.emptyCon.setVisibility(View.VISIBLE);
                        pageFragment.hintTxt.setText(getString(R.string.no_profiles));
                    }
                    isFromProfileActivity = false;
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
        uid.setText(hekrUserAction.getUserId());
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
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final RadioButton boy = new RadioButton(context);
        boy.setText(getString(R.string.boy));
        boy.setTextColor(MainActivity.this.getResources().getColor(R.color.blue_500));
        final RadioButton girl = new RadioButton(context);
        girl.setText(getString(R.string.girl));
        girl.setTextColor(MainActivity.this.getResources().getColor(R.color.pink_500));
        radioGroup.addView(boy, layoutParams);
        radioGroup.addView(girl, layoutParams);
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
                        extraProperties.profileDatas = customerBean.profileDatas;
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
                    Intent intent = new Intent(context, ConfigActivity.class);
                    startActivity(intent);
//                } else if (tabLayout.getSelectedTabPosition() == 1) {
//                    List<SimpleDeviceBean> notAdded = new ArrayList<>();
//                    List<SimpleDeviceBean> added = new ArrayList<>();
//                    for (int i = 0; i < LocalDeviceBean.findALll().size(); i++) {
//                        LocalDeviceBean local = LocalDeviceBean.findALll().get(i);
//                        SimpleDeviceBean simDean = new SimpleDeviceBean();
//                        simDean.setId(i);
//                        simDean.setDevTid(local.devTid);
//                        simDean.setCtrlKey(local.ctrlKey);
//                        simDean.setDevCate(local.categoryName);
//                        simDean.setLogo(local.logo);
//                        simDean.setDevCate(local.categoryName);
//                        notAdded.add(simDean);
//                    }
//
//                    Intent intent = new Intent(context, GroupActivity.class);
//                    intent.putExtra("notAdded", (Serializable) notAdded);
//                    intent.putExtra("added", (Serializable) added);
//                    intent.putExtra("isNewCreate", true);
//                    startActivity(intent);
                } else if (tabLayout.getSelectedTabPosition() == 1) {
                    if (isCustomerNull()) {
                        ToastUtil.showMessage(MainActivity.this, getString(R.string.no_user));
                        return;
                    }
                    Intent intent = new Intent(context, CreateProfileActivity.class);
                    startActivityForResult(intent, ADD_PROFILE);
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

    private boolean isFromProfileActivity = false;

    @Override
    protected void onResume() {
        super.onResume();
        initHeaderImg();
        PageFragment pageFragment = mAdapter.getFragment(tabLayout.getSelectedTabPosition());
        if (pageFragment != null && !isFromProfileActivity) {
            Log.e("pageNo", "--->" + pageFragment.mPage);
            pageFragment.onRefresh();
        }

        //取消通知栏消息
        Intent intent = getIntent();
        if (null != intent) {
            int notifyId = intent.getIntExtra("notifyId", 0);
            if (notifyId != 0) {
                NotificationManager mNotificationManager = (NotificationManager) MyApplication.getInstance()
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(notifyId);
            }
        }
    }

    private void initToolbar() {
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
//        super.onSaveInstanceState(outState, outPersistentState);
//    }

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

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        if (menuItem.getItemId() == R.id.change_psw) {
                            showPswDialog();
//                        } else if (menuItem.getItemId() == R.id.nav_share) {
//                            Intent intent = new Intent(Intent.ACTION_SEND);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.putExtra(Intent.EXTRA_TEXT, "Android Local Share");   //附带的说明信息
//                            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp_photo.jpg")));
//                            intent.putExtra(Intent.EXTRA_SUBJECT, "Title");
//                            intent.setType("image/*");   //分享图片
//                            startActivity(Intent.createChooser(intent, "分享"));
                        } else if (menuItem.getItemId() == R.id.about_us) {
                            startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        } else if (menuItem.getItemId() == R.id.zhuxiao) {
                            MainActivity.this.finish();
                            SpCache.clear();
                            try {
                                AppManager.getAppManager().AppExit(MainActivity.this);
                            } catch (Exception e) {
                                Log.e("exit", "exit exception");
                            }
                        } else if (menuItem.getItemId() == R.id.my_code) {
                            startActivity(new Intent(MainActivity.this, MyCodeActivity.class));
                        } else if (menuItem.getItemId() == R.id.change_language) {
                            startActivity(new Intent(MainActivity.this, ChangeLanguageActivity.class));
                        }
                        return true;
                    }
                });
    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            } else {
                exitBy2Click(); //调用双击退出函数
            }
        }
        return false;
    }

    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, getString(R.string.clcik_again_exit), Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            AppManager.getAppManager().AppExit(this);
        }
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
        if (resultCode == RESULT_OK) {
            if (requestCode == CROP) {
                if (StringUtils.isNotBlank(photoHelper.getTempPath())) {
                    uploadPhoto(photoHelper.getTempPath());
                } else if (StringUtils.isNotBlank(photoHelper.getCameraPath())) {
                    uploadPhoto(photoHelper.getCameraPath());
                }

            } else if (requestCode == ADD_PROFILE) {
                ProfileData profileData = (ProfileData) data.getSerializableExtra("profileData");
                ExtraProperties extraProperties = new ExtraProperties();
                List<ProfileData> profileDatas = customerBean.profileDatas;
                int age = customerBean.getAge();
                if (null == profileDatas) {
                    profileDatas = new ArrayList<>();
                }
                profileDatas.add(profileData);
                extraProperties.age = age;
                extraProperties.profileDatas = profileDatas;
                customerBean.setExtraProperties(extraProperties);
                uploadCustomer();
                isFromProfileActivity = true;
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
                ToastUtil.showMessage(context, getString(R.string.set_failed) + "：" + HekrCodeUtil.errorCode2Msg(context, i));
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
