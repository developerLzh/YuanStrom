package com.lzh.yuanstrom.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteHelper extends SQLiteOpenHelper {

	public static final String BLANK = " ";

	public static final String COMMA = ",";

	public static final String DB_DIR = "databases";

	public static final String DB_NAME = "data.db";

	private static final int VERSION = 1;

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
		createDevInfoTable(db);
	}

	private void createDevInfoTable(SQLiteDatabase db) {
		sqlBuf.append("CREATE TABLE ").append("t_devinfo").append(" (")
				.append("key_id").append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
				.append("devCategory").append(" ").append("TEXT").append(",")
				.append("devName").append(" ").append("TEXT").append(",")
				.append("gateWaySerialNumber").append(" ").append("TEXT").append(",")
				.append("devShortAddr").append(" ").append("TEXT").append(",")
				.append("state").append(" ").append("INTEGER").append(",")
				.append("isChecked").append(" ").append("INTEGER").append(",")
				.append("hongWaiShortAddr").append(" ").append("TEXT").append(",")
				.append("menciShortAddr").append(" ").append("TEXT").append(",")
				.append("hongwaiEffect").append(" ").append("INTEGER").append(",")

				.append("anjianShortAddr").append(" ").append("TEXT").append(",")
				.append("isAddLeft").append(" ").append("INTEGER").append(",")
				.append("isAddRight").append(" ").append("INTEGER").append(",")
				.append("anjianLeftEffect").append(" ").append("INTEGER").append(",")
				.append("anjianRightEffect").append(" ").append("INTEGER").append(",")

				.append("menciEffect").append(" ").append("INTEGER")
				.append(");");
		execCreateTableSQL(db);
	}

	private void execCreateTableSQL(SQLiteDatabase db) {
		db.execSQL(sqlBuf.toString());
		sqlBuf.setLength(0x0);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		int current = oldVersion;

//		if (current == 1) {
//
//			db.execSQL("DROP TABLE IF EXISTS t_callPhoneInfo");
//			createCallPhoneInfoTable(db);
//
//			db.execSQL("DROP TABLE IF EXISTS t_noticeInfo");
//			createNoticeInfoTable(db);
//
//			db.execSQL("DROP TABLE IF EXISTS t_orderinfo");
//			createOrderInfoTable(db);
//
//			current += 1;
//		}
	}
}