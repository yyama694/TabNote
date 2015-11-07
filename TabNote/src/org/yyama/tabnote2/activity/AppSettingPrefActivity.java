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
		// PrefFragment�̌Ăяo��
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(android.R.id.content, new PrefFragment());
		ft.commit();
	}

	// �ݒ��ʂ�PrefFragment�N���X
	public static class PrefFragment extends PreferenceFragment {
		private SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			public void onSharedPreferenceChanged(SharedPreferences sp,
					String key) {
				reloadSummary();
			}
		};

		private void reloadSummary() {
			// underline�̃T�}���[��ύX����
			CheckBoxPreference cp = (CheckBoxPreference) getPreferenceScreen()
					.findPreference("underlines");
			cp.setSummary(cp.isChecked() ? "true" : "false");
			// font size�̃T�}���[��ύX����
			ListPreference lp = (ListPreference) getPreferenceScreen()
					.findPreference("fontSize");
			lp.setSummary(lp.getEntry());

			// �^�u�̍ĕ`��
//			TabNoteView.draw(false);
			// ViewPager�̍ĕ`��
			TabNoteView.setMainViewPager();
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.app_setting_pref);
			reloadSummary();
		}

		// �ݒ�l���ύX���ꂽ�Ƃ��̃��X�i�[��o�^
		@Override
		public void onResume() {
			super.onResume();
			getPreferenceScreen().getSharedPreferences()
					.registerOnSharedPreferenceChangeListener(listener);
		}

		// �ݒ�l���ύX���ꂽ�Ƃ��̃��X�i�[�o�^������
		@Override
		public void onPause() {
			super.onPause();
			getPreferenceScreen().getSharedPreferences()
					.unregisterOnSharedPreferenceChangeListener(listener);
		}

	}
}
