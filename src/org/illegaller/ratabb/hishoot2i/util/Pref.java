package org.illegaller.ratabb.hishoot2i.util;

import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Pref {
	private Context mContext;
	private SharedPreferences sp;
	private Editor editor;

	public Pref(Context context) {
		mContext = context;
		String prefName = mContext.getPackageName() + "_preferences";
		sp = mContext.getSharedPreferences(prefName, Context.MODE_PRIVATE);
		editor = sp.edit();
	}

	public SharedPreferences getSPref() {
		return sp;
	}

	@SuppressWarnings("unchecked")
	@SuppressLint("NewApi")
	public void putAndApply(String key, Object value) {

		if (value instanceof String) {
			editor.putString(key, (String) value);
		}
		if (value instanceof Boolean) {
			editor.putBoolean(key, (Boolean) value);
		}
		if (value instanceof Integer) {
			editor.putInt(key, (Integer) value);
		}
		if (value instanceof Float) {
			editor.putFloat(key, (Float) value);
		}
		if (value instanceof Long) {
			editor.putLong(key, (Long) value);
		}
		if (value instanceof Set<?>) {
			editor.putStringSet(key, (Set<String>) value);
		}
		editor.apply();
	}

	public void remove(String key) {
		editor.remove(key).commit();
	}
}
