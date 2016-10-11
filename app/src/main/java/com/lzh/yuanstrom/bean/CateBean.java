package com.lzh.yuanstrom.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/6/19.
 */
public class CateBean {

    public String devCate;

    public int connedNo;

    public int offlinNo;

    public int onlineNo;

    public List<DeviceInfo> devs;

    public boolean gridVisible;

    public void setDevCate(String devCate) {
        this.devCate = devCate;
    }

    public void setConnedNo(int connedNo) {
        this.connedNo = connedNo;
    }

    public void setOfflinNo(int offlinNo) {
        this.offlinNo = offlinNo;
    }

    public void setOnlineNo(int onlineNo) {
        this.onlineNo = onlineNo;
    }

    public void setDevs(List<DeviceInfo> devs) {
        this.devs = devs;
    }

    public void setGridVisible(boolean gridVisible) {
        this.gridVisible = gridVisible;
    }

    @Override
    public String toString() {
        return "CateBean{" +
                "devCate='" + devCate + '\'' +
                ", connedNo=" + connedNo +
                ", offlinNo=" + offlinNo +
                ", onlineNo=" + onlineNo +
                ", devs=" + devs +
                ", gridVisible=" + gridVisible +
                '}';
    }
}
