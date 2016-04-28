package org.illegaller.ratabb.hishoot2i.utils;

import android.graphics.Typeface;
import java.io.File;

public class FontUtils {
  private static Typeface sBadgeTypeface = sDefault();

  private FontUtils() { /*no instance*/ }

  public static Typeface getBadgeTypeface() {
    return sBadgeTypeface;
  }

  public static void setBadgeTypeface(final File file) {
    if (!file.exists() && !file.canRead()) return;
    try {
      sBadgeTypeface = Typeface.createFromFile(file);
    } catch (Exception e) {
      CrashLog.logError("FontUtils: " + file.getName(), e);
      FontUtils.setBadgeTypefaceDefault();
    }
  }

  public static void setBadgeTypefaceDefault() {
    sBadgeTypeface = sDefault();
  }

  private static Typeface sDefault() {
    return Typeface.create("sans-serif-black", Typeface.BOLD);
  }
}
