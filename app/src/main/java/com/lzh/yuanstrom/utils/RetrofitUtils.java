package com.lzh.yuanstrom.utils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/4/27.
 *
 */
public class RetrofitUtils {

    public static final String BASE_URL = "http://user.hekr.me/";

    //创建okHttpClient客户端，并设置相关配置
//    private static OkHttpClient mOkHttpClient = genericClient();

//    private static Retrofit retrofit  = new Retrofit.Builder()
//            .baseUrl(BASE_URL).client(MyApplication.getInstance().getOkHttpClient())
//            .addConverterFactory(GsonConverterFactory.create()) //Gson转化
//            .build();
//
//    public static  <T> T createApi(Class<T> cls){
//       return retrofit.create(cls);
//    }

    public static String getRandomString(int length) { //length表示生成字符串的长度
        return "abcd";
    }

    public static String getHeader(String seed){
        return "u=" + MySettingsHelper.getCookieUser() + ";_csrftoken_=" + seed;
    }

    public static String getSeed(){
        return getRandomString(4);
    }

    public static OkHttpClient genericClient() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Interceptor.Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("Accept","application/json")
                                .addHeader("Content-Type","application/json")
                                .build();
                        return chain.proceed(request);
                    }

                })
                .connectTimeout(20, TimeUnit.SECONDS)
                .build();

        return httpClient;
    }
}
