package org.illegaller.ratabb.hishoot2i.model.tray;

import net.grandcentrix.tray.AppPreferences;

public class StringTray {
  private final AppPreferences mAppPreferences;
  private final String mKey;
  private final String mDefaultValue;

  public StringTray(AppPreferences tray, String key, String defaultValue) {
    this.mAppPreferences = tray;
    this.mKey = key;
    this.mDefaultValue = defaultValue;
  }

  public String getValue() {
    return mAppPreferences.getString(mKey, mDefaultValue);
  }

  public void setValue(String value) {
    mAppPreferences.put(mKey, value);
  }
}
