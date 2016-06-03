package org.illegaller.ratabb.hishoot2i.model.tray;

import net.grandcentrix.tray.AppPreferences;

public class IntTray {
  private final AppPreferences mAppPreferences;
  private final String mKey;
  private final int mDefaultValue;

  public IntTray(AppPreferences tray, String key, int defaultValue) {
    this.mAppPreferences = tray;
    this.mKey = key;
    this.mDefaultValue = defaultValue;
  }

  public int getValue() {
    return mAppPreferences.getInt(mKey, mDefaultValue);
  }

  public void setValue(int value) {
    mAppPreferences.put(mKey, value);
  }
}
