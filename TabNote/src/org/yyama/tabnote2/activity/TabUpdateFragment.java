package org.yyama.tabnote2.activity;

import java.util.List;

import org.yyama.tabnote2.R;
import org.yyama.tabnote2.adapter.TabPagerAdapter;
import org.yyama.tabnote2.dao.TblTabDao;
import org.yyama.tabnote2.model.Tab;
import org.yyama.tabnote2.model.TabNote;
import org.yyama.tabnote2.service.TabColorEnum;
import org.yyama.tabnote2.service.TabNoteService;
import org.yyama.tabnote2.view.TabNoteView;
import org.yyama.tabnote2.view.TabUpdateDialogView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;

public class TabUpdateFragment extends DialogFragment implements
		OnClickListener {
	private ViewPager vp;
	private EditText tabTitle;
	private List<Integer> colorIds;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.update_tab_layout, null);
		TabUpdateDialogView.setView(view);
		TabUpdateDialogView.setSetting(getArguments());
		builder.setView(view);

		// view�B���C���X�^���X�ϐ��ɕێ�����
		vp = (ViewPager) view.findViewById(R.id.tab_view_pager);
		tabTitle = (EditText) view.findViewById(R.id.tab_title_edit);

		// viewPager�̐ݒ�
		TabPagerAdapter tpa = new TabPagerAdapter(getActivity());
		colorIds = TabNoteService.getColorIdAllKind();
		for (Integer i : colorIds) {
			ImageView iv = new ImageView(getActivity());
			iv.setImageResource(i);
			tpa.add(iv);
		}
		vp.setAdapter(tpa);

		// �ύX�̏ꍇ�A���݂̃^�u�̐F��I����Ԃɂ���
		Bundle bundle = getArguments();
		if (bundle != null) {
			Tab tab = (Tab) bundle.get("tab");
			for (int i = 0; i < colorIds.size(); i++) {
				if (colorIds.get(i) == tab.color.tabImageId) {
					vp.setCurrentItem(i);
					break;
				}
			}
		}

		// ���X�i�[���Z�b�g����
		view.findViewById(R.id.update_tab_cancel).setOnClickListener(this);
		view.findViewById(R.id.update_tab_ok).setOnClickListener(this);

		return builder.create();
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
		ViewTreeObserver observer = vp.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				// viewPager�̃p�f�B���O�T�C�Y�����߂�
				TabUpdateDialogView.setViewPagerPadding();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.update_tab_cancel:
			dismiss();
			break;
		case R.id.update_tab_ok:
			Bundle bundle = getArguments();
			TabNoteService.unActivateAll();
			if (bundle == null) {
				// �^�u��ǉ�����
				Tab tab = TabNoteService.addTab(TabColorEnum
						.getTabColorEnumFromKey(vp.getCurrentItem()), tabTitle
						.getText().toString());
				TabNoteView.addMainViewPager(tab.value);
				// DB�ɒǉ�����
				tab.id = TblTabDao.insert(tab, TabNote.tabs.size() - 1);
			} else {
				Tab tab = (Tab) bundle.get("tab");
				// �^�u��ύX����
				tab.isActivate = true;
				tab.color = TabColorEnum.getTabColorEnumFromKey(vp
						.getCurrentItem());
				tab.title = tabTitle.getText().toString();
				// DB��ύX����
				TblTabDao.update(tab);
			}
			TabNoteView.draw(true);
			dismiss();
		default:
			break;
		}
	}
}
