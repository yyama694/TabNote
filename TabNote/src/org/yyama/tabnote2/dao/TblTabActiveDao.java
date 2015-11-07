package org.yyama.tabnote2.dao;

import org.yyama.tabnote2.constant.Constant;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TblTabActiveDao {
	private static SQLiteDatabase db;

	public static int getActiveNum() {
		String sql = "SELECT active_tab_no FROM " + Constant.TABLE_NAME_ACTIVE
				+ ";";
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToNext();
		return cursor.getInt(0);
	}

	public static void setActiveNum(int i) {
		String sql = "UPDATE " + Constant.TABLE_NAME_ACTIVE
				+ " SET active_tab_no=?;";
		db.execSQL(sql, new String[] { String.valueOf(i) });
	}

	public static void init(Activity act) {
		MySQLiteOpenHelper helper = new MySQLiteOpenHelper(act);
		db = helper.getWritableDatabase();
	}

	public static void close() {
		db.close();
	}
}
