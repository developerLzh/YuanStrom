package com.lzh.yuanstrom.contract;

/**
 * Created by Administrator on 2016/12/3.
 */

public interface BaseImpl {
    //        void setLayoutId(int layoutId);
    void showLoading(boolean cancelable);

    void hideLoading();

    void canBackToolbar(String title);

    void setScreenOption();
}
