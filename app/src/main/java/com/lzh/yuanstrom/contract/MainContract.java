package com.lzh.yuanstrom.contract;

import com.lzh.yuanstrom.BasePresenter;
import com.lzh.yuanstrom.BaseView;
import com.lzh.yuanstrom.common.GetCustomerListener;

import me.hekr.hekrsdk.action.HekrUser;

/**
 * Created by Administrator on 2016/12/3.
 */

public interface MainContract {
    interface View extends BaseView<Presenter>{
        void showPswDialog();
    }
    interface Presenter extends BasePresenter{
        void changePsw(String newStr, String oldStr, HekrUser.ChangePwdListener listener);
        void getCustomerInfo(GetCustomerListener listener);
        void uploadPhoto(String path);
    }
}
