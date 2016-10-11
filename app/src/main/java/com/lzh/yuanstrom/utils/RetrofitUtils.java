package com.lzh.yuanstrom.utils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2016/4/27.
 *
 */
public class RetrofitUtils {

    public static String HEKR_OLD = "http://user.hekr.me/";

    public static String BASE_URL = "http://user.hekr.me/";

    public static String HEKR_NEW = "http://uaa.openapi.hekr.me/";

    public static String getRandomString(int length) { //length表示生成字符串的长度
        return "abcd";
    }

    public static String getHeader(String seed){
        return "u=" + MySettingsHelper.getCookieUser() + ";_csrftoken_=" + seed;
    }

    public static String getSeed(){
        return getRandomString(4);
    }

    //创建okHttpClient客户端，并设置相关配置
    private static OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .build();

    private static Retrofit retrofit;

    public static <T>T createRxApi(Class<T> cls,String baseUrl){
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl).client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create()) //Gson转化
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit.create(cls);
    }
}
