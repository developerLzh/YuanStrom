package com.lzh.yuanstrom.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "YuanGeeData.db";

    private static final int VERSION = 3;

    private StringBuffer sqlBuf;

    private SQLiteDatabase sqlDatabase;

    private static SqliteHelper sqlHelpler;

    private SqliteHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        sqlBuf = new StringBuffer();

    }

    public static void init(Context context) {

        if (sqlHelpler == null) {
            sqlHelpler = new SqliteHelper(context);
        }
    }

    public static SqliteHelper getInstance() {
        if (sqlHelpler == null) {
            throw new NullPointerException(
                    "SqliteHelper init function not call");
        }
        return sqlHelpler;
    }

    public synchronized SQLiteDatabase openSqliteDatabase()
            throws SQLiteException {
        if (sqlDatabase != null) {
            if ((sqlDatabase.isDbLockedByCurrentThread())) {
                try {
                    Thread.sleep(0xa);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return sqlDatabase;
        }
        sqlDatabase = sqlHelpler.getReadableDatabase();
        return sqlDatabase;
    }

    public void onCreate(SQLiteDatabase db) {
        createCmdInfoTable(db);
        createDevInfoTable(db);
        createCustomerTable(db);
        createProfileInfoTable(db);
        createProfileCmdTable(db);
    }

    private void createProfileInfoTable(SQLiteDatabase db) {
        sqlBuf.append("CREATE TABLE ").append("t_profile_info").append(" (")
                .append("profileId").append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append("createTime").append(" ").append("LONG").append(",")
                .append("updateTime").append(" ").append("LONG").append(",")
                .append("profileName").append(" ").append("TEXT")
                .append(");");
        execCreateTableSQL(db);
    }
    private void createCustomerTable(SQLiteDatabase db) {
        sqlBuf.append("CREATE TABLE ").append("t_customer_info").append(" (")
                .append("id").append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append("birthday").append(" ").append("LONG").append(",")
                .append("firstName").append(" ").append("TEXT").append(",")
                .append("lastName").append(" ").append("TEXT").append(",")
                .append("phoneNumber").append(" ").append("TEXT").append(",")
                .append("gender").append(" ").append("TEXT").append(",")
                .append("description").append(" ").append("TEXT").append(",")
                .append("email").append(" ").append("TEXT").append(",")
                .append("photo").append(" ").append("TEXT").append(",")
                .append("age").append(" ").append("TEXT")
                .append(");");
        execCreateTableSQL(db);
    }

    private void createProfileCmdTable(SQLiteDatabase db) {
        sqlBuf.append("CREATE TABLE ").append("t_profile_cmd_info").append(" (")
                .append("id").append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append("profileId").append(" ").append("INTEGER").append(",")
                .append("command").append(" ").append("TEXT")
                .append(");");
        execCreateTableSQL(db);
    }

    private void createCmdInfoTable(SQLiteDatabase db) {
        sqlBuf.append("CREATE TABLE ").append("t_cmd_info").append(" (")
                .append("cmdId").append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append("devTid").append(" ").append("TEXT").append(",")
                .append("ctrlKey").append(" ").append("TEXT").append(",")
                .append("cmdContent").append(" ").append("TEXT").append(",")
                .append("profileId").append(" ").append("INTEGER").append(");");
        execCreateTableSQL(db);
    }

    private void createDevInfoTable(SQLiteDatabase db) {
        sqlBuf.append("CREATE TABLE ").append("t_dev_info").append(" (")
                .append("devId").append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append("devTid").append(" ").append("TEXT").append(",")

                .append("ctrlKey").append(" ").append("TEXT").append(",")
                .append("bindKey").append(" ").append("TEXT").append(",")
                .append("cid").append(" ").append("TEXT").append(",")
                .append("workModeType").append(" ").append("TEXT").append(",")
                .append("tokenType").append(" ").append("INTEGER").append(",")
                .append("binVersion").append(" ").append("TEXT").append(",")
                .append("sdkVer").append(" ").append("TEXT").append(",")
                .append("binType").append(" ").append("TEXT").append(",")
                .append("servicePort").append(" ").append("INTEGER").append(",")
                .append("ssid").append(" ").append("TEXT").append(",")
                .append("mac").append(" ").append("TEXT").append(",")
                .append("finger").append(" ").append("TEXT").append(",")
                .append("ownerUid").append(" ").append("TEXT").append(",")
                .append("devShareNum").append(" ").append("Integer").append(",")
                .append("deviceName").append(" ").append("TEXT").append(",")
                .append("desc").append(" ").append("TEXT").append(",")
                .append("folderId").append(" ").append("TEXT").append(",")
                .append("productPublicKey").append(" ").append("TEXT").append(",")
                .append("logo").append(" ").append("TEXT").append(",")
                .append("granted").append(" ").append("INTEGER").append(",")
                .append("setSchedulerTask").append(" ").append("INTEGER").append(",")
                .append("online").append(" ").append("INTEGER").append(",")
                .append("model").append(" ").append("TEXT").append(",")
                .append("cidName").append(" ").append("TEXT").append(",")
                .append("folderName").append(" ").append("TEXT").append(",")
                .append("categoryName").append(" ").append("TEXT").append(",")
                .append("productName").append(" ").append("TEXT").append(",")
                .append("forceBind").append(" ").append("TEXT").append(",")
                .append("maxDevShareNum").append(" ").append("TEXT")
                .append(");");
        execCreateTableSQL(db);
    }

    private void execCreateTableSQL(SQLiteDatabase db) {
        db.execSQL(sqlBuf.toString());
        sqlBuf.setLength(0x0);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        int current = oldVersion;

        if (current == 1) {

            db.execSQL("DROP TABLE IF EXISTS t_devinfo");

            createProfileInfoTable(db);
            createCmdInfoTable(db);

            current += 1;
        }
        if (current == 2) {

            createDevInfoTable(db);

            current += 1;

        }
    }
}