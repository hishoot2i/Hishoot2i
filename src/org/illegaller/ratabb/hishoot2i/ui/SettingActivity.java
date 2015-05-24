package org.illegaller.ratabb.hishoot2i.ui;

import org.illegaller.ratabb.hishoot2i.Constants;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.util.DeviceUtil;
import org.illegaller.ratabb.hishoot2i.util.Pref;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

@SuppressLint("NewApi")
public class SettingActivity extends AppCompatActivity {
	private SharedPreferences mPref;
	@SuppressWarnings("unused")
	private static final String TAG = "Hishoot2i:SettingFragment";
	private static boolean ischanghe;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPref = Pref.getPref(this);

		DeviceUtil.setTintSystemBar(this, false);

		getSupportActionBar().setTitle(R.string.menu_setting);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, (new SettingFragment()))
				.commit();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		if (ischanghe) {
			Intent intent = new Intent(Constants.ACTION_UPDATE);
			sendBroadcast(intent);
		}
		super.onDestroy();
	}

	private class SettingFragment extends PreferenceFragment implements
			OnSharedPreferenceChangeListener {

		private ListPreference LPimageQuality;

		public SettingFragment() {
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			ischanghe = false;
			addPreferencesFromResource(R.xml.setting);
			LPimageQuality = (ListPreference) findPreference(Constants.KEY_PREF_IMAGE_QUALITY);
		}

		@Override
		public void onResume() {
			super.onResume();
			mPref.registerOnSharedPreferenceChangeListener(SettingFragment.this);

			updateSumPref(null);

		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			mPref.unregisterOnSharedPreferenceChangeListener(SettingFragment.this);

		}

		@Override
		public void onPause() {
			mPref.unregisterOnSharedPreferenceChangeListener(SettingFragment.this);
			super.onPause();
		}

		void updateSumPref(String pref) {
			if ((pref == null)
					|| (pref.equals(Constants.KEY_PREF_IMAGE_QUALITY))) {
				LPimageQuality.setSummary(LPimageQuality.getEntry());
			}

		}

		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			// XXX
			ischanghe = true;
			updateSumPref(key);
		}
	}

}
