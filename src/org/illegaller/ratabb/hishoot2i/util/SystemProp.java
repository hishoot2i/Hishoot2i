package org.illegaller.ratabb.hishoot2i.util;

import org.illegaller.ratabb.hishoot2i.Constants;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class SystemProp {
	private static String SETTING_TRIAL_COUNT = Constants.SETTING_TRIAL_COUNT;
	private static int MAX_TRIAL = 50;

	public static int getOrPutTrial(Context c) {
		ContentResolver cr = c.getContentResolver();
		int result = Settings.System.getInt(cr, SETTING_TRIAL_COUNT, -1);
		if (result == -1) {
			putTrial(cr, MAX_TRIAL);
			result = MAX_TRIAL;
		} else {
			if (--result >= 0) {
				result = (result > MAX_TRIAL) ? MAX_TRIAL : result;
				putTrial(cr, result);
			}
		}
		return result;
	}

	public static int getTrial(Context c) {
		int ret = 0;
		try {
			ret = Settings.System.getInt(c.getContentResolver(),
					SETTING_TRIAL_COUNT);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
			ret = Settings.System.getInt(c.getContentResolver(),
					SETTING_TRIAL_COUNT, getOrPutTrial(c));
		}
		return ret;
	}

	public static boolean putTrial(ContentResolver cr, int i) {
		return Settings.System.putInt(cr, SETTING_TRIAL_COUNT, i);
	}

	public static boolean resTrial(ContentResolver cr) {
		return Settings.System.putInt(cr, SETTING_TRIAL_COUNT, MAX_TRIAL);
	}
}
