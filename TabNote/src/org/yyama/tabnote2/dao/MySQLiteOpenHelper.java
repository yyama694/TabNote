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
	// NOTE�e�[�u��
	private static final String CREATE_TABLE_NOTE = "CREATE TABLE "
			+ TABLE_NAME_NOTE + "(" + "_id INTEGER PRIMARY KEY NOT NULL,"
			+ "name TEXT," + "note_order TEXT," + "create_datetime TEXT,"
			+ "modify_datetime TEXT" + ");";

	// TAB�e�[�u��
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

	// ACTIVE�e�[�u��
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
		Log.d("yyama", "DB�쐬���܂��B");
		db.execSQL(CREATE_TABLE_NOTE);
		db.execSQL(CREATE_TABLE_TAB);
		db.execSQL(CREATE_TABLE_ACTIVE);
		db.execSQL(INSERT_ACTIVE);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		// �O���L�[��L���ɂ���
		db.execSQL("PRAGMA foreign_keys=ON;");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// ver1�́A�^�u�̃C���[�WID�����̂܂ܕۑ����Ă�����肪�������̂ŁAEnum�ŊǗ�����悤�ɂ����B
		if (oldVersion == 1) {
			System.out.println("enum���g���悤�Ɉڍs���܂��B");
			String selectSQL = "SELECT _id, tab_image_id, underline_image_id FROM "
					+ TABLE_NAME_TAB_NOTE;
			String updateSQL = "UPDATE " + TABLE_NAME_TAB_NOTE
					+ " SET tab_image_id=?," + "underline_image_id=?,"
					+ "modify_datetime=? WHERE _id=?";
			Cursor c = db.rawQuery(selectSQL, null);
			String dateStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
					.format(new Date());
			while (c.moveToNext()) {
				// TabColorEnum���擾
				int imegeId = c.getInt(1);
				TabColorEnum tabEnum = TabColorEnum
						.getTabColorEnumFromImageId(imegeId);

				String[] param = { String.valueOf(tabEnum.key),
						String.valueOf(tabEnum.key), dateStr, c.getString(0) };
				db.execSQL(updateSQL, param);
			}
		}
		// �f�[�^�̎�������ς����̂ŁA�V�����e�[�u���Ɉڍs
		if (oldVersion <= 2) {
			System.out.println("data�̎�������ς���ڍs���s���܂��B");
			// Note�e�[�u���ւ̏���
			db.execSQL(CREATE_TABLE_NOTE);
			String ins = "INSERT INTO " + TABLE_NAME_NOTE
					+ "( name, note_order, create_datetime,"
					+ "modify_datetime )" + " VALUES (?,?,?,?);";
			String dateStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
					.format(new Date());
			String[] param = new String[] {
					act.getString(R.string.default_note_name), "0", dateStr,
					dateStr };

			// �f�[�^�ڍs
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
