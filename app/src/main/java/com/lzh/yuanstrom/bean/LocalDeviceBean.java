package com.lzh.yuanstrom.bean;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lzh.yuanstrom.sql.SqliteHelper;

import java.util.ArrayList;
import java.util.List;

import me.hekr.hekrsdk.bean.DeviceBean;

/**
 * Created by Administrator on 2016/10/24.
 */

public class LocalDeviceBean {

    public String devTid;

    public String ctrlKey;

    public String bindKey;

    public String cid;

    public String workModeType;

    public int tokenType;

    public String binVersion;

    public String sdkVer;

    public String binType;

    public int servicePort;

    public String ssid;

    public String mac;

    public String finger;

    public String ownerUid;

    public int devShareNum;

    public String deviceName;

    public String desc;

    public String folderId;

    public String productPublicKey;

    public String logo;

    public boolean granted;

    public boolean setSchedulerTask;

    public boolean online;

    public String model;

    public String cidName;

    public String folderName;

    public String categoryName;

    public String productName;

    public boolean forceBind;

    public int maxDevShareNum;

    public boolean saveNew() {
        List<LocalDeviceBean> devs = LocalDeviceBean.findALll();
        for (LocalDeviceBean dev : devs) {
            if (dev.devTid.equals(devTid)) {
                return update();//tid相同就代表是同一设备
            }
        }
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        ContentValues values = new ContentValues();
        values.put("devTid", devTid);
        values.put("ctrlKey", ctrlKey);
        values.put("bindKey", bindKey);
        values.put("cid", cid);
        values.put("workModeType", workModeType);
        values.put("tokenType", tokenType);
        values.put("binVersion", binVersion);
        values.put("sdkVer", sdkVer);
        values.put("binType", binType);
        values.put("servicePort", servicePort);
        values.put("ssid", ssid);
        values.put("mac", mac);
        values.put("finger", finger);
        values.put("ownerUid", ownerUid);

        values.put("devShareNum", devShareNum);
        values.put("deviceName", deviceName);
        values.put("desc", desc);
        values.put("folderId", folderId);
        values.put("productPublicKey", productPublicKey);
        values.put("logo", logo);
        values.put("granted", granted ? 1 : 0);
        values.put("setSchedulerTask", setSchedulerTask ? 1 : 0);
        values.put("online", online ? 1 : 0);
        values.put("model", model);
        values.put("cidName", cidName);
        values.put("folderName", folderName);
        values.put("categoryName", categoryName);
        values.put("productName", productName);
        values.put("forceBind", forceBind ? 1:0);
        values.put("maxDevShareNum", maxDevShareNum);
        boolean flag = db.insert("t_dev_info", null, values) != -1;
        return flag;
    }

    public boolean update() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        ContentValues values = new ContentValues();
        values.put("devTid", devTid);
        values.put("ctrlKey", ctrlKey);
        values.put("bindKey", bindKey);
        values.put("cid", cid);
        values.put("workModeType", workModeType);
        values.put("tokenType", tokenType);
        values.put("binVersion", binVersion);
        values.put("sdkVer", sdkVer);
        values.put("binType", binType);
        values.put("servicePort", servicePort);
        values.put("ssid", ssid);
        values.put("mac", mac);
        values.put("finger", finger);
        values.put("ownerUid", ownerUid);

        values.put("devShareNum", devShareNum);
        values.put("deviceName", deviceName);
        values.put("desc", desc);
        values.put("folderId", folderId);
        values.put("productPublicKey", productPublicKey);
        values.put("logo", logo);
        values.put("granted", granted ? 1 : 0);
        values.put("setSchedulerTask", setSchedulerTask ? 1 : 0);
        values.put("online", online ? 1 : 0);
        values.put("model", model);
        values.put("cidName", cidName);
        values.put("folderName", folderName);
        values.put("categoryName", categoryName);
        values.put("productName", productName);
        values.put("forceBind", forceBind);
        values.put("maxDevShareNum", maxDevShareNum);
        /*
         * values.put("age", age); values.put("jialing", jialing);
		 */
        boolean flag = db.update("t_dev_info", values, "devTid = ?", new String[]{String.valueOf(devTid)}) == 1;
        return flag;
    }

    public static boolean deletByTid(String devTid) {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        boolean flag = db.delete("t_dev_info", " devTid = ? ", new String[]{devTid}) > 0;
        return flag;
    }

    public static boolean deletAll() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        boolean flag = db.delete("t_dev_info", null, null) > 0;
        return flag;
    }

    public static LocalDeviceBean findByTid(String tid) {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();

        Cursor cursor = db
                .rawQuery(
                        "select * from t_dev_info where devTid = ? ",
                        new String[]{tid});
        LocalDeviceBean info = new LocalDeviceBean();
        try {
            while (cursor.moveToNext()) {
                info = cursorToDev(cursor);
            }
        } finally {
            cursor.close();
        }
        return info;
    }

    public static LocalDeviceBean cursorToDev(Cursor cursor) {

        LocalDeviceBean info = new LocalDeviceBean();
        info.devTid = cursor.getString(cursor.getColumnIndex("devTid"));
        info.ctrlKey = cursor.getString(cursor.getColumnIndex("ctrlKey"));
        info.bindKey = cursor.getString(cursor
                .getColumnIndex("bindKey"));
        info.cid = cursor.getString(cursor
                .getColumnIndex("cid"));
        info.workModeType = cursor.getString(cursor
                .getColumnIndex("workModeType"));
        info.tokenType = cursor.getInt(cursor
                .getColumnIndex("tokenType"));
        info.granted = cursor.getInt(cursor
                .getColumnIndex("granted")) == 1 ? true : false;
        info.binVersion = cursor.getString(cursor.getColumnIndex("binVersion"));
        info.sdkVer = cursor.getString(cursor.getColumnIndex("sdkVer"));
        info.binType = cursor.getString(cursor.getColumnIndex("binType"));
        info.servicePort = cursor.getInt(cursor.getColumnIndex("servicePort"));
        info.setSchedulerTask = cursor.getInt(cursor.getColumnIndex("setSchedulerTask")) == 1 ? true : false;
        info.online = cursor.getInt(cursor.getColumnIndex("online")) == 1 ? true : false;
        info.ssid = cursor.getString(cursor.getColumnIndex("ssid"));

        info.model = cursor.getString(cursor.getColumnIndex("model"));

        info.mac = cursor.getString(cursor.getColumnIndex("mac"));
        info.finger = cursor.getString(cursor.getColumnIndex("finger"));
        info.ownerUid = cursor.getString(cursor.getColumnIndex("ownerUid"));
        info.devShareNum = cursor.getInt(cursor.getColumnIndex("devShareNum"));
        info.deviceName = cursor.getString(cursor.getColumnIndex("deviceName"));
        info.desc = cursor.getString(cursor.getColumnIndex("desc"));
        info.folderId = cursor.getString(cursor.getColumnIndex("folderId"));
        info.productPublicKey = cursor.getString(cursor.getColumnIndex("productPublicKey"));
        info.logo = cursor.getString(cursor.getColumnIndex("logo"));
        info.cidName = cursor.getString(cursor.getColumnIndex("cidName"));
        info.folderName = cursor.getString(cursor.getColumnIndex("folderName"));
        info.categoryName = cursor.getString(cursor.getColumnIndex("categoryName"));
        info.productName = cursor.getString(cursor.getColumnIndex("productName"));
        info.forceBind = cursor.getInt(cursor.getColumnIndex("forceBind")) == 1 ? true : false;
        info.maxDevShareNum = cursor.getInt(cursor.getColumnIndex("maxDevShareNum"));

        return info;
    }

    public static List<LocalDeviceBean> findALll() {
        List<LocalDeviceBean> devs = new ArrayList<>();
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();

        Cursor cursor = db
                .rawQuery(
                        "select * from t_dev_info ",
                        new String[]{});
        try {
            while (cursor.moveToNext()) {
                LocalDeviceBean info = cursorToDev(cursor);
                devs.add(info);
            }
        } finally {
            cursor.close();
        }
        return devs;
    }

    /**
     * hekr devicebean转换为LocalDeviceBean
     *
     * @param device
     */
    public static LocalDeviceBean dev2Local(DeviceBean device) {
        LocalDeviceBean local = new LocalDeviceBean();
        local.devTid = device.getDevTid();
        local.ctrlKey = device.getCtrlKey();
        local.bindKey = device.getBindKey();
        local.cid = device.getCid();
        local.workModeType = device.getWorkModeType();
        local.tokenType = device.getTokenType();
        local.binVersion = device.getBinVersion();
        local.sdkVer = device.getSdkVer();
        local.binType = device.getBinType();
        local.servicePort = device.getServicePort();
        local.ssid = device.getSsid();
        local.mac = device.getMac();
        local.finger = device.getFinger();
        local.ownerUid = device.getOwnerUid();
        local.devShareNum = device.getDevShareNum();
        local.deviceName = device.getDeviceName();
        local.desc = device.getDesc();
        local.folderId = device.getFolderId();
        local.productPublicKey = device.getProductPublicKey();
        local.logo = device.getLogo();
        local.granted = device.isGranted();
        local.setSchedulerTask = device.isSetSchedulerTask();
        local.online = device.isOnline();
        local.model = device.getModel();
        local.cidName = device.getCidName();
        local.folderName = device.getFolderName();
        local.categoryName = device.getCategoryName().getZh_CN();
        local.productName = device.getProductName().getZh_CN();
        local.forceBind = device.isForceBind();
        local.maxDevShareNum = device.getMaxDevShareNum();

        return local;
    }

}
