package org.illegaller.ratabb.hishoot2i.ui;

import org.illegaller.ratabb.hishoot2i.Constants;
import org.illegaller.ratabb.hishoot2i.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

public class OlderSettingActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {
	private SharedPreferences mPref;
	private ListPreference LPimageQuality;
	private CheckBoxPreference cbPrefSingle, cbPrefBlur;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.oldsetting);
		mPref = getSharedPreferences(getPackageName() + "_preferences",
				Context.MODE_PRIVATE);
		LPimageQuality = (ListPreference) findPreference(Constants.KEY_PREF_IMAGE_QUALITY);
		cbPrefSingle = (CheckBoxPreference) findPreference(Constants.KEY_PREF_SINGLE_SS);
		cbPrefBlur = (CheckBoxPreference) findPreference(Constants.KEY_PREF_BLUR_BG);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mPref.registerOnSharedPreferenceChangeListener(this);
		updateSumPref(null);
	}

	@Override
	protected void onPause() {
		mPref.unregisterOnSharedPreferenceChangeListener(this);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		Intent intent = new Intent(Constants.ACTION_UPDATE);
		sendBroadcast(intent);
		super.onDestroy();
	}

	void updateSumPref(String pref) {
		if ((pref == null) || (pref.equals(Constants.KEY_PREF_IMAGE_QUALITY))) {
			LPimageQuality.setSummary(LPimageQuality.getEntry());
		}
		if ((pref == null) || (pref.equals(Constants.KEY_PREF_SINGLE_SS))) {
			boolean single = cbPrefSingle.isChecked();
			if (!single) {
				mPref.edit().putBoolean(Constants.KEY_PREF_BLUR_BG, false)
						.apply();
				cbPrefBlur.setChecked(false);
			}
		}

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		updateSumPref(key);
	}
}
