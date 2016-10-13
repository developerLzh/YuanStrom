package com.lzh.yuanstrom.bean;

/**
 * Created by Vicent on 2016/10/11.
 */

public class CheckVerifyResult {
    public String phoneNumber;
    public String verifyCode;
    public String token;
    public long expireTime;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public String getToken() {
        return token;
    }

    public long getExpireTime() {
        return expireTime;
    }
}
