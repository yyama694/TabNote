package org.yyama.tabnote.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class MainPagerAdapter extends PagerAdapter {
	List<LinearLayout> list = new ArrayList<>();

	public void setList(List<LinearLayout> list) {
		this.list = list;
	}

	public void addView(LinearLayout tab) {
		list.add(tab);
	}

	public void removeView(int i){
		list.remove(i);
	}
	
	public LinearLayout getView(int i) {
		return list.get(i);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(list.get(position));
		return list.get(position);
	}

	@Override
	public void destroyItem(ViewGroup oVg, int position, Object object) {
		oVg.removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == (LinearLayout) object;
	}
}
