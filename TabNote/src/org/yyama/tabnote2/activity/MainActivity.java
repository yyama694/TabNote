package org.yyama.tabnote2.activity;

import org.yyama.tabnote2.R;
import org.yyama.tabnote2.dao.TblNoteDao;
import org.yyama.tabnote2.dao.TblTabActiveDao;
import org.yyama.tabnote2.dao.TblTabDao;
import org.yyama.tabnote2.model.Tab;
import org.yyama.tabnote2.model.TabNote;
import org.yyama.tabnote2.service.TabNoteService;
import org.yyama.tabnote2.view.TabNoteView;
import org.yyama.tabnote2.view.TabUpdateDialogView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;

public class MainActivity extends AppCompatActivity implements OnClickListener,
		OnLongClickListener, OnPageChangeListener, OnGestureListener,
		OnDoubleTapListener, OnTouchListener, TextWatcher,
		OnGlobalLayoutListener {

	private GestureDetector gd;

	@Override
	protected void onPause() {
		TabNoteView.saveValueAll();
		TblTabActiveDao.setActiveNum(TabNote.getActiveNum());
		super.onPause();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
	}

	// 一番最初の起動時にonWindowFocusChanged内でdrawするためのフラグ
	private boolean firstDraw = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		TabNoteService.act = this;
		TblNoteDao.init(this);
		TblTabDao.init(this);
		TblTabActiveDao.init(this);
		TabNoteService.init(this);
		TabNoteView.init(this);
		TabUpdateDialogView.init(this);
		ViewPager viewPager = (ViewPager) findViewById(R.id.main_view_pager);
		viewPager.addOnPageChangeListener(this);
		viewPager.setOnTouchListener(this);
		gd = new GestureDetector(viewPager.getContext(), this);

		// 起動時、タブが自動スクロールするための細工
		ViewTreeObserver observer = findViewById(R.id.TabLinearLayout)
				.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(this);

		TabNoteView.draw(true);
		onNewIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		// ほかのアプリからIntentを受信した場合は、新しいタブを追加
		String str = intent.getStringExtra(Intent.EXTRA_TEXT);
		if (str != null) {
			Tab tab = TabNoteService.addTab(str);
			TabNoteView.addMainViewPager(str);
			// DBに追加する
			tab.id = TblTabDao.insert(tab, TabNote.tabs.size() - 1);
			firstDraw = true;
		} else {
			Log.d("yyama", "intentはnullです。");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		TblTabDao.close();
		TblTabActiveDao.close();
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.app_settings) {
			Intent intent = new Intent(this, AppSettingPrefActivity.class);
			startActivity(intent);
			return true;
		} else if (id == R.id.tab_settings) {
			showContextMenu(TabNote.tabs.get(TabNote.getActiveNum()));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		Object o = v.getTag();
		if (o instanceof String && o.equals("addTab")) {
			TabUpdateDialogView.setIsAdd(true);
			DialogFragment df = new TabUpdateFragment();
			df.show(getSupportFragmentManager(), "");
		}
		if (o instanceof Tab) {
			TabNoteService.unActivateAll();
			((Tab) o).isActivate = true;
			TabNoteView.draw(true);
		}
	}

	@Override
	public boolean onLongClick(View v) {

		// タブ長押しで振動させる
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(100);

		// クリックイベントを発生させる
		v.performClick();

		showContextMenu((Tab) v.getTag());

		return true;
	}

	private void showContextMenu(Tab t) {
		// コンテキストメニュー風のメニューを表示する
		DialogFragment df = new TabContextMenuFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable("tab", t);
		df.setArguments(bundle);
		df.show(getSupportFragmentManager(), "");
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	// ページ選択した際に、なぜか2回呼ばれる。要改善。
	@Override
	public void onPageSelected(int currentItem) {
		TabNoteService.unActivateAll();
		TabNote.tabs.get(currentItem).isActivate = true;
		TabNoteView.draw(false);
		if (TabNote.tabs.get(currentItem).isReadMode) {
			if (getCurrentFocus() != null) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		// Log.d("yyama", "onDoubleTap!");
		TabNoteView.toEditMoge();
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (TabNoteView.isEditMode()) {
				TabNoteView.saveValue();
				TabNoteView.toReadMode();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// Log.d("yyama", "onTouch!!");
		// return gd.onTouchEvent(event);
		return false;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// ScrollViewがonTouchイベントを消化してしまうので、dispatchTouchEventのほうで↓の処理を行うように修正した。
		// とりあえず動いている。
		gd.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		TabNote.tabs.get(TabNote.getActiveNum()).edited = true;
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	// 起動時、タブが自動スクロールするための細工
	@Override
	public void onGlobalLayout() {
		if (firstDraw) {
			firstDraw = false;
			TabNoteView.tabScroll();
		}

	}
}
