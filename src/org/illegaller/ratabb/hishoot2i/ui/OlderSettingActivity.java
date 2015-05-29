package org.illegaller.ratabb.hishoot2i.ui;

import org.illegaller.ratabb.hishoot2i.Constants;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.util.DeviceUtil;
import org.illegaller.ratabb.hishoot2i.util.Pref;
import org.illegaller.ratabb.hishoot2i.util.SystemProp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class OlderSettingActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {
	private Pref mPref;
	private ListPreference LPimageQuality;
	private static boolean ischanghe, currentSingle, currentHideWm;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DeviceUtil.setTintSystemBar(this, true);
		addPreferencesFromResource(R.xml.oldsetting);
		mPref = new Pref(this);
		LPimageQuality = (ListPreference) findPreference(Constants.KEY_PREF_IMAGE_QUALITY);

		currentSingle = mPref.getSPref().getBoolean(
				Constants.KEY_PREF_SINGLE_SS, false);
		currentHideWm = mPref.getSPref().getBoolean(
				Constants.KEY_PREF_HIDE_WATTERMARK, false);
		((Preference) findPreference(Constants.KEY_PREF_TEMPLATE_WEB))
				.setIntent(new Intent("android.intent.action.VIEW", Uri
						.parse(Constants.TEMPLATE_WEB)));
	}

	@Override
	protected void onResume() {
		super.onResume();
		mPref.getSPref().registerOnSharedPreferenceChangeListener(this);
		updateSumPref(null);
	}

	@Override
	protected void onPause() {
		mPref.getSPref().unregisterOnSharedPreferenceChangeListener(this);
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

	@SuppressWarnings("deprecation")
	void updateSumPref(String pref) {
		if ((pref == null) || (pref.equals(Constants.KEY_PREF_IMAGE_QUALITY))) {
			LPimageQuality.setSummary(LPimageQuality.getEntry());
		}
		if ((pref == null) || (pref.equals(Constants.KEY_PREF_HIDE_WATTERMARK))) {

			CheckBoxPreference spHide = (CheckBoxPreference) findPreference(Constants.KEY_PREF_HIDE_WATTERMARK);
			if (spHide.isChecked()) {
				int t = SystemProp.getTrial(OlderSettingActivity.this);
				spHide.setSummaryOn("Trial count: " + t);
			}
			ischanghe |= (currentHideWm != mPref.getSPref().getBoolean(
					Constants.KEY_PREF_HIDE_WATTERMARK, false));
		}
		if (pref != null) {
			if (pref.equals(Constants.KEY_PREF_SINGLE_SS)) {
				ischanghe = (currentSingle != mPref.getSPref().getBoolean(
						Constants.KEY_PREF_SINGLE_SS, false));
			}
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		updateSumPref(key);
	}
}
