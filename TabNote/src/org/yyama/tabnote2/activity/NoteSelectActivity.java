package org.yyama.tabnote2.activity;

import java.util.List;

import org.yyama.tabnote2.R;
import org.yyama.tabnote2.dao.TblNoteDao;
import org.yyama.tabnote2.model.Note;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class NoteSelectActivity extends AppCompatActivity {

	private LinearLayout ll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_note);
		ActionBar bar = getSupportActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
//		bar.setDisplayShowHomeEnabled(true);
//		bar.setIcon(R.drawable.ic_launcher);
		setParts();
		show();
	}

	private void setParts() {
		ll = (LinearLayout) findViewById(R.id.note_select_linear_layout);
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
			childLL.addView(ib);
			ib = new ImageButton(this);
			ib.setImageResource(android.R.drawable.ic_menu_delete);
			ib.setLayoutParams(lp);
			ib.setBackground(null);
			ib.setBackgroundResource(R.drawable.select_note_selector);
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
}
