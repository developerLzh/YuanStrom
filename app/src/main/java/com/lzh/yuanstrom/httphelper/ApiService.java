package com.lzh.yuanstrom.httphelper;

import com.lzh.yuanstrom.bean.CheckVerifyResult;
import com.lzh.yuanstrom.bean.LoginResult;
import com.lzh.yuanstrom.bean.ObserveBean;
import com.lzh.yuanstrom.bean.RegisterResult;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Vicent on 2016/8/20.
 */
public interface ApiService {

    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("register?type=phone")
    Observable<ObserveBean<RegisterResult>> register(@Body RequestBody requestBody);

    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("login")
    Observable<ObserveBean<LoginResult>> login(@Body RequestBody requestBody);

    @GET("sms/getVerifyCode")
    Observable<ObserveBean<Object>> getVerifyCode(
            @Query("phoneNumber") String phoneNumber,
            @Query("pid") String pid,
            @Query("type") String type
    );

    @GET("sms/checkVerifyCode")
    Observable<ObserveBean<CheckVerifyResult>> checkVerifyCode(
            @Query("phoneNumber") String phoneNumber,
            @Query("code") String code
    );
}
