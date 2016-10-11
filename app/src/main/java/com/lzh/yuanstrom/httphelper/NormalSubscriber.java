package com.lzh.yuanstrom.httphelper;

import rx.Subscriber;

import android.util.Log;

/**
 * Created by Administrator on 2016/10/11.
 */

public class NormalSubscriber<T> extends Subscriber<T> implements SubscriberListener<T> {

    private SubscriberListener<T> mySubscriberListener;

    public NormalSubscriber(SubscriberListener<T> mySubscriberListener) {
        this.mySubscriberListener = mySubscriberListener;
    }

    @Override
    public void onCompleted() {
        Log.e("mission","mission complete");
        unBind();
    }

    @Override
    public void onError(Throwable e) {
        Log.e("mission","mission error");
        unBind();
    }

    @Override
    public void onNext(T t) {
        if (mySubscriberListener != null) {
            mySubscriberListener.onNext(t);
        }
    }

    private void unBind() {
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }
    }
}
