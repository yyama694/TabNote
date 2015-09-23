package org.yyama.tabnote.activity;

import org.yyama.tabnote.R;
import org.yyama.tabnote.view.TabNoteView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

public class AppSettingPrefActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// PrefFragmentの呼び出し
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new PrefFragment()).commit();
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
