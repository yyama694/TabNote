package org.yyama.tabnote.activity;

import java.util.List;

import org.yyama.tabnote.R;
import org.yyama.tabnote.adapter.TabPagerAdapter;
import org.yyama.tabnote.dao.TblTabNoteDao;
import org.yyama.tabnote.model.Tab;
import org.yyama.tabnote.model.TabNote;
import org.yyama.tabnote.service.TabNoteService;
import org.yyama.tabnote.view.TabNoteView;
import org.yyama.tabnote.view.TabUpdateDialogView;

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

		// view達をインスタンス変数に保持する
		vp = (ViewPager) view.findViewById(R.id.tab_view_pager);
		tabTitle = (EditText) view.findViewById(R.id.tab_title_edit);

		// viewPagerの設定
		TabPagerAdapter tpa = new TabPagerAdapter(getActivity());
		colorIds = TabNoteService.getColorIdAllKind();
		for (Integer i : colorIds) {
			ImageView iv = new ImageView(getActivity());
			iv.setImageResource(i);
			tpa.add(iv);
		}
		vp.setAdapter(tpa);

		// 変更の場合、現在のタブの色を選択状態にする
		Bundle bundle = getArguments();
		if (bundle != null) {
			Tab tab = (Tab) bundle.get("tab");
			for (int i = 0; i < colorIds.size(); i++) {
				if (colorIds.get(i) == tab.tabImageId) {
					vp.setCurrentItem(i);
					break;
				}
			}
		}

		// リスナーをセットする
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
				// viewPagerのパディングサイズを決める
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
				// タブを追加する
				Tab tab = TabNoteService.addTab(colorIds.get(vp
						.getCurrentItem()), tabTitle.getText().toString());
				TabNoteView.addMainViewPager(tab.value);
				// DBに追加する
				tab.id = TblTabNoteDao.insert(tab, TabNote.tabs.size() - 1);
			} else {
				Tab tab = (Tab) bundle.get("tab");
				// タブを変更する
				tab.isActivate = true;
				tab.tabImageId = colorIds.get(vp.getCurrentItem());
				tab.tabUnderLineImageId = TabNoteService
						.getUnderLineImageIdFromTabImageId(tab.tabImageId);
				tab.title = tabTitle.getText().toString();
				// DBを変更数する
				TblTabNoteDao.update(tab);
			}
			TabNoteView.draw();
			dismiss();
		default:
			break;
		}
	}
}
