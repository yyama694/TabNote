package org.yyama.tabnote.model;

import java.util.ArrayList;
import java.util.List;

public class TabNote {
	public static List<Tab> tabs = new ArrayList<>();
	public static int valueTextSize;
	
	private TabNote() {
	}

	public static int getActiveNum() {
		for (int i = 0; i < tabs.size(); i++) {
			if(tabs.get(i).isActivate){
				return i;
			}
		}
		return 0;
	}

	public static int delete(Tab tab) {
		for (int i = 0; i < tabs.size(); i++) {
			if(tab == tabs.get(i)){
				tabs.remove(i);
				return i;
			}
		}
		return 0;
	}

	public static int getTabOrder(Tab tab){
		for (int i = 0; i < tabs.size(); i++) {
			if(tab==tabs.get(i)){
				return i;
			}
		}
		return -1;
	}
}
