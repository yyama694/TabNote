package org.yyama.tabnote2.dao;

import static org.yyama.tabnote2.constant.Constant.TABLE_NAME_ACTIVE;
import static org.yyama.tabnote2.constant.Constant.TABLE_NAME_NOTE;
import static org.yyama.tabnote2.constant.Constant.TABLE_NAME_TAB;
import static org.yyama.tabnote2.constant.Constant.TABLE_NAME_TAB_NOTE;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.yyama.tabnote2.R;
import org.yyama.tabnote2.service.TabColorEnum;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
	// NOTEテーブル
	private static final String CREATE_TABLE_NOTE = "CREATE TABLE "
			+ TABLE_NAME_NOTE + "(" + "_id INTEGER PRIMARY KEY NOT NULL,"
			+ "name TEXT," + "note_order TEXT," + "create_datetime TEXT,"
			+ "modify_datetime TEXT" + ");";

	// TABテーブル
	private static final String CREATE_TABLE_TAB = "CREATE TABLE "
			+ TABLE_NAME_TAB
			+ "("
			+ "_id INTEGER PRIMARY KEY NOT NULL,"
			+ "title TEXT, "
			+ "value TEXT,"
			+ "tab_color_key INTEGER,"
			+ "tab_order INTEGER NOT NULL,"
			+ "fk_note_id INTEGER NOT NULL,"
			+ "create_datetime TEXT NOT NULL,"
			+ "modify_datetime TEXT NOT NULL,FOREIGN KEY(fk_note_id) REFERENCES "
			+ TABLE_NAME_NOTE + "(_id)" + ");";

	// ACTIVEテーブル
	private static final String CREATE_TABLE_ACTIVE = "CREATE TABLE "
			+ TABLE_NAME_ACTIVE + "( active_tab_no INTEGER NOT NULL );";
	private static final String INSERT_ACTIVE = "INSERT INTO "
			+ TABLE_NAME_ACTIVE + " VALUES(0);";
	private static final String DB_NAME = "DB_TAB_NOTE";

	private static Activity act;

	public MySQLiteOpenHelper(Context context) {
		super(context, DB_NAME, null, 3);
		MySQLiteOpenHelper.act = (Activity) context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("yyama", "DB作成します。");
		db.execSQL(CREATE_TABLE_NOTE);
		db.execSQL(CREATE_TABLE_TAB);
		db.execSQL(CREATE_TABLE_ACTIVE);
		db.execSQL(INSERT_ACTIVE);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		// 外部キーを有効にする
		db.execSQL("PRAGMA foreign_keys=ON;");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// ver1は、タブのイメージIDをそのまま保存していた問題があったので、Enumで管理するようにした。
		if (oldVersion == 1) {
			System.out.println("enumを使うように移行します。");
			String selectSQL = "SELECT _id, tab_image_id, underline_image_id FROM "
					+ TABLE_NAME_TAB_NOTE;
			String updateSQL = "UPDATE " + TABLE_NAME_TAB_NOTE
					+ " SET tab_image_id=?," + "underline_image_id=?,"
					+ "modify_datetime=? WHERE _id=?";
			Cursor c = db.rawQuery(selectSQL, null);
			String dateStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
					.format(new Date());
			while (c.moveToNext()) {
				// TabColorEnumを取得
				int imegeId = c.getInt(1);
				TabColorEnum tabEnum = TabColorEnum
						.getTabColorEnumFromImageId(imegeId);

				String[] param = { String.valueOf(tabEnum.key),
						String.valueOf(tabEnum.key), dateStr, c.getString(0) };
				db.execSQL(updateSQL, param);
			}
		}
		// データの持ち方を変えたので、新しいテーブルに移行
		if (oldVersion <= 2) {
			System.out.println("dataの持ち方を変える移行を行います。");
			// Noteテーブルへの処理
			db.execSQL(CREATE_TABLE_NOTE);
			String ins = "INSERT INTO " + TABLE_NAME_NOTE
					+ "( name, note_order, create_datetime,"
					+ "modify_datetime )" + " VALUES (?,?,?,?);";
			String dateStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
					.format(new Date());
			String[] param = new String[] {
					act.getString(R.string.default_note_name), "0", dateStr,
					dateStr };

			// データ移行
			db.execSQL(CREATE_TABLE_TAB);
			String selSql = "SELECT title,value,tab_image_id,create_datetime "
					+ "from " + TABLE_NAME_TAB_NOTE + ";";
			ins = "INSERT INTO " + TABLE_NAME_TAB + "(title," + "value,"
					+ "tab_color_key," + "fk_note_id," + "create_datetime,"
					+ "modify_datetime) VALUES(?,?,?,?,?,?)";
			Cursor c = db.rawQuery(selSql, null);
			while (c.moveToNext()) {
				param = new String[] { c.getString(0), c.getString(1),
						c.getString(2), c.getString(3), "0", dateStr };
				db.execSQL(ins, param);

			}
		}
	}
}
