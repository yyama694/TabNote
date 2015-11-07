package org.yyama.tabnote2.activity;

import org.yyama.tabnote2.R;
import org.yyama.tabnote2.view.TabNoteView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

public class AppSettingPrefActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// PrefFragmentの呼び出し
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(android.R.id.content, new PrefFragment());
		ft.commit();
	}

	// 設定画面のPrefFragmentクラス
	public static class PrefFragment extends PreferenceFragment {
		private SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			public void onSharedPreferenceChanged(SharedPreferences sp,
					String key) {
				reloadSummary();
			}
		};

		private void reloadSummary() {
			// underlineのサマリーを変更する
			CheckBoxPreference cp = (CheckBoxPreference) getPreferenceScreen()
					.findPreference("underlines");
			cp.setSummary(cp.isChecked() ? "true" : "false");
			// font sizeのサマリーを変更する
			ListPreference lp = (ListPreference) getPreferenceScreen()
					.findPreference("fontSize");
			lp.setSummary(lp.getEntry());

			// タブの再描画
//			TabNoteView.draw(false);
			// ViewPagerの再描画
			TabNoteView.setMainViewPager();
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.app_setting_pref);
			reloadSummary();
		}

		// 設定値が変更されたときのリスナーを登録
		@Override
		public void onResume() {
			super.onResume();
			getPreferenceScreen().getSharedPreferences()
					.registerOnSharedPreferenceChangeListener(listener);
		}

		// 設定値が変更されたときのリスナー登録を解除
		@Override
		public void onPause() {
			super.onPause();
			getPreferenceScreen().getSharedPreferences()
					.unregisterOnSharedPreferenceChangeListener(listener);
		}

	}
}
