package com.lzh.yuanstrom.httphelper;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2016/8/20.
 */
public interface ApiService {
    /**
     * 获取设备deviceToken或者userToken
     *
     * @param type DEVICE  USER
     * @param csrftoken
     * @return
     */
    @GET("token/generate.json")
    Call<String> getDeviceAccessKey(@Query("type") String type,
                                    @Query("_csrftoken_") String csrftoken);

    @GET("device/list.json")
    Call<String> getDeviceList(
            @Header("_csrftoken_")String csrftoken);
}
