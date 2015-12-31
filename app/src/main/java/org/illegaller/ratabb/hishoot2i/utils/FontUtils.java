package org.illegaller.ratabb.hishoot2i.utils;

import android.graphics.Typeface;

import java.io.File;

public class FontUtils {
    private static Typeface sBadgeTypeface = Typeface.create("sans-serif-black", Typeface.BOLD);

    protected FontUtils() {
        throw new AssertionError("no construction");
    }

    public static Typeface getBadgeTypeface() {
        return sBadgeTypeface;
    }

    public static void setBadgeTypeface(final String path) {
        sBadgeTypeface = Typeface.createFromFile(path);
    }

    public static void setBadgeTypeface(final File file) {
        sBadgeTypeface = Typeface.createFromFile(file);
    }

    public static void setBadgeTypefaceDefault() {
        sBadgeTypeface = Typeface.create("sans-serif-black", Typeface.BOLD);
    }

    public static void setBadgeTypeface(final Typeface typeface) {
        sBadgeTypeface = typeface;
    }
}
