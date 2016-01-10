package org.yyama.tabnote2.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.yyama.tabnote2.R;
import org.yyama.tabnote2.activity.MainActivity;
import org.yyama.tabnote2.adapter.MainPagerAdapter;
import org.yyama.tabnote2.dao.TblTabActiveDao;
import org.yyama.tabnote2.dao.TblTabNoteDao;
import org.yyama.tabnote2.model.Tab;
import org.yyama.tabnote2.model.TabNote;
import org.yyama.tabnote2.service.TabNoteService;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.content.Context;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

/**
 * @author fukuchi
 * 
 */
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
				// Log.d("yyama", "広告を表示します。");
			} else {
				showAd = false;
				// Log.d("yyama", "広告を表示しません。");
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

	public static void draw(boolean scrollMainView) {
		Log.d("yyama", "draw");

		// タブのサイズを取得しておく（タブスクロールのため）
		int[] tabWidths = new int[tabLL.getChildCount()];
		int hsvWidth = 0;
		// int allWidth = 0;
		if (tabLL.getChildAt(0) != null) {
			for (int i = 0; i < tabWidths.length - 1; i++) {
				tabWidths[i] = tabLL.getChildAt(i).getWidth();
				// allWidth += tabLL.getChildAt(i).getWidth();
				// Log.d("yyama", "width:" + i + ":" + tabWidths[i]);
			}
			hsvWidth = hsv.getWidth();
			// Log.d("yyama", "tabWidth:" + tabWidth);
			// Log.d("yyama", "hsvWidth:" + hsvWidth);
		}
		// クリア
		tabLL.removeAllViews();

		// タブサイズの取得
		// int size = Integer.parseInt(PreferenceManager
		// .getDefaultSharedPreferences(act).getString("tabSize", "20"));
		// int btnWidth = act.getResources().getDimensionPixelSize(
		// R.dimen.width320dp);
		// int btnHeight = act.getResources().getDimensionPixelSize(
		// R.dimen.height70dp);

		Button btn;
		// 通常タブ
		for (Tab tab : TabNote.tabs) {
			btn = getNewTabBtn();
			// タブのサイズを設定(未実装)
			// btn.setWidth(btnWidth);
			// btn.setHeight(btnHeight);

			btn.setBackgroundResource(tab.color.tabImageId);
			// btn.setTextSize(size);
			btn.setText(tab.title);
			btn.setTag(tab);
			if (tab.isActivate) {
				// タブの下線
				underLineImg.setImageResource(tab.color.underlineImageId);
				setAlphaForView(btn, 1.0f);
			} else {
				setAlphaForView(btn, 0.6f);
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
		// btn.setTextSize(size);
		// btn.setHeight(btnHeight);
		// btn.setWidth((int) (btnHeight * 1.18));
		setAlphaForView(btn, 0.6f);
		tabLL.addView(btn);

		// タブのスクロール
		if (tabWidths.length != 0 && tabWidths[0] != 0) {
			int activeNum = TabNote.getActiveNum();
			int width = 0;
			for (int i = 0; i <= activeNum - 1; i++) {
				width += tabWidths[i];
			}
			hsv.smoothScrollTo(width - hsvWidth / 2 + tabWidths[activeNum] / 2,
					0);
		}

		// メインビューのスクロール
		if (scrollMainView) {
			vp.setCurrentItem(TabNote.getActiveNum());
		}
	}

	public static void tabScroll() {
		// タブのサイズを取得しておく（タブスクロールのため）
		int[] tabWidths = new int[tabLL.getChildCount()];
		int hsvWidth = 0;
		if (tabLL.getChildAt(0) != null) {
			for (int i = 0; i < tabWidths.length - 1; i++) {
				tabWidths[i] = tabLL.getChildAt(i).getWidth();
			}
			hsvWidth = hsv.getWidth();
		}
		// タブのスクロール
		if (tabWidths.length != 0 && tabWidths[0] != 0) {
			int activeNum = TabNote.getActiveNum();
			int width = 0;
			for (int i = 0; i <= activeNum - 1; i++) {
				width += tabWidths[i];
			}
			hsv.smoothScrollTo(width - hsvWidth / 2 + tabWidths[activeNum] / 2,
					0);
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
			if (Build.VERSION.SDK_INT >= 11) {
				tv.setTextIsSelectable(true);
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
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		MarginLayoutParams mlp = (MarginLayoutParams) lp;
		mlp.setMargins(-1, 0, -1, 0);
		// マージンを設定
		btn.setLayoutParams(mlp);
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

	/**
	 * android2.3でsetAlphaが使用できないので、回避用のメソッド
	 * 
	 * @param v
	 * @param alpha
	 */
	private static void setAlphaForView(View v, float alpha) {
		AlphaAnimation animation = new AlphaAnimation(alpha, alpha);
		animation.setDuration(0);
		animation.setFillAfter(true);
		v.startAnimation(animation);
	}
}
