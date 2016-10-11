package com.lzh.yuanstrom.bean;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.lzh.yuanstrom.sql.SqliteHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lzh on 2016/2/24.
 */
public class DeviceInfo implements Parcelable {

    public long id;

    public String devCategory;//设备种类

    public String devName;//设备名字

    public String gateWaySerialNumber;//网关序列号   就是特么的tid   气死爹了

    public String devShortAddr;//设备段地址

    public int state;//1代表打开  2代表关闭

    public boolean isChecked;//代表是否与终端对比过

    public String hongWaiShortAddr;//代表是否加入到了红外

    public String menciShortAddr;//代表是否加入到了门窗感应器

    public int hongwaiEffect;//代表红外的作用  0或null  表示没作用   1表示靠近时打开  2表示 靠近时关闭

    public int menciEffect;//代表门磁的作用  0或null  表示没作用   1表示靠近时打开  2表示 靠近时关闭

    public String anjianShortAddr;//按键短地址

    public boolean isAddLeft;//表示是否加入左边的按键  true false
    public boolean isAddRight;//表示是否加入右边的按键  true false

    public int anjianLeftEffect;//按键作用   0或null 表示没用  1表示按按钮时打开  2表示按按钮时关闭
    public int anjianRightEffect;//按键作用   0或null 表示没用  1表示按按钮时打开  2表示按按钮时关闭

    public int rValue;
    public int gValue;
    public int bValue;

    public DeviceInfo() {

    }

    protected DeviceInfo(Parcel in) {
        id = in.readLong();
        devCategory = in.readString();
        devName = in.readString();
        gateWaySerialNumber = in.readString();
        devShortAddr = in.readString();
        state = in.readInt();
        isChecked = in.readByte() != 0;
        hongWaiShortAddr = in.readString();
        menciShortAddr = in.readString();
        hongwaiEffect = in.readInt();
        menciEffect = in.readInt();
        anjianShortAddr = in.readString();
        isAddLeft = in.readByte() != 0;
        isAddRight = in.readByte() != 0;
        anjianLeftEffect = in.readInt();
        anjianRightEffect = in.readInt();
    }

    public static final Creator<DeviceInfo> CREATOR = new Creator<DeviceInfo>() {
        @Override
        public DeviceInfo createFromParcel(Parcel in) {
            return new DeviceInfo(in);
        }

        @Override
        public DeviceInfo[] newArray(int size) {
            return new DeviceInfo[size];
        }
    };

    public static String DevInfo2Json(DeviceInfo info) {
        return "{devCategory:" + "\"" + info.devCategory + "\","
                + "id:" + "\"" + info.id + "\","
                + "devName:" + "\"" + info.devName + "\","
                + "gateWaySerialNumber:" + "\"" + info.gateWaySerialNumber + "\","
                + "devShortAddr:" + "\"" + info.devShortAddr + "\","

                + "anjianLeftEffect:" + "\"" + info.anjianLeftEffect + "\","
                + "anjianShortAddr:" + "\"" + info.anjianShortAddr + "\","
                + "anjianRightEffect:" + "\"" + info.anjianRightEffect + "\","
                + "isAddLeft:" + "\"" + info.isAddLeft + "\","
                + "isAddRight:" + "\"" + info.isAddLeft + "\","

                + "menciShortAddr:" + "\"" + info.menciShortAddr + "\","
                + "menciEffect:" + "\"" + info.menciEffect + "\","

                + "hongwaiEffect:" + "\"" + info.hongwaiEffect + "\","
                + "hongWaiShortAddr:" + "\"" + info.hongWaiShortAddr + "\"}";
    }

    public static String list2Json(List<DeviceInfo> infos) {
        String str = "";
        if (infos != null && infos.size() != 0) {
            if (infos.size() == 1) {
                str = "[" + DevInfo2Json(infos.get(0)) + "]";
            } else {
                for (int i = 0; i < infos.size(); i++) {
                    DeviceInfo dev = infos.get(i);
                    if (i == 0) {
                        str = "[" + DevInfo2Json(dev);
                    } else if (i == infos.size() - 1) {
                        str += "," + DevInfo2Json(dev) + "]";
                    } else {
                        str += "," + DevInfo2Json(dev);
                    }
                }
            }
        }
        Log.e("json", str);
        return str;
    }

    /**
     * 保存数据
     *
     * @return
     */
    public boolean saveNew() {
        List<DeviceInfo> devs = DeviceInfo.findALll();
        for (DeviceInfo dev : devs) {
            if (dev.devShortAddr.equals(devShortAddr) && dev.gateWaySerialNumber.equals(gateWaySerialNumber)) {
                return false;//如果短地址和网关序列号都一样  那么就是同一个设备  要保持设备的唯一性  云端数据有可能重复
            }
        }
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        ContentValues values = new ContentValues();
        values.put("devCategory", devCategory);
        values.put("devName", devName);
        values.put("gateWaySerialNumber", gateWaySerialNumber);
        values.put("devShortAddr", devShortAddr);
        values.put("state", state);
        values.put("isChecked", isChecked ? 1 : 0);
        values.put("hongWaiShortAddr", hongWaiShortAddr);
        values.put("menciShortAddr", menciShortAddr);
        values.put("hongwaiEffect", hongwaiEffect);
        values.put("menciEffect", menciEffect);
        values.put("anjianLeftEffect", anjianLeftEffect);
        values.put("anjianRightEffect", anjianRightEffect);
        values.put("isAddLeft", isAddLeft ? 1 : 0);
        values.put("isAddRight", isAddRight ? 1 : 0);
        values.put("anjianShortAddr", anjianShortAddr);
        /*
		 * values.put("age", age); values.put("jialing", jialing);
		 */
        boolean flag = db.insert("t_devinfo", null, values) != -1;
        return flag;
    }

    public static boolean deletByShortAddrTid(String devShortAddr, String tid) {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        boolean flag = db.delete("t_devinfo", " devShortAddr = ? and gateWaySerialNumber = ? ", new String[]{devShortAddr, tid}) > 0;
        return flag;
    }

    public static boolean deletAll() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        boolean flag = db.delete("t_devinfo", null, null) > 0;
        return flag;
    }

    public static List<DeviceInfo> findByTid(String tid) {
        List<DeviceInfo> devs = new ArrayList<DeviceInfo>();
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();

        Cursor cursor = db
                .rawQuery(
                        "select * from t_devinfo where gateWaySerialNumber = ? ",
                        new String[]{tid});
        try {
            while (cursor.moveToNext()) {
                DeviceInfo info = cursorToDev(cursor);
                devs.add(info);
            }
        } finally {
            cursor.close();
        }
        return devs;
    }

    public static List<DeviceInfo> findALll() {
        List<DeviceInfo> devs = new ArrayList<DeviceInfo>();
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();

        Cursor cursor = db
                .rawQuery(
                        "select * from t_devinfo ",
                        new String[]{});
        try {
            while (cursor.moveToNext()) {
                DeviceInfo info = cursorToDev(cursor);
                devs.add(info);
            }
        } finally {
            cursor.close();
        }
        return devs;
    }

    public boolean updateNew() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();

        ContentValues values = new ContentValues();
        values.put("key_id", id);
        values.put("devCategory", devCategory);
        values.put("devName", devName);
        values.put("gateWaySerialNumber", gateWaySerialNumber);
        values.put("devShortAddr", devShortAddr);
        values.put("state", state);
        values.put("isChecked", isChecked ? 1 : 0);
        values.put("hongWaiShortAddr", hongWaiShortAddr);
        values.put("menciShortAddr", menciShortAddr);
        values.put("hongwaiEffect", hongwaiEffect);
        values.put("menciEffect", menciEffect);
        values.put("anjianLeftEffect", anjianLeftEffect);
        values.put("anjianRightEffect", anjianRightEffect);
        values.put("isAddLeft", isAddLeft ? 1 : 0);
        values.put("isAddRight", isAddRight ? 1 : 0);
        values.put("anjianShortAddr", anjianShortAddr);

        boolean flag = db.update("t_devinfo", values, "key_id = ?", new String[]{String.valueOf(id)}) == 1;
        return flag;
    }

    public boolean updateSomething() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();

        ContentValues values = new ContentValues();
        values.put("gateWaySerialNumber", gateWaySerialNumber);
        values.put("devShortAddr", devShortAddr);
        values.put("state", state);
        values.put("isChecked", isChecked ? 1 : 0);

        boolean flag = db.update("t_devinfo", values, "key_id = ?", new String[]{String.valueOf(id)}) == 1;
        return flag;
    }

    /**
     * 根据ID查询数据
     *
     * @param id
     * @return
     */
    public static DeviceInfo findByID(Long id) {

        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();

        Cursor cursor = db.rawQuery("select * from t_devinfo where key_id = ? ", new String[]{String.valueOf(id)});

        DeviceInfo info = null;

        try {
            if (cursor.moveToNext()) {
                info = cursorToDev(cursor);
            }
        } finally {
            cursor.close();
        }
        return info;
    }

    /**
     * 根据短地址和网管序列号查询订单
     *
     * @param devShortAddr,tid
     * @return
     */
    public static DeviceInfo findByShortAddrTid(String devShortAddr, String tid) {

        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();

        Cursor cursor = db.rawQuery("select * from t_devinfo where devShortAddr = ? and gateWaySerialNumber = ?", new String[]{devShortAddr, tid});

        DeviceInfo info = null;

        try {
            if (cursor.moveToNext()) {
                info = cursorToDev(cursor);
            }
        } finally {
            cursor.close();
        }
        return info;
    }

    private static DeviceInfo cursorToDev(Cursor cursor) {

        DeviceInfo info = new DeviceInfo();
        info.id = cursor.getLong(cursor.getColumnIndex("key_id"));
        info.devCategory = cursor.getString(cursor.getColumnIndex("devCategory"));
        info.devName = cursor.getString(cursor
                .getColumnIndex("devName"));
        info.gateWaySerialNumber = cursor.getString(cursor
                .getColumnIndex("gateWaySerialNumber"));
        info.devShortAddr = cursor.getString(cursor
                .getColumnIndex("devShortAddr"));
        info.state = cursor.getInt(cursor
                .getColumnIndex("state"));
        info.isChecked = cursor.getInt(cursor
                .getColumnIndex("isChecked")) == 1 ? true : false;
        info.hongWaiShortAddr = cursor.getString(cursor.getColumnIndex("hongWaiShortAddr"));
        info.menciShortAddr = cursor.getString(cursor.getColumnIndex("menciShortAddr"));
        info.hongwaiEffect = cursor.getInt(cursor.getColumnIndex("hongwaiEffect"));
        info.menciEffect = cursor.getInt(cursor.getColumnIndex("menciEffect"));
        info.isAddLeft = cursor.getInt(cursor.getColumnIndex("isAddLeft")) == 1 ? true : false;
        info.isAddRight = cursor.getInt(cursor.getColumnIndex("isAddRight")) == 1 ? true : false;
        info.anjianLeftEffect = cursor.getInt(cursor.getColumnIndex("anjianLeftEffect"));
        info.anjianRightEffect = cursor.getInt(cursor.getColumnIndex("anjianRightEffect"));
        info.anjianShortAddr = cursor.getString(cursor.getColumnIndex("anjianShortAddr"));

        return info;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(devCategory);
        dest.writeString(devName);
        dest.writeString(gateWaySerialNumber);
        dest.writeString(devShortAddr);
        dest.writeInt(state);
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeString(hongWaiShortAddr);
        dest.writeString(menciShortAddr);
        dest.writeInt(hongwaiEffect);
        dest.writeInt(menciEffect);
        dest.writeString(anjianShortAddr);
        dest.writeByte((byte) (isAddLeft ? 1 : 0));
        dest.writeByte((byte) (isAddRight ? 1 : 0));
        dest.writeInt(anjianLeftEffect);
        dest.writeInt(anjianRightEffect);
    }
}
