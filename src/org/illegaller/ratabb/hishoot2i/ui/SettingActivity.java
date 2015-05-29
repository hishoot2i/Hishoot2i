package org.illegaller.ratabb.hishoot2i.ui;

import org.illegaller.ratabb.hishoot2i.Constants;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.util.DeviceUtil;
import org.illegaller.ratabb.hishoot2i.util.Pref;
import org.illegaller.ratabb.hishoot2i.util.SystemProp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

@SuppressLint("NewApi")
public class SettingActivity extends AppCompatActivity {
	private Pref mPref;
	private boolean ischanghe, currentBlur, currentSingle, currentHideWm;
	private int currentBlurRad;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPref = new Pref(this);

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

			currentBlur = mPref.getSPref().getBoolean(
					Constants.KEY_PREF_BLUR_BG, false);
			currentBlurRad = mPref.getSPref().getInt(
					Constants.KEY_PREF_BLUR_RADIUS, 5);

			currentSingle = mPref.getSPref().getBoolean(
					Constants.KEY_PREF_SINGLE_SS, false);
			currentHideWm = mPref.getSPref().getBoolean(
					Constants.KEY_PREF_HIDE_WATTERMARK, false);

			((Preference) findPreference(Constants.KEY_PREF_TEMPLATE_WEB))
					.setIntent(new Intent("android.intent.action.VIEW", Uri
							.parse(Constants.TEMPLATE_WEB)));

		}

		@Override
		public void onResume() {
			super.onResume();
			mPref.getSPref().registerOnSharedPreferenceChangeListener(
					SettingFragment.this);
			updateSumPref(null);

		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			mPref.getSPref().unregisterOnSharedPreferenceChangeListener(
					SettingFragment.this);

		}

		@Override
		public void onPause() {
			mPref.getSPref().unregisterOnSharedPreferenceChangeListener(
					SettingFragment.this);
			super.onPause();
		}

		void updateSumPref(String pref) {
			if ((pref == null)
					|| (pref.equals(Constants.KEY_PREF_IMAGE_QUALITY))) {
				LPimageQuality.setSummary(LPimageQuality.getEntry());
			}
			if ((pref == null)
					|| (pref.equals(Constants.KEY_PREF_HIDE_WATTERMARK))) {

				SwitchPreference spHide = (SwitchPreference) findPreference(Constants.KEY_PREF_HIDE_WATTERMARK);
				if (spHide.isChecked()) {
					int t = SystemProp.getTrial(SettingActivity.this);
					spHide.setSummaryOn("Trial count: " + t);
				}
				ischanghe |= (currentHideWm != mPref.getSPref().getBoolean(
						Constants.KEY_PREF_HIDE_WATTERMARK, false));
			}
			if (pref != null) {
				if ((pref.equals(Constants.KEY_PREF_BLUR_BG))
						|| (pref.equals(Constants.KEY_PREF_SINGLE_SS))
						|| (pref.equals(Constants.KEY_PREF_BLUR_RADIUS))) {
					ischanghe =

					(currentBlur != mPref.getSPref().getBoolean(
							Constants.KEY_PREF_BLUR_BG, false))

							|| (currentSingle != mPref.getSPref().getBoolean(
									Constants.KEY_PREF_SINGLE_SS, false))
							|| (currentBlurRad != mPref.getSPref().getInt(
									Constants.KEY_PREF_BLUR_RADIUS, 5))

					;
				}

			}
		}

		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			updateSumPref(key);
		}
	}

}
