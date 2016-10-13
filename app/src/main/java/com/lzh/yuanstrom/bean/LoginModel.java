package com.lzh.yuanstrom.bean;

/**
 * Created by Vicent on 2016/10/11.
 */

public class LoginModel {

    private String pid;

    private String username;

    private String password;

    private String clientType;

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }
}
