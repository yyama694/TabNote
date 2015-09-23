package org.yyama.tabnote.view;

import java.util.ArrayList;
import java.util.List;

import org.yyama.tabnote.R;
import org.yyama.tabnote.activity.MainActivity;
import org.yyama.tabnote.adapter.MainPagerAdapter;
import org.yyama.tabnote.dao.TblTabActiveDao;
import org.yyama.tabnote.dao.TblTabNoteDao;
import org.yyama.tabnote.model.Tab;
import org.yyama.tabnote.model.TabNote;
import org.yyama.tabnote.service.TabNoteService;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class TabNoteView {
	private static MainActivity act;
	private static LinearLayout tabLL;
	private static HorizontalScrollView hsv;
	private static ImageView underLineImg;
	private static ViewPager vp;
	private static MainPagerAdapter mpa;

	private TabNoteView() {
	}

	public static void init(MainActivity act) {
		TabNoteView.act = act;
		tabLL = (LinearLayout) act.findViewById(R.id.TabLinearLayout);
		underLineImg = (ImageView) act.findViewById(R.id.under_line);
		hsv = (HorizontalScrollView) act.findViewById(R.id.TabHorizontalSV);
		vp = (ViewPager) act.findViewById(R.id.main_view_pager);
		// ���C���r���[�̐ݒ�
		setMainViewPager();
		// �A�N�e�B�u�^�u�̐ݒ�
		int i = TblTabActiveDao.getActiveNum();
		TabNoteService.unActivateAll();
		TabNote.tabs.get(i).isActivate = true;

	}

	public static void draw() {
		draw(true);
	}

	public static void draw(boolean scrollMainView) {
		// �^�u�̃T�C�Y���擾���Ă����i�^�u�X�N���[���̂��߁j
		int tabWidth = 0;
		int hsvWidth = 0;
		if (tabLL.getChildAt(0) != null) {
			tabWidth = tabLL.getChildAt(0).getWidth();
			hsvWidth = hsv.getWidth();
		}

		// �N���A
		tabLL.removeAllViews();

		Button btn;
		// �ʏ�^�u
		for (Tab tab : TabNote.tabs) {
			btn = getNewTabBtn();
			btn.setBackgroundResource(tab.tabImageId);
			btn.setText(tab.title);
			btn.setTag(tab);
			if (tab.isActivate) {
				// �^�u�̉���
				underLineImg.setImageResource(tab.tabUnderLineImageId);
				btn.setAlpha(1.0f);
			} else {
				btn.setAlpha(0.7f);
			}
			tabLL.addView(btn);
			btn.setOnClickListener(act);
			btn.setOnLongClickListener(act);
			btn.setTag(tab);
		}

		// �^�u�ǉ��^�u
		btn = getNewTabBtn();
		btn.setBackgroundResource(R.drawable.tab_add);
		btn.setOnClickListener(act);
		btn.setTag("addTab");
		tabLL.addView(btn);

		// �^�u�̃X�N���[��
		if (tabWidth != 0) {
			hsv.smoothScrollTo((TabNote.getActiveNum() * tabWidth) - hsvWidth
					/ 2 + tabWidth / 2, 0);
		}

		// ���C���r���[�̃X�N���[��
		if (scrollMainView) {
			vp.setCurrentItem(TabNote.getActiveNum());
		}
	}

	public static void setMainViewPager() {
		vp.removeOnPageChangeListener(act);
		List<LinearLayout> list = new ArrayList<>();
		for (Tab tab : TabNote.tabs) {
			LinearLayout ll = (LinearLayout) act.getLayoutInflater().inflate(
					R.layout.linear_in_page_view, null);
			LineTextView tv = (LineTextView) ll
					.findViewById(R.id.text_in_page_view);

			LineEditTextView ev = (LineEditTextView) ll
					.findViewById(R.id.edit_in_page_view);

			// �e��ݒ�l�̕ۑ�
			setPreference(tv, ev);

			tv.setText(tab.value);
			ev.setText(tab.value);
			if (!tab.isReadMode) {
				((LineTextView) ll.findViewById(R.id.text_in_page_view))
						.setVisibility(View.GONE);
				((LineEditTextView) ll.findViewById(R.id.edit_in_page_view))
						.setVisibility(View.VISIBLE);

			}
			((LineEditTextView) ll.findViewById(R.id.edit_in_page_view))
					.addTextChangedListener(act);
			list.add(ll);
		}
		MainPagerAdapter mpa = new MainPagerAdapter();
		TabNoteView.mpa = mpa;
		mpa.setList(list);
		vp.setAdapter(mpa);
		vp.setCurrentItem(TabNote.getActiveNum());
		vp.addOnPageChangeListener(act);
	}

	public static void addMainViewPager(String str) {
		LinearLayout ll = (LinearLayout) act.getLayoutInflater().inflate(
				R.layout.linear_in_page_view, null);
		LineTextView tv = (LineTextView) ll
				.findViewById(R.id.text_in_page_view);
		LineEditTextView ev = (LineEditTextView) ll
				.findViewById(R.id.edit_in_page_view);
		tv.setText(str);
		ev.setText(str);
		ev.addTextChangedListener(act);

		// �e��ݒ�l�̕ۑ�
		setPreference(tv, ev);

		mpa.addView(ll);
		mpa.notifyDataSetChanged();
		vp.setCurrentItem(TabNote.getActiveNum());
	}

	private static void setPreference(LineTextView tv, LineEditTextView ev) {
		// �A���_�[���C���t���O�̐ݒ�
		ev.underLine = PreferenceManager.getDefaultSharedPreferences(act)
				.getBoolean("underlines", true);
		tv.underLine = PreferenceManager.getDefaultSharedPreferences(act)
				.getBoolean("underlines", true);
		// �����T�C�Y�̐ݒ�
		ev.fontSize = Integer.parseInt(PreferenceManager
				.getDefaultSharedPreferences(act).getString("fontSize", "20"));
		tv.fontSize = Integer.parseInt(PreferenceManager
				.getDefaultSharedPreferences(act).getString("fontSize", "20"));

	}

	@Deprecated
	public static void removeMainViewPager(int i) {
		// �@���܂������Ă���Ȃ��̂ł��̃��\�b�h�͎g�p���Ȃ��ł��������B
		mpa.removeView(i);
		mpa.notifyDataSetChanged();
	}

	private static Button getNewTabBtn() {
		Button btn = new Button(act);
		btn.setTextColor(act.getResources().getColor(android.R.color.white));
		return btn;
	}

	public static void toEditMoge() {
		LinearLayout ll = mpa.getView(vp.getCurrentItem());
		((LineTextView) ll.findViewById(R.id.text_in_page_view))
				.setVisibility(View.GONE);
		LineEditTextView tv = (LineEditTextView) ll
				.findViewById(R.id.edit_in_page_view);
		tv.setVisibility(View.VISIBLE);
		// �\�t�g�L�[�{�[�h���o��
		InputMethodManager inputMethodManager = (InputMethodManager) act
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.showSoftInput(tv, 0);

		// �_�u���^�b�v�̈ē����\������Ă���Ƃ��́A�S�I���ɂ��Ă�����
		if (tv.getText().toString()
				.equals(act.getString(R.string.double_tap_description))) {
			tv.selectAll();
		}

		// �t�H�[�J�X�𓖂Ăă\�t�g�E�F�A�L�[�{�[�h���o���B
		tv.requestFocus();
		InputMethodManager manager = (InputMethodManager) act
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		manager.toggleSoftInput(1, InputMethodManager.SHOW_IMPLICIT);

		// �ҏW���t���O���Z�b�g
		TabNote.tabs.get(vp.getCurrentItem()).isReadMode = false;
	}

	public static void toReadMode() {
		LinearLayout ll = mpa.getView(vp.getCurrentItem());
		((LineTextView) ll.findViewById(R.id.text_in_page_view))
				.setVisibility(View.VISIBLE);
		((LineEditTextView) ll.findViewById(R.id.edit_in_page_view))
				.setVisibility(View.GONE);
		// �ҏW���t���O���Z�b�g
		TabNote.tabs.get(vp.getCurrentItem()).isReadMode = true;
	}

	public static boolean isEditMode() {
		LinearLayout ll = mpa.getView(vp.getCurrentItem());
		LineTextView tv = (LineTextView) ll
				.findViewById(R.id.text_in_page_view);
		if (tv.getVisibility() == View.GONE) {
			return true;
		}
		return false;
	}

	public static void saveValueAll() {
		boolean updated = false;
		for (int i = 0; i < TabNote.tabs.size(); i++) {
			if (!TabNote.tabs.get(i).isReadMode && TabNote.tabs.get(i).edited) {
				updated = true;
				LineTextView tv;
				LineEditTextView ev;

				tv = (LineTextView) mpa.getView(i).findViewById(
						R.id.text_in_page_view);
				ev = (LineEditTextView) mpa.getView(i).findViewById(
						R.id.edit_in_page_view);
				tv.setText(ev.getText().toString());
				TabNote.tabs.get(i).value = ev.getText().toString();
				TabNote.tabs.get(i).edited = false;
				TblTabNoteDao.update(TabNote.tabs.get(i));
			}
		}
		if (updated) {
			Toast.makeText(act, act.getString(R.string.has_been_saved),
					Toast.LENGTH_SHORT).show();
		}
	}

	public static void saveValue() {
		Tab activeTab = TabNote.tabs.get(TabNote.getActiveNum());
		if (!activeTab.isReadMode && activeTab.edited) {
			LineTextView tv;
			LineEditTextView ev;

			tv = (LineTextView) mpa.getView(vp.getCurrentItem()).findViewById(
					R.id.text_in_page_view);
			ev = (LineEditTextView) mpa.getView(vp.getCurrentItem())
					.findViewById(R.id.edit_in_page_view);

			tv.setText(ev.getText().toString());
			activeTab.value = ev.getText().toString();
			activeTab.edited = false;
			TblTabNoteDao.update(activeTab);
			Toast.makeText(act, act.getString(R.string.has_been_saved),
					Toast.LENGTH_SHORT).show();
		}
	}
}
