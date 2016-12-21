package com.lzh.yuanstrom.httphelper;

/**
 * Created by Vicent on 2016/9/26.
 */


/**
 * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
 *
 * @param <T> Subscriber真正需要的数据类型，也就是Data部分的数据类型
 */
//public class HttpResultFunc<T> implements Func1<ObserveBean<T>, T> {
//    public HttpResultFunc() {
//    }
//
//    @Override
//    public T call(ObserveBean<T> httpResult) {
//        if (httpResult.getCode() != 0) {
//            throw new ApiException(httpResult.getCode(),httpResult.getDesc());
//        }
//        return httpResult.getT();
//    }
//}
public class HttpResultFunc<T>{

}
