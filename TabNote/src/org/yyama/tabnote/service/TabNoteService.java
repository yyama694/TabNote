package org.yyama.tabnote.service;

import java.util.ArrayList;
import java.util.List;

import org.yyama.tabnote.R;
import org.yyama.tabnote.dao.TblTabNoteDao;
import org.yyama.tabnote.model.Tab;
import org.yyama.tabnote.model.TabNote;

import android.app.Activity;

public class TabNoteService {
	private TabNoteService() {
	}

	public static void init(Activity act) {
		List<Tab> list = TblTabNoteDao.selectAll();
		if (list.size() == 0) {
			// TabÇ™DBÇ…ìoò^Ç≥ÇÍÇƒÇ¢Ç»Ç¢èÍçá
			Tab tab = new Tab();
			tab.title = act.getString(R.string.tab_title);
			tab.isActivate = true;
			tab.tabImageId = R.drawable.tab_red_active;
			tab.tabUnderLineImageId = R.drawable.under_tab_line_red;
			tab.value = act.getString(R.string.double_tap_description);
			tab.id = TblTabNoteDao.insert(tab, 0);
			list.add(tab);
		} else {
		}
		TabNote.tabs = list;
	}

	public static List<Integer> getColorIdAllKind() {
		List<Integer> list = new ArrayList<>();
		list.add(R.drawable.tab_red_active);
		list.add(R.drawable.tab_orange_active);
		list.add(R.drawable.tab_green_active);
		list.add(R.drawable.tab_blue_active);
		list.add(R.drawable.tab_purple_active);
		return list;
	}

	public static int getUnderLineImageIdFromTabImageId(int tabImageId) {
		switch (tabImageId) {
		case R.drawable.tab_blue_active:
			return R.drawable.under_tab_line_blue;
		case R.drawable.tab_green_active:
			return R.drawable.under_tab_line_green;
		case R.drawable.tab_orange_active:
			return R.drawable.under_tab_line_orange;
		case R.drawable.tab_purple_active:
			return R.drawable.under_tab_line_purple;
		case R.drawable.tab_red_active:
			return R.drawable.under_tab_line_red;
		default:
			return 0;
		}
	}

	public static void unActivateAll() {
		for (Tab tab : TabNote.tabs) {
			tab.isActivate = false;
		}
	}

	public static void delete(Tab tab) {
		unActivateAll();
		int i = TabNote.delete(tab);
		if (TabNote.tabs.size() != 0) {
			if (i == 0) {
				TabNote.tabs.get(i).isActivate = true;
			} else {
				TabNote.tabs.get(i - 1).isActivate = true;
			}
		}
		TblTabNoteDao.delete(tab);
		TblTabNoteDao.updateOrder();
	}

	public static void toLeft(Tab tab) {
		int order = TabNote.getTabOrder(tab);
		Tab tmp = TabNote.tabs.get(order - 1);
		TabNote.tabs.remove(order - 1);
		TabNote.tabs.add(order, tmp);
	}

	public static void toRight(Tab tab) {
		int order = TabNote.getTabOrder(tab);
		Tab tmp = TabNote.tabs.get(order + 1);
		TabNote.tabs.remove(order + 1);
		TabNote.tabs.add(order, tmp);
	}

	public static Tab addTab(String value) {
		return addTab(R.drawable.tab_red_active, "", value);
	}
	public static Tab addTab(int ColorId, String title) {
		return addTab(ColorId, title, "");
	}

	public static Tab addTab(int ColorId, String title, String value) {
		Tab tab = new Tab();
		unActivateAll();
		tab.isActivate = true;
		tab.tabImageId = ColorId;
		tab.tabUnderLineImageId = TabNoteService
				.getUnderLineImageIdFromTabImageId(tab.tabImageId);
		tab.title = title;
		tab.value = value;
		TabNote.tabs.add(tab);
		return tab;
	}

}
