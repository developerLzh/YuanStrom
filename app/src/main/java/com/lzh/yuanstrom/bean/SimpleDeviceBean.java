package com.lzh.yuanstrom.bean;

import com.lzh.yuanstrom.common.AbstractDataProvider;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/19.
 */

public class SimpleDeviceBean extends AbstractDataProvider.Data implements Serializable{

    public long id;

    public String logo;

    public String devTid;

    public String devCate;

    public String devName;

    public String ctrlKey;

    private boolean pinned;

    private int viewType;

    public SimpleDeviceBean() {

    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public boolean isSectionHeader() {
        return false;
    }

    @Override
    public int getViewType() {
        return viewType;
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    @Override
    public boolean isPinned() {
        return pinned;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

}
