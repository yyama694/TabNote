package org.yyama.tabnote.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.yyama.tabnote.R;
import org.yyama.tabnote.activity.MainActivity;
import org.yyama.tabnote.adapter.MainPagerAdapter;
import org.yyama.tabnote.dao.TblTabActiveDao;
import org.yyama.tabnote.dao.TblTabNoteDao;
import org.yyama.tabnote.model.Tab;
import org.yyama.tabnote.model.TabNote;
import org.yyama.tabnote.service.TabNoteService;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
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

	// 広告表示フラグ
	private static boolean showAd = false;

	private TabNoteView() {
	}

	public static void init(MainActivity act) {
		// 広告表示するかどうかの決定
		int i = PreferenceManager.getDefaultSharedPreferences(act).getInt(
				"showCount", 0);
		if (i >= 5) {
			if (new Random().nextInt(2) == 0) {
				showAd = true;
				Log.d("yyama", "広告を表示します。");
			} else {
				showAd = false;
				Log.d("yyama", "広告を表示しません。");
			}
		} else {
			Log.d("yyama", "起動回数を満たさないため、広告を表示しません。回数=" + i);
			PreferenceManager.getDefaultSharedPreferences(act).edit()
					.putInt("showCount", i + 1).commit();
		}
		TabNoteView.act = act;
		tabLL = (LinearLayout) act.findViewById(R.id.TabLinearLayout);
		underLineImg = (ImageView) act.findViewById(R.id.under_line);
		hsv = (HorizontalScrollView) act.findViewById(R.id.TabHorizontalSV);
		vp = (ViewPager) act.findViewById(R.id.main_view_pager);
		// メインビューの設定
		setMainViewPager();
		// アクティブタブの設定
		i = TblTabActiveDao.getActiveNum();
		TabNoteService.unActivateAll();
		TabNote.tabs.get(i).isActivate = true;

	}

	public static void draw() {
		draw(true);
	}

	public static void draw(boolean scrollMainView) {
		// タブのサイズを取得しておく（タブスクロールのため）
		int tabWidth = 0;
		int hsvWidth = 0;
		if (tabLL.getChildAt(0) != null) {
			tabWidth = tabLL.getChildAt(0).getWidth();
			hsvWidth = hsv.getWidth();
		}

		// クリア
		tabLL.removeAllViews();

		Button btn;
		// 通常タブ
		for (Tab tab : TabNote.tabs) {
			btn = getNewTabBtn();
			btn.setBackgroundResource(tab.tabImageId);
			btn.setText(tab.title);
			btn.setTag(tab);
			if (tab.isActivate) {
				// タブの下線
				underLineImg.setImageResource(tab.tabUnderLineImageId);
				btn.setAlpha(1.0f);
			} else {
				btn.setAlpha(0.6f);
			}
			tabLL.addView(btn);
			btn.setOnClickListener(act);
			btn.setOnLongClickListener(act);
			btn.setTag(tab);
		}

		// タブ追加タブ
		btn = getNewTabBtn();
		btn.setBackgroundResource(R.drawable.tab_add);
		btn.setOnClickListener(act);
		btn.setTag("addTab");
		btn.setAlpha(0.6f);
		tabLL.addView(btn);

		// タブのスクロール
		if (tabWidth != 0) {
			hsv.smoothScrollTo((TabNote.getActiveNum() * tabWidth) - hsvWidth
					/ 2 + tabWidth / 2, 0);
		}

		// メインビューのスクロール
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

			// 各種設定値のセット
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
			if (showAd) {
				addSetting(ll);
			}
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

		// 各種設定値の保存
		setPreference(tv, ev);

		// 広告の設定
		if (showAd) {
			addSetting(ll);
		}

		mpa.addView(ll);
		mpa.notifyDataSetChanged();
		vp.setCurrentItem(TabNote.getActiveNum());
	}

	private static void setPreference(LineTextView tv, LineEditTextView ev) {
		// アンダーラインフラグの設定
		ev.underLine = PreferenceManager.getDefaultSharedPreferences(act)
				.getBoolean("underlines", true);
		tv.underLine = PreferenceManager.getDefaultSharedPreferences(act)
				.getBoolean("underlines", true);
		// 文字サイズの設定
		ev.fontSize = Integer.parseInt(PreferenceManager
				.getDefaultSharedPreferences(act).getString("fontSize", "20"));
		tv.fontSize = Integer.parseInt(PreferenceManager
				.getDefaultSharedPreferences(act).getString("fontSize", "20"));

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
		// ソフトキーボードを出す
		InputMethodManager inputMethodManager = (InputMethodManager) act
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.showSoftInput(tv, 0);

		// ダブルタップの案内が表示されているときは、全選択にしてあげる
		if (tv.getText().toString()
				.equals(act.getString(R.string.double_tap_description))) {
			tv.selectAll();
		}

		// フォーカスを当ててソフトウェアキーボードを出す。
		tv.requestFocus();
		InputMethodManager manager = (InputMethodManager) act
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		manager.toggleSoftInput(1, InputMethodManager.SHOW_IMPLICIT);

		// 広告を非表示にする
		LinearLayout layout = (LinearLayout) ll.findViewById(R.id.adSpace);
		layout.setVisibility(View.GONE);

		// 編集中フラグをセット
		TabNote.tabs.get(vp.getCurrentItem()).isReadMode = false;
	}

	public static void toReadMode() {
		LinearLayout ll = mpa.getView(vp.getCurrentItem());
		((LineTextView) ll.findViewById(R.id.text_in_page_view))
				.setVisibility(View.VISIBLE);
		((LineEditTextView) ll.findViewById(R.id.edit_in_page_view))
				.setVisibility(View.GONE);
		// 広告を表示する
		LinearLayout layout = (LinearLayout) ll.findViewById(R.id.adSpace);
		layout.setVisibility(View.VISIBLE);
		// 編集中フラグをセット
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

	private static void addSetting(View v) {
		Log.d("yyama", "addSetting!!");
		AdView adView;
		// adView を作成する
		adView = new AdView(act);
		adView.setAdUnitId("ca-app-pub-2505812570403600/1091357771");
		adView.setAdSize(AdSize.BANNER);

		// 属性 android:id="@+id/mainLayout" が与えられているものとして
		// LinearLayout をルックアップする
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.adSpace);

		// adView を追加する
		layout.addView(adView);

		// 一般的なリクエストを行う
		AdRequest adRequest = new AdRequest.Builder().addTestDevice(
				"F3B1B2779DEF816F9B31AA6C6DC57C3F").build();
		// AdRequest adRequest = new AdRequest.Builder().build();

		// 広告リクエストを行って adView を読み込む
		adView.loadAd(adRequest);
	}

}
