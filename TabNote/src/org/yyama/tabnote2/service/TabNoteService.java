package org.yyama.tabnote2.service;

import java.util.ArrayList;
import java.util.List;

import org.yyama.tabnote2.R;
import org.yyama.tabnote2.dao.TblTabNoteDao;
import org.yyama.tabnote2.model.Tab;
import org.yyama.tabnote2.model.TabNote;

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
			tab.color = TabColorEnum.RED;
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
		return addTab(TabColorEnum.RED, "", value);
	}
	public static Tab addTab(TabColorEnum color, String title) {
		return addTab(color, title, "");
	}

	public static Tab addTab(TabColorEnum color, String title, String value) {
		Tab tab = new Tab();
		unActivateAll();
		tab.isActivate = true;
		tab.color = color;
		tab.title = title;
		tab.value = value;
		TabNote.tabs.add(tab);
		return tab;
	}

}
