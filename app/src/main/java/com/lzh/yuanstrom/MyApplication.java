package com.lzh.yuanstrom;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.*;
import android.content.res.Configuration;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.lzh.yuanstrom.sql.SqliteHelper;
import com.lzh.yuanstrom.utils.BitmapCache;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.Locale;

import me.hekr.hekrsdk.util.HekrSDK;

/**
 * Created by Vicent on 2016/7/15.
 */
public class MyApplication extends Application {

    private static MyApplication context;
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;

    public static boolean loginSuccessed = false;

    public static ImageLoader imageLoader;

    @Override
    public void onCreate() {
        super.onCreate();
        HekrSDK.init(getApplicationContext(), R.raw.config);
//打开log,默认为false
        HekrSDK.openLog(true);
        context = this;
        SqliteHelper.init(context);
        CrashReport.initCrashReport(getApplicationContext(), "e6da4bc88b", false);
        setLanguage();
    }

    private void setLanguage() {
        boolean isFirst = getSharedPreferences().getBoolean("firstOpen", true);
        if (isFirst) {//如果第一次进入APP，保存一个默认的系统语言
            Configuration config = getResources().getConfiguration();
            setDefaultLanguage(config);
            getEditor().putBoolean("firstOpen", false).apply();
        }
        loadLanguage();
    }

    public static MyApplication getInstance() {
        return context;
    }

    public SharedPreferences getSharedPreferences() {
        if (null == sharedPreferences) {
            sharedPreferences = context.getSharedPreferences("yuanStrom", MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    public SharedPreferences.Editor getEditor() {
        if (null == editor) {
            editor = context.getSharedPreferences("yuanStrom", MODE_PRIVATE).edit();
        }
        return editor;
    }

    public boolean isLoginSuccessed() {
        return loginSuccessed;
    }

    public void setLoginSuc(boolean a) {
        loginSuccessed = a;
    }

    public ImageLoader getImageLoader() {
        if (null == imageLoader) {
            imageLoader = new ImageLoader(Volley.newRequestQueue(this), new BitmapCache());
        }
        return imageLoader;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setDefaultLanguage(newConfig);
    }

    private void setDefaultLanguage(Configuration config) {
        String language = config.locale.getLanguage();
        if (language.contains("zh")) {
            getEditor().putString("defaultLanguage", "zh").apply();
        } else if (language.contains("en")) {
            getEditor().putString("defaultLanguage", "en").apply();
        } else {
            getEditor().putString("defaultLanguage", "en").apply();
        }
        getEditor().putString("language","default").apply();
        loadLanguage();
    }

    /**
     * 加载本地设置的语言
     */
    public void loadLanguage() {
        SharedPreferences preferences = getSharedPreferences();
        String language = preferences.getString("language", "default");
        Configuration config = getResources().getConfiguration();   //获取默认配置
        if (language.equals("zh")) {
            config.locale = Locale.SIMPLIFIED_CHINESE;
        } else if (language.equals("en")) {
            config.locale = Locale.ENGLISH;
        } else {
            String defaultLag = getSharedPreferences().getString("defaultLanguage", "zh");
            if (defaultLag.equals("zh")) {
                config.locale = Locale.SIMPLIFIED_CHINESE;
            } else if (defaultLag.equals("en")) {
                config.locale = Locale.ENGLISH;
            }
        }
        getBaseContext().getResources().updateConfiguration(config, null);   //更新配置文件
    }
}
