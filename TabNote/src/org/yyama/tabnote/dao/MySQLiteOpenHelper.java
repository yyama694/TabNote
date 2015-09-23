package org.yyama.tabnote.dao;

import org.yyama.tabnote.constant.Constant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

	private static final String CREATE_TABLE_TAB = "CREATE TABLE "
			+ Constant.TABLE_NAME_TAB + "("
			+ "_id INTEGER PRIMARY KEY NOT NULL," + "title TEXT, "
			+ "value TEXT," + "tab_image_id INTEGER,"
			+ "underline_image_id INTEGER," + "tab_order INTEGER NOT NULL,"
			+ "create_datetime TEXT NOT NULL,"
			+ "modify_datetime TEXT NOT NULL);";
	private static final String CREATE_TABLE_ACTIVE = "CREATE TABLE "
			+ Constant.TABLE_NAME_ACTIVE
			+ "( active_tab_no INTEGER NOT NULL );";
	private static final String INSERT_ACTIVE = "INSERT INTO "
			+ Constant.TABLE_NAME_ACTIVE + " VALUES(0);";
	private static final String DB_NAME = "DB_TAB_NOTE";

	public MySQLiteOpenHelper(Context context) {
		super(context, DB_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("yyama", "DBçÏê¨ÇµÇ‹Ç∑ÅB");
		db.execSQL(CREATE_TABLE_TAB);
		db.execSQL(CREATE_TABLE_ACTIVE);
		db.execSQL(INSERT_ACTIVE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
