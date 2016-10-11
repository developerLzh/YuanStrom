package com.lzh.yuanstrom.httphelper;

import android.content.Context;
import android.net.ParseException;
import android.util.Log;

import com.google.gson.JsonParseException;
import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.utils.ToastUtil;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 * Created by Administrator on 2016/9/26.
 */

public class InternetSubscriber<T> extends Subscriber<T> implements ProgressDismissListener {

    private Context context;

    private ProgressHandler progressHandler;

    private SubscriberListener<T> mySubscriberListener;

    public InternetSubscriber(
            Context context,
            boolean needShowProgress,
            boolean dialogCancelable,
            SubscriberListener<T> mySubscriberListener) {
        this.context = context;
        this.mySubscriberListener = mySubscriberListener;
        if (needShowProgress) {
            progressHandler = new ProgressHandler(context, dialogCancelable, this);
        }
    }

    @Override
    public void onCompleted() {
        Log.e("MySubscriber", "mission complete");

        if (null != progressHandler) {
            progressHandler.sendEmptyMessage(ProgressHandler.DISMISS_DIALOG);
        } else {
            this.onProgressDismiss();
        }

    }

    /**
     * 处理错误信息
     *
     * @param e
     */
    @Override
    public void onError(Throwable e) {
        Log.e("MySubscriber", "mission error");
        if (e instanceof HttpException) {
            ToastUtil.showMessage(context, context.getString(R.string.response_error) + ((HttpException) e).code());//400、500、404之类的响应码错误
        } else if (e instanceof SocketTimeoutException) {
            ToastUtil.showMessage(context, context.getString(R.string.out_time));//连接超时错误
        } else if (e instanceof ConnectException) {
            ToastUtil.showMessage(context, context.getString(R.string.no_internet));//连接失败错误
        } else if (e instanceof JsonParseException || e instanceof JSONException || e instanceof ParseException) {
            ToastUtil.showMessage(context, context.getString(R.string.parse_error));//解析错误
        } else {
            ToastUtil.showMessage(context, e.getMessage());//服务器定义的错误
        }

        if (null != progressHandler) {
            progressHandler.sendEmptyMessage(ProgressHandler.DISMISS_DIALOG);
        } else {
            this.onProgressDismiss();
        }

    }

    @Override
    public void onNext(T t) {
        Log.e("MySubscriber", "mission onNext");
        if (mySubscriberListener != null) {
            mySubscriberListener.onNext(t);
        }
    }

    /**
     * 在开始订阅时，如果需要显示加载框
     */
    @Override
    public void onStart() {
        super.onStart();
        if (null != progressHandler) {
            progressHandler.sendEmptyMessage(ProgressHandler.SHOW_DIALOG);
        }
    }

    /**
     * 在加载框消失时是整个流程的最后一步
     * 好像可以防止内存泄露
     */
    @Override
    public void onProgressDismiss() {
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }
    }
}
