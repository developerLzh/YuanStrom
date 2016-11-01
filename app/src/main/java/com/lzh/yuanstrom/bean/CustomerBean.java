package com.lzh.yuanstrom.bean;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lzh.yuanstrom.sql.SqliteHelper;

import java.io.Serializable;

import java.util.List;

import me.hekr.hekrsdk.bean.ProfileBean;

/**
 * Created by Administrator on 2016/10/31.
 */

public class CustomerBean implements Serializable {
    private long birthday;
    private String firstName;
    private String lastName;
    private long updateDate;
    private String phoneNumber;
    private String gender;
    private ProfileBean.AvatarUrl avatarUrl;
    private String description;
    private String email;

    private int age;

    private ExtraProperties extraProperties;

    public List<ProfileData> profileDatas;

    public void setProfileDatas(List<ProfileData> profileDatas) {
        this.profileDatas = profileDatas;
    }

    public List<ProfileData> getProfileDatas() {
        return profileDatas;
    }

    public void setExtraProperties(ExtraProperties extraProperties) {
        this.extraProperties = extraProperties;
    }

    public ExtraProperties getExtraProperties() {
        return extraProperties;
    }

    public CustomerBean() {
    }

    public long getBirthday() {
        return this.birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public long getUpdateDate() {
        return this.updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public ProfileBean.AvatarUrl getAvatarUrl() {
        return this.avatarUrl;
    }

    public void setAvatarUrl(ProfileBean.AvatarUrl avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean saveNew() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        ContentValues values = new ContentValues();
        values.put("birthday", birthday);
        values.put("firstName", firstName);
        values.put("lastName", lastName);
        values.put("phoneNumber", phoneNumber);
        values.put("gender", gender);
        values.put("description", description);
        values.put("email", email);
        values.put("photo", avatarUrl.getSmall());
        values.put("age", age);
        boolean flag = db.insert("t_customer_info", null, values) != -1;
        return flag;
    }

    public static boolean deletAll() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        boolean flag = db.delete("t_customer_info", null, null) > 0;
        return flag;
    }

    public boolean updateNew() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        ContentValues values = new ContentValues();
        values.put("birthday", birthday);
        values.put("firstName", firstName);
        values.put("lastName", lastName);
        values.put("phoneNumber", phoneNumber);
        values.put("gender", gender);
        values.put("description", description);
        values.put("email", email);
        values.put("photo", avatarUrl.getSmall());
        values.put("age", age);

        boolean flag = db.update("t_customer_info", values, "devTid = ?", new String[]{String.valueOf(phoneNumber)}) == 1;
        return flag;
    }

    public static CustomerBean findOne() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();

        Cursor cursor = db
                .rawQuery(
                        "select * from t_customer_info where devTid = ? ",
                        new String[]{});
        CustomerBean customerBean = new CustomerBean();
        try {
            while (cursor.moveToNext()) {
                customerBean = cursorToDev(cursor);
            }
        } finally {
            cursor.close();
        }
        return customerBean;
    }

    public static CustomerBean cursorToDev(Cursor cursor) {

        CustomerBean customerBean = new CustomerBean();
        customerBean.birthday = cursor.getLong(cursor.getColumnIndex("birthday"));
        customerBean.firstName = cursor.getString(cursor.getColumnIndex("firstName"));
        customerBean.lastName = cursor.getString(cursor.getColumnIndex("lastName"));
        customerBean.phoneNumber = cursor.getString(cursor.getColumnIndex("phoneNumber"));
        customerBean.gender = cursor.getString(cursor.getColumnIndex("gender"));
        customerBean.description = cursor.getString(cursor.getColumnIndex("description"));
        customerBean.email = cursor.getString(cursor.getColumnIndex("email"));
        ProfileBean.AvatarUrl avatarUrl = new ProfileBean.AvatarUrl();
        avatarUrl.setSmall(cursor.getString(cursor.getColumnIndex("photo")));
        customerBean.avatarUrl = avatarUrl;
        customerBean.age = cursor.getInt(cursor.getColumnIndex("age"));


        return customerBean;
    }

}
