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

	// ��ԍŏ��̋N������onWindowFocusChanged����draw���邽�߂̃t���O
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

		// �N�����A�^�u�������X�N���[�����邽�߂̍׍H
		ViewTreeObserver observer = findViewById(R.id.TabLinearLayout)
				.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(this);

		TabNoteView.draw(true);
		onNewIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		// �ق��̃A�v������Intent����M�����ꍇ�́A�V�����^�u��ǉ�
		String str = intent.getStringExtra(Intent.EXTRA_TEXT);
		if (str != null) {
			Tab tab = TabNoteService.addTab(str);
			TabNoteView.addMainViewPager(str);
			// DB�ɒǉ�����
			tab.id = TblTabDao.insert(tab, TabNote.tabs.size() - 1);
			firstDraw = true;
		} else {
			Log.d("yyama", "intent��null�ł��B");
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

		// �^�u�������ŐU��������
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(100);

		// �N���b�N�C�x���g�𔭐�������
		v.performClick();

		showContextMenu((Tab) v.getTag());

		return true;
	}

	private void showContextMenu(Tab t) {
		// �R���e�L�X�g���j���[���̃��j���[��\������
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

	// �y�[�W�I�������ۂɁA�Ȃ���2��Ă΂��B�v���P�B
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
		// ScrollView��onTouch�C�x���g���������Ă��܂��̂ŁAdispatchTouchEvent�̂ق��Ł��̏������s���悤�ɏC�������B
		// �Ƃ肠���������Ă���B
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

	// �N�����A�^�u�������X�N���[�����邽�߂̍׍H
	@Override
	public void onGlobalLayout() {
		if (firstDraw) {
			firstDraw = false;
			TabNoteView.tabScroll();
		}

	}
}
