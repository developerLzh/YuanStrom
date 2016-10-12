package com.lzh.yuanstrom;

import android.app.Application;
import android.content.SharedPreferences;

import com.lzh.yuanstrom.sql.SqliteHelper;
import com.lzh.yuanstrom.utils.RetrofitUtils;

import me.hekr.hekrsdk.util.HekrSDK;
import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2016/7/15.
 */
public class MyApplication extends Application {

    private static MyApplication context;
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;

    public static boolean loginSuccessed = false;

    @Override
    public void onCreate() {
        super.onCreate();
        HekrSDK.init(getApplicationContext(), R.raw.config);
//打开log,默认为false
        HekrSDK.openLog(true);
        context = this;
        SqliteHelper.init(context);
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

    public boolean isLoginSuccessed(){
        return loginSuccessed;
    }
    public void setLoginSuc(boolean a){
        loginSuccessed = a;
    }
}
