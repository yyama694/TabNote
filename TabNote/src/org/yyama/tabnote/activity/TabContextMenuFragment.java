package org.yyama.tabnote.activity;

import org.yyama.tabnote.R;
import org.yyama.tabnote.dao.TblTabNoteDao;
import org.yyama.tabnote.model.Tab;
import org.yyama.tabnote.model.TabNote;
import org.yyama.tabnote.service.TabNoteService;
import org.yyama.tabnote.view.TabNoteView;
import org.yyama.tabnote.view.TabUpdateDialogView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class TabContextMenuFragment extends DialogFragment implements
		OnClickListener, android.content.DialogInterface.OnClickListener {
	Tab tab;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.tab_context_menu_activity, null);
		builder.setView(view);
		view.findViewById(R.id.tab_setting).setOnClickListener(this);
		view.findViewById(R.id.tab_delete).setOnClickListener(this);
		view.findViewById(R.id.tab_to_left).setOnClickListener(this);
		view.findViewById(R.id.tab_to_right).setOnClickListener(this);
		view.findViewById(R.id.tab_share).setOnClickListener(this);

		// タイトルを作成する
		tab = (Tab) getArguments().get("tab");
		TextView tv = (TextView) view.findViewById(R.id.tab_context_menu_title);
		tv.setCompoundDrawablesWithIntrinsicBounds(tab.tabImageId, 0, 0, 0);
		if (tab.title == null || tab.title.equals("")) {
			tv.setText(getString(R.string.tab_menu));
		} else {
			tv.setText(tab.title + " " + getString(R.string.tab_menu));
		}

		// 一番左のタブの場合、これ以上左にはいけない
		if (TabNote.getTabOrder(tab) == 0) {
			tv = (TextView) view.findViewById(R.id.tab_to_left);
			tv.setEnabled(false);
		}

		// 一番右のタブの場合、これ以上右にはいけない
		if (TabNote.getTabOrder(tab) == TabNote.tabs.size() - 1) {
			tv = (TextView) view.findViewById(R.id.tab_to_right);
			tv.setEnabled(false);
		}

		return builder.create();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tab_setting:
			TabUpdateDialogView.setIsAdd(false);
			DialogFragment df = new TabUpdateFragment();
			Bundle bundle = new Bundle();
			bundle.putSerializable("tab", tab);
			df.setArguments(bundle);
			df.show(getActivity().getSupportFragmentManager(), "");
			dismiss();
			break;
		case R.id.tab_delete:
			new AlertDialog.Builder(getActivity())
					.setTitle(getString(R.string.confirm))
					.setMessage(getString(R.string.confirm_delete))
					.setPositiveButton(getString(android.R.string.ok), this)
					.setNegativeButton(getString(android.R.string.cancel), null)
					.show();
			dismiss();
			break;
		case R.id.tab_to_left:
			TabNoteView.saveValueAll();
			TabNoteService.toLeft(tab);
			TabNoteView.setMainViewPager();
			TblTabNoteDao.updateOrder();
			TabNoteView.draw();
			dismiss();
			break;
		case R.id.tab_to_right:
			TabNoteView.saveValueAll();
			TabNoteService.toRight(tab);
			TabNoteView.setMainViewPager();
			TblTabNoteDao.updateOrder();
			TabNoteView.draw();
			dismiss();
			break;
		case R.id.tab_share:
			TabNoteView.saveValueAll();
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_TEXT, tab.value);
			startActivity(intent);
			dismiss();
			break;
		default:
			break;
		}

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			TabNoteService.delete(tab);
			TabNoteView.saveValueAll();
			TabNoteView.setMainViewPager();
			TabNoteView.draw();
			break;
		default:
			break;
		}
	}

}
