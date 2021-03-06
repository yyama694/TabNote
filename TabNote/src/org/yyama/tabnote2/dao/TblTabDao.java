package org.yyama.tabnote2.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.yyama.tabnote2.constant.Constant.*;
import org.yyama.tabnote2.model.Tab;
import org.yyama.tabnote2.model.TabNote;
import org.yyama.tabnote2.service.TabColorEnum;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

//tab_image_idカラムにはTabColorEnumのキー値が入る。ころあいを見計らって名前を変える。
//underline_image_idは未使用項目。ころあいを見計らって消す
public class TblTabDao {
	private static SQLiteDatabase db;
	private static final String DATE_PATTERN = "yyyy/MM/dd HH:mm:ss";

	public static void init(Activity act) {
		MySQLiteOpenHelper helper = new MySQLiteOpenHelper(act);
		db = helper.getWritableDatabase();
	}

	// TODO 引数で指定されたノートIDのみのタブを返すように変更予定、。
	public static List<Tab> selectTabBelongActiveNote() {
		String sql = "SELECT _id, title, value, tab_color_key FROM "
				+ TABLE_NAME_TAB + " ORDER BY tab_order;";
		Cursor c = db.rawQuery(sql, null);
		List<Tab> list = new ArrayList<>();
		while (c.moveToNext()) {
			// タブオブジェクトをセット
			Tab tab = new Tab();
			tab.id = c.getLong(0);
			tab.title = c.getString(1);
			tab.value = c.getString(2);
			tab.color = TabColorEnum.getTabColorEnumFromKey(c.getInt(3));
			list.add(tab);
		}
		return list;
	}

	public static long insert(Tab tab, int order) {
		String sql = "INSERT INTO " + TABLE_NAME_TAB
				+ "( title, value, tab_color_key, "
				+ " tab_order, create_datetime, modify_datetime ) "
				+ "VALUES (?,?,?,?,?,?);";
		String dateStr = new SimpleDateFormat(DATE_PATTERN).format(new Date());

		String[] param = { tab.title, tab.value, String.valueOf(tab.color.key),
				String.valueOf(order), dateStr, dateStr };
		db.execSQL(sql, param);
		// IDを取得し返す
		Cursor c = db.rawQuery("SELECT LAST_INSERT_ROWID();", null);
		c.moveToNext();
		return c.getLong(0);
	}

	public static void update(Tab tab) {
		String sql = "UPDATE " + TABLE_NAME_TAB + " SET title=?," + "value=?,"
				+ "tab_color_key=?," + "modify_datetime=? WHERE _id=?";
		String dateStr = new SimpleDateFormat(DATE_PATTERN).format(new Date());

		String[] param = { tab.title, tab.value, String.valueOf(tab.color.key),
				dateStr, String.valueOf(tab.id) };
		db.execSQL(sql, param);
	}

	public static void delete(Tab tab) {
		String sql = "DELETE FROM " + TABLE_NAME_TAB + " WHERE _id=?";
		String[] param = { String.valueOf(tab.id) };
		db.execSQL(sql, param);
	}

	public static void updateOrder() {
		String sql = "UPDATE " + TABLE_NAME_TAB
				+ " SET tab_order=? WHERE _id=?";
		for (int i = 0; i < TabNote.tabs.size(); i++) {
			Tab tab = TabNote.tabs.get(i);
			String[] param = new String[2];
			param[0] = String.valueOf(i);
			param[1] = String.valueOf(tab.id);
			db.execSQL(sql, param);
		}
	}

	public static void close() {
		db.close();
	}

}
