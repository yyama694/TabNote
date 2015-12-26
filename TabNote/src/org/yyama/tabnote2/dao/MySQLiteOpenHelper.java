package org.yyama.tabnote2.dao;

import org.yyama.tabnote2.constant.Constant;
import org.yyama.tabnote2.service.TabColorEnum;

import android.content.Context;
import android.database.Cursor;
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
		super(context, DB_NAME, null, 2);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("yyama", "DB作成します。");
		db.execSQL(CREATE_TABLE_TAB);
		db.execSQL(CREATE_TABLE_ACTIVE);
		db.execSQL(INSERT_ACTIVE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 1→2 タブのイメージIDをそのまま保存していた問題があったので、Enumで管理するようにした。
		if (oldVersion == 1 && newVersion == 2) {
			String selectSQL = "SELECT _id, tab_image_id, underline_image_id FROM "
					+ Constant.TABLE_NAME_TAB;
			String updateSQL = "UPDATE " + Constant.TABLE_NAME_TAB
					+ " SET tab_image_id=?," + "underline_image_id=?,"
					+ "modify_datetime=? WHERE _id=?";
			Cursor c = db.rawQuery(selectSQL, null);
			while (c.moveToNext()) {
				// TabColorEnumを取得
				int imegeId = c.getInt(1);
				TabColorEnum tabEnum = TabColorEnum
						.getTabColorEnumFromImageId(imegeId);

				String[] param = { String.valueOf(tabEnum.key),
						String.valueOf(tabEnum.key), c.getString(0) };
				db.execSQL(updateSQL, param);
			}

		}
	}

}
