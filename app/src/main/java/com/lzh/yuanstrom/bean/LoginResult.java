package com.lzh.yuanstrom.bean;

/**
 * Created by Vicent on 2016/10/11.
 */

public class LoginResult {
    private String access_token;

    private String refresh_token;

    private String token_type;

    private String expires_in;

    private String jti;

    public String getAccess_token() {
        return access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public String getJti() {
        return jti;
    }
}
