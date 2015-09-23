package org.yyama.tabnote.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class TabPagerAdapter extends PagerAdapter {
	/** ���X�g. */
	private ArrayList<ImageView> mList;

	/**
	 * �w�肳�ꂽ�|�W�V�����̃A�C�e����Ԃ�
	 */
	public ImageView getItem(int position) {
		return mList.get(position);
	}

	/**
	 * �R���X�g���N�^.
	 */
	public TabPagerAdapter(Context context) {
		mList = new ArrayList<ImageView>();
	}

	/**
	 * ���X�g�ɃA�C�e����ǉ�����.
	 * 
	 * @param item
	 *            �A�C�e��
	 */
	public void add(ImageView item) {
		mList.add(item);
	}

	@Override
	public int getCount() {
		// ���X�g�̃A�C�e������Ԃ�
		return mList.size();
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		// ���X�g����擾
		ImageView item = mList.get(position);

		// �R���e�i�ɒǉ�
		container.addView(item);

		return item;
	}

	// View ���폜
	@Override
	public void destroyItem(ViewGroup oVg, int position, Object object) {
		oVg.removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == (ImageView) object;
	}

}
