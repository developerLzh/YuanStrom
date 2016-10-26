package com.lzh.yuanstrom.bean;

/**
 * Created by Administrator on 2016/10/24.
 */

public class FullData {
    public String devTid;
    public String ctrlKey;
    public DetailData data;

    public FullData(String devTid, String ctrlKey, DetailData data) {
        this.devTid = devTid;
        this.ctrlKey = ctrlKey;
        this.data = data;
    }
}
