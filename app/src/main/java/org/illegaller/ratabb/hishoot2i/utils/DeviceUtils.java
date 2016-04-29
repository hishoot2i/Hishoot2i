package org.illegaller.ratabb.hishoot2i.utils;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.WindowManager;
import java.util.Locale;

import static android.os.Build.VERSION.RELEASE;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

public class DeviceUtils {
  private DeviceUtils() { /*no instance*/ }

  /*********** StatusBar ***********/
  @TargetApi(LOLLIPOP) public static void setTransparentStatusBar(@NonNull final Window window) {
    @ColorInt int transparent = android.R.color.transparent;
    if (DeviceUtils.isCompatible(LOLLIPOP)) {
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      window.setStatusBarColor(transparent);
    }
  }

  public static int getDeviceHeight() {
    return Resources.getSystem().getDisplayMetrics().heightPixels;
  }

  public static int getDeviceWidth() {
    return Resources.getSystem().getDisplayMetrics().widthPixels;
  }

  public static String getDeviceName() {
    final String manufacturer = Build.MANUFACTURER;
    final String model = Build.MODEL;
    if (model.startsWith(manufacturer)) {
      return DeviceUtils.capitalize(model);
    } else {
      return String.format("%s %s", capitalize(manufacturer), model);
    }
  }

  public static String getDeviceOS() {
    return String.format(Locale.US, "Android %s [sdk%d]", RELEASE, SDK_INT);
  }

  public static String capitalize(String string) {
    if (null == string || string.length() == 0) return "";
    char firstChar = string.charAt(0);
    if (Character.isUpperCase(firstChar)) {
      return string;
    } else {
      return Character.toUpperCase(firstChar) + string.substring(1);
    }
  }

  /******* Build.VERSION ******/
  public static boolean isCompatible(int apiLevel) {
    return android.os.Build.VERSION.SDK_INT >= apiLevel;
  }
}
