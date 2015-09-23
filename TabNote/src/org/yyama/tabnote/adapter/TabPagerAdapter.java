package org.yyama.tabnote.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class TabPagerAdapter extends PagerAdapter {
	/** リスト. */
	private ArrayList<ImageView> mList;

	/**
	 * 指定されたポジションのアイテムを返す
	 */
	public ImageView getItem(int position) {
		return mList.get(position);
	}

	/**
	 * コンストラクタ.
	 */
	public TabPagerAdapter(Context context) {
		mList = new ArrayList<ImageView>();
	}

	/**
	 * リストにアイテムを追加する.
	 * 
	 * @param item
	 *            アイテム
	 */
	public void add(ImageView item) {
		mList.add(item);
	}

	@Override
	public int getCount() {
		// リストのアイテム数を返す
		return mList.size();
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		// リストから取得
		ImageView item = mList.get(position);

		// コンテナに追加
		container.addView(item);

		return item;
	}

	// View を削除
	@Override
	public void destroyItem(ViewGroup oVg, int position, Object object) {
		oVg.removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == (ImageView) object;
	}

}
