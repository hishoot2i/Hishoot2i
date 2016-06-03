package org.illegaller.ratabb.hishoot2i.model.tray;

import net.grandcentrix.tray.AppPreferences;

public class BooleanTray {
  private final AppPreferences mAppPreferences;
  private final String mKey;
  private final boolean mDefaultValue;

  public BooleanTray(AppPreferences tray, String key, boolean defaultValue) {
    this.mAppPreferences = tray;
    this.mKey = key;
    this.mDefaultValue = defaultValue;
  }

  public boolean isValue() {
    return mAppPreferences.getBoolean(mKey, mDefaultValue);
  }

  public void setValue(boolean value) {
    mAppPreferences.put(mKey, value);
  }
}
