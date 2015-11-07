package org.yyama.tabnote2.view;

import org.yyama.tabnote2.R;
import org.yyama.tabnote2.activity.MainActivity;
import org.yyama.tabnote2.model.Tab;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class TabUpdateDialogView {
	private static MainActivity act;
	private static View view;
	private static boolean isAdd;

	private TabUpdateDialogView() {
	}

	public static void init(MainActivity act) {
		TabUpdateDialogView.act = act;
	}

	public static void setIsAdd(boolean isAdd) {
		TabUpdateDialogView.isAdd = isAdd;
	}

	public static void setView(View view) {
		TabUpdateDialogView.view = view;
	}

	public static void setSetting(Bundle savedInstanceState) {
		TextView tv = (TextView) view
				.findViewById(R.id.tab_update_dialog_title);
		EditText et = (EditText) view.findViewById(R.id.tab_title_edit);
		if (isAdd) {
			tv.setText(act.getString(R.string.add_tab));
			tv.setCompoundDrawablesWithIntrinsicBounds(
					android.R.drawable.ic_menu_add, 0, 0, 0);
			et.setText("");
		} else {
			tv.setText(act.getString(R.string.settings_tab));
			tv.setCompoundDrawablesWithIntrinsicBounds(
					android.R.drawable.ic_menu_preferences, 0, 0, 0);
			Tab tab = (Tab) savedInstanceState.getSerializable("tab");
			et.setText(tab.title);
			et.selectAll();
		}
	}

	public static void setViewPagerPadding() {
		ViewPager vp = (ViewPager) view.findViewById(R.id.tab_view_pager);
		int viewPagerWidth = vp.getWidth();
		Bitmap bmp = BitmapFactory.decodeResource(act.getResources(),
				R.drawable.tab_blue_active);
		int tabBmpWidth = bmp.getWidth();
		int space = tabBmpWidth / 3 / 2;
		int tabCnt = vp.getAdapter().getCount();
		int wantWidth = 2 * vp.getAdapter().getCount() * space + tabCnt
				* tabBmpWidth;
		int padding = (int) ((viewPagerWidth * tabCnt - wantWidth) / tabCnt / 2.5f);
		vp.setPadding(padding, 0, padding, toPixcelFromdDip(5));
	}

	private static int toPixcelFromdDip(int dip) {
		float density = act.getResources().getDisplayMetrics().density;
		return (int) (dip * density + 0.5f);
	}

}
