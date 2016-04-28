package org.illegaller.ratabb.hishoot2i.model.tray;

import net.grandcentrix.tray.AppPreferences;

public class BooleanTray {
  private final AppPreferences tray;
  private final String key;
  private final boolean defaultValue;

  public BooleanTray(AppPreferences tray, String key, boolean defaultValue) {
    this.tray = tray;
    this.key = key;
    this.defaultValue = defaultValue;
  }

  public boolean get() {
    return tray.getBoolean(key, defaultValue);
  }

  public void set(boolean value) {
    tray.put(key, value);
  }
}
