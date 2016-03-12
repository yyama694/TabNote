package org.yyama.tabnote2.activity;

import org.yyama.tabnote2.R;
import org.yyama.tabnote2.dao.TblNoteDao;
import org.yyama.tabnote2.dao.TblTabDao;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class TableDebugActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_table_debug);
		TextView tv = (TextView) findViewById(R.id.TableDebugTextView);
		tv.setText(TblNoteDao.StaticToString()
				+ System.getProperty("line.separator")
				+ TblTabDao.staticToString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.table_debug, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
