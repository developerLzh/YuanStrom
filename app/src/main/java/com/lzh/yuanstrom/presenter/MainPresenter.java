package com.lzh.yuanstrom.presenter;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.lzh.yuanstrom.bean.CustomerBean;
import com.lzh.yuanstrom.common.GetCustomerListener;
import com.lzh.yuanstrom.contract.MainContract;

import java.io.FileNotFoundException;

import me.hekr.hekrsdk.action.HekrUser;
import me.hekr.hekrsdk.action.HekrUserAction;
import me.hekr.hekrsdk.bean.FileBean;
import me.hekr.hekrsdk.bean.ProfileBean;
import me.hekr.hekrsdk.util.ConstantsUtil;
import me.hekr.hekrsdk.util.SpCache;

/**
 * Created by Administrator on 2016/12/3.
 */

public class MainPresenter implements MainContract.Presenter {
    private HekrUserAction hekrUserAction;
    public MainPresenter(HekrUserAction hekrUserAction) {
        this.hekrUserAction = hekrUserAction;
    }


    @Override
    public void changePsw(String newStr, String oldStr, HekrUser.ChangePwdListener listener) {
        hekrUserAction.changePassword(newStr, oldStr, listener);
    }

    @Override
    public void getCustomerInfo(final GetCustomerListener listener) {
        CharSequence url = TextUtils.concat(new CharSequence[]{ConstantsUtil.UrlUtil.BASE_USER_URL, "user/profile"});
        hekrUserAction.getHekrData(url, new HekrUserAction.GetHekrDataListener() {
            public void getSuccess(Object object) {
                ProfileBean profileBean = JSONObject.parseObject(object.toString(), ProfileBean.class);
                setUserCache(profileBean);
                CustomerBean customerBean = JSONObject.parseObject(object.toString(), CustomerBean.class);
                listener.getSuccess(customerBean);
            }

            public void getFail(int errorCode) {
                listener.getFailed(errorCode);
            }
        });
    }

    @Override
    public void uploadPhoto(String path) {
//        try {
//            hekrUserAction.uploadFile(path, new HekrUser.UploadFileListener() {
//                @Override
//                public void uploadFileSuccess(FileBean fileBean) {
//                    ProfileBean.AvatarUrl avatarUrl = new ProfileBean.AvatarUrl();
//                    avatarUrl.setSmall(fileBean.getFileSourceUrl());
//                    customerBean.setAvatarUrl(avatarUrl);
//                    uploadCustomer();
//                }
//
//                @Override
//                public void uploadFileFail(int i) {
//
//                }
//
//                @Override
//                public void uploadProgress(int i) {
//
//                }
//            });
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    public void setUserCache(ProfileBean profileBean) {
        try {
            if (!TextUtils.isEmpty(profileBean.getAvatarUrl().getSmall())) {
                SpCache.putString("avatarUrl", profileBean.getAvatarUrl().getSmall());
            }
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        if (!TextUtils.isEmpty(profileBean.getPhoneNumber())) {
            SpCache.putString("PhoneNumber", profileBean.getPhoneNumber());
        }

        if (!TextUtils.isEmpty(profileBean.getEmail())) {
            SpCache.putString("email", profileBean.getEmail());
        }

        if (!TextUtils.isEmpty(profileBean.getLastName())) {
            SpCache.putString("lastName", profileBean.getLastName());
        }

        if (!TextUtils.isEmpty(profileBean.getGender())) {
            SpCache.putString("gender", profileBean.getGender());
        }

        if (!TextUtils.isEmpty(profileBean.getAge())) {
            SpCache.putString("age", profileBean.getAge());
        }
    }

}
