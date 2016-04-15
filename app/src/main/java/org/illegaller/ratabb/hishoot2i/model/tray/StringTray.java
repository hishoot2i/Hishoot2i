package org.illegaller.ratabb.hishoot2i.model.tray;

import net.grandcentrix.tray.AppPreferences;

public class StringTray {
    private final AppPreferences tray;
    private final String key;
    private final String defaultValue;

    public StringTray(AppPreferences tray, String key, String defaultValue) {
        this.tray = tray;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String get() {
        return tray.getString(key, defaultValue);
    }

    public void set(String value) {
        tray.put(key, value);
    }
}
