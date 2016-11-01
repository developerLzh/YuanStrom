package com.lzh.yuanstrom.common;

import com.lzh.yuanstrom.bean.CustomerBean;

/**
 * Created by Administrator on 2016/10/31.
 */

public interface GetCustomerListener {
    void getSuccess(CustomerBean bean);
    void getFailed( int errCode);
}
