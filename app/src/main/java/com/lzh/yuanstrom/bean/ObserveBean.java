package com.lzh.yuanstrom.bean;

/**
 * Created by Administrator on 2016/10/11.
 */

public class ObserveBean<T> {

    public T t;

    public long code = 0;

    public String desc = "";

    public long timestamp = 0;

    public long getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public T getT() {
        return t;
    }
}
