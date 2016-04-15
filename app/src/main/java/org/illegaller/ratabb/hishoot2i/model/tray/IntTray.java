package org.illegaller.ratabb.hishoot2i.model.tray;

import net.grandcentrix.tray.AppPreferences;

public class IntTray {
    private final AppPreferences tray;
    private final String key;
    private final int defaultValue;

    public IntTray(AppPreferences tray, String key, int defaultValue) {
        this.tray = tray;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public int get() {
        return tray.getInt(key, defaultValue);
    }

    public void set(int value) {
        tray.put(key, value);
    }
}
