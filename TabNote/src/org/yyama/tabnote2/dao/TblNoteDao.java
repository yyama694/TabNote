package org.yyama.tabnote2.dao;

import static org.yyama.tabnote2.constant.Constant.TABLE_NAME_NOTE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.yyama.tabnote2.model.Note;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TblNoteDao {
	private static final String DATE_PATTERN = "yyyy/MM/dd HH:mm:ss";

	private static SQLiteDatabase db;

	public static void init(Activity act) {
		MySQLiteOpenHelper helper = new MySQLiteOpenHelper(act);
		db = helper.getWritableDatabase();
	}

	private static final String SELECT_ALL = "SELECT _id,name,note_order FROM "
			+ TABLE_NAME_NOTE + " order by note_order;";

	public static List<Note> selectAll() {
		Cursor c = db.rawQuery(SELECT_ALL,new String[]{});
		List<Note> list = new ArrayList<>();
		while (c.moveToNext()) {
			Note note=new Note(c.getInt(0),c.getString(1),c.getLong(2));
			list.add(note);
		}
		return list;
	}

	private static final String INSERT_TABLE_NOTE = "INSERT INTO "
			+ TABLE_NAME_NOTE + "( name, note_order, create_datetime,"
			+ "modify_datetime )" + " VALUES (?,?,?,?);";

	public static long insert(Note note){
		String dateStr = new SimpleDateFormat(DATE_PATTERN).format(new Date());
		String[] param = { note.name,
				dateStr, dateStr };
		db.execSQL(INSERT_TABLE_NOTE, param);
		// ID���擾���Ԃ�
		Cursor c = db.rawQuery("SELECT LAST_INSERT_ROWID();", null);
		c.moveToNext();
		return c.getLong(0);
	}
}