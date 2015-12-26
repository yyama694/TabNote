package org.yyama.tabnote2.service;

import static org.yyama.tabnote2.R.drawable.*;

public enum TabColorEnum {
	RED(0, tab_red_active, under_tab_line_red), ORANGE(1, tab_orange_active,
			under_tab_line_orange), GREEN(2, tab_green_active,
			under_tab_line_green), BLUE(3, tab_blue_active, under_tab_line_blue), PURPLE(
			4, tab_purple_active, under_tab_line_purple);
	public final int key;
	public final int tabImageId;
	public final int underlineImageId;

	private TabColorEnum(int key, int tabImageId, int underlineImageId) {
		this.key = key;
		this.tabImageId = tabImageId;
		this.underlineImageId = underlineImageId;
	}

	// キーからEnumを返す。
	public static TabColorEnum getTabColorEnumFromKey(int key) {
		for (TabColorEnum en : TabColorEnum.values()) {
			if (key == en.key) {
				return en;
			}
		}
		return TabColorEnum.RED;
	}

	// タブイメージIDからEnumを返す。リファクタリンヅ完了までの一時的なメソッドになると思う。たぶん。
	public static TabColorEnum getTabColorEnumFromImageId(int imegeId) {
		for (TabColorEnum en : TabColorEnum.values()) {
			if (imegeId == en.tabImageId) {
				return en;
			}
		}
		return TabColorEnum.RED;
	}

}
