package org.yyama.tabnote2.activity;

import java.util.List;

import org.yyama.tabnote2.R;
import org.yyama.tabnote2.adndoird_util.KeyboardUtils;
import org.yyama.tabnote2.dao.TblNoteDao;
import org.yyama.tabnote2.model.Note;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class NoteSelectActivity extends AppCompatActivity implements
		OnClickListener {
	private enum ItemKind {
		NOTE, EDIT, DELETE
	}

	private class ItemTag {
		final ItemKind itemKind;
		final String noteName;

		public ItemTag(ItemKind item, String noteName) {
			this.itemKind = item;
			this.noteName = noteName;
		}
	}

	private LinearLayout ll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_note);
		setTitle(R.string.select_note);
		ActionBar bar = getSupportActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setHomeAsUpIndicator(R.drawable.ic_menu_back);
		// bar.setDisplayShowHomeEnabled(true);
		// bar.setIcon(R.drawable.ic_launcher);
		setParts();
		show();
	}

	private void setParts() {
		ll = (LinearLayout) findViewById(R.id.note_select_linear_layout);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			// 戻るボタン
			finish();
			return true;
		case R.id.add_note:
			// 新規ノート
			showNewNoteDialog();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void show() {
		List<Note> notes = TblNoteDao.selectAll();
		// for (Note note : notes) {
		for (int i = 0; i < 20; i++) {
			LinearLayout childLL = getLlAtList();
			childLL.addView(getTextViewAtList());
			ImageButton ib = new ImageButton(this);
			ib.setImageResource(android.R.drawable.ic_menu_preferences);
			ib.setBackground(null);
			ib.setBackgroundResource(R.drawable.select_note_selector);
			LayoutParams lp = new LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.MATCH_PARENT, 0);
			MarginLayoutParams mp = (MarginLayoutParams) lp;
			mp.setMargins(0, 0, 0, 0);
			ib.setLayoutParams(lp);
			ItemTag tag = new ItemTag(ItemKind.EDIT, "aaaaaaa"); // 第2引数はノート名
			ib.setTag(tag);
			ib.setOnClickListener(this);
			childLL.addView(ib);

			ib = new ImageButton(this);
			ib.setImageResource(android.R.drawable.ic_menu_delete);
			ib.setLayoutParams(lp);
			ib.setBackground(null);
			ib.setBackgroundResource(R.drawable.select_note_selector);
			ib.setOnClickListener(this);
			tag = new ItemTag(ItemKind.DELETE, "aaaaaaa"); // 第2引数はノート名
			ib.setTag(tag);
			childLL.addView(ib);
			ll.addView(childLL);
		}
		// }
	}

	private TextView getTextViewAtList() {
		TextView tv = new TextView(this);
		tv.setClickable(true);
		tv.setBackgroundResource(R.drawable.select_note_selector);
		tv.setText("aaadsfsadf");
		tv.setTextSize(30);
		tv.setPadding(15, 30, 15, 30);
		tv.setTextColor(Color.BLACK);
		LayoutParams lp = new LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, 1);
		tv.setLayoutParams(lp);
		return tv;
	}

	private LinearLayout getLlAtList() {
		LinearLayout childLL = new LinearLayout(this);
		childLL.setOrientation(LinearLayout.HORIZONTAL);
		childLL.setBackgroundColor(Color.WHITE);
		LayoutParams lp = new LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, 1);
		lp.setMargins(4, 4, 4, 4);
		childLL.setLayoutParams(lp);

		return childLL;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.select_notoe_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onClick(View v) {
		Object tag = v.getTag();
		if (tag != null && tag instanceof ItemTag) {
			ItemTag iTag = (ItemTag) tag;
			switch (iTag.itemKind) {
			case DELETE:
				showDeleteComfirmDialog(iTag);
				break;
			case EDIT:
				showChangeNoteDialog(iTag);
				break;
			default:
				break;
			}
		}
	}

	// ファイル削除時のダイアログを表示する
	private void showDeleteComfirmDialog(ItemTag iTag) {
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.delete_confirmation_title))
				.setMessage(
						getString(R.string.note_delete_confirmation) + "["
								+ iTag.noteName + "]")
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// OK button pressed
							}
						}).setNegativeButton(android.R.string.cancel, null)
				.show();
	}

	// 新規ノート名入力ダイアログを表示する
	private void showNewNoteDialog() {

		// テキスト入力を受け付けるビューを作成します。
		final EditText editView = new EditText(this);
		editView.setText(getString(R.string.note) + " "
				+ (TblNoteDao.getMaxId() + 1));
		editView.selectAll();
		new AlertDialog.Builder(this)
				.setTitle(R.string.add_note)
				.setMessage(R.string.new_note_name)
				// setViewにてビューを設定します。
				.setView(editView)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// 入力した文字をトースト出力する
								Toast.makeText(NoteSelectActivity.this,
										editView.getText().toString(),
										Toast.LENGTH_LONG).show();
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).show();
		KeyboardUtils.show(this, editView);
	}

	// ノート名変更入力ダイアログを返す。
	private void showChangeNoteDialog(ItemTag iTag) {
		final EditText editView = new EditText(this);
		editView.setText(iTag.noteName);
		editView.selectAll();
		new AlertDialog.Builder(this)
				.setTitle(R.string.change_note_name_title)
				.setMessage(R.string.change_note_name)
				// setViewにてビューを設定します。
				.setView(editView)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// 入力した文字をトースト出力する
								Toast.makeText(NoteSelectActivity.this,
										editView.getText().toString(),
										Toast.LENGTH_LONG).show();
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).show();
		KeyboardUtils.show(this, editView);
	}

}
