package org.illegaller.ratabb.hishoot2i.ui;

import org.illegaller.ratabb.hishoot2i.Constants;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.util.DeviceUtil;
import org.illegaller.ratabb.hishoot2i.util.Pref;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

public class OlderSettingActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {
	private SharedPreferences mPref;
	private ListPreference LPimageQuality;
	private static boolean ischanghe;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DeviceUtil.setTintSystemBar(this, true);
		addPreferencesFromResource(R.xml.oldsetting);
		mPref = Pref.getPref(this);
		LPimageQuality = (ListPreference) findPreference(Constants.KEY_PREF_IMAGE_QUALITY);

		ischanghe = false;
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
		if (ischanghe) {
			Intent intent = new Intent(Constants.ACTION_UPDATE);
			sendBroadcast(intent);
		}
		super.onDestroy();
	}

	void updateSumPref(String pref) {
		if ((pref == null) || (pref.equals(Constants.KEY_PREF_IMAGE_QUALITY))) {
			LPimageQuality.setSummary(LPimageQuality.getEntry());
		}

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		ischanghe = true;
		updateSumPref(key);
	}
}
