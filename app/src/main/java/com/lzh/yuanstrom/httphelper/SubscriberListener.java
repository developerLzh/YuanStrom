package com.lzh.yuanstrom.httphelper;

/**
 * Created by Vicent on 2016/9/26.
 */

public interface SubscriberListener<T> {
    void onNext(T t);
}
