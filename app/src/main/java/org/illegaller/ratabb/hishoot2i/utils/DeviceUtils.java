package org.illegaller.ratabb.hishoot2i.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Method;
import java.util.Locale;

import static android.os.Build.VERSION.RELEASE;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.GINGERBREAD;
import static android.os.Build.VERSION_CODES.HONEYCOMB;
import static android.os.Build.VERSION_CODES.HONEYCOMB_MR1;
import static android.os.Build.VERSION_CODES.HONEYCOMB_MR2;
import static android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;
import static android.os.Build.VERSION_CODES.JELLY_BEAN;
import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR1;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

public class DeviceUtils {
    private DeviceUtils() {        //no instance
    }

    /*********** StatusBar ***********/
    @TargetApi(LOLLIPOP) public static void setTransparentStatusBar(@NonNull final Window window) {
        @ColorInt int transparent = android.R.color.transparent;
        if (isLollipop()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(transparent);
        }
    }

    @TargetApi(JELLY_BEAN_MR1) public static int getDeviceHeight(final Display display) {
        if (isJellyBeanMR1()) return getDisplayMetrics(display).heightPixels;
        else if (isHoneycombMR2()) {
            try {
                Method mGetRawH = Display.class.getMethod("getRawHeight");
                return (Integer) mGetRawH.invoke(display);
            } catch (Exception e) {
                Log.e("DeviceUtils", "getRawHeight reflection", e);
                return 0;
            }
        } else //noinspection deprecation
            return display.getHeight();
    }

    public static int getDeviceWidth(final Display display) {
        return getDisplayMetrics(display).widthPixels;
    }

    @TargetApi(JELLY_BEAN_MR1) private static DisplayMetrics getDisplayMetrics(final Display display) {
        DisplayMetrics result = new DisplayMetrics();
        if (isJellyBeanMR1()) display.getRealMetrics(result);
        else display.getMetrics(result);
        return result;
    }

    public static int getDensity(final Display display) {
        int dens = getDisplayMetrics(display).densityDpi;
        if (dens >= DisplayMetrics.DENSITY_XXXHIGH) return 5;
        else if (dens >= DisplayMetrics.DENSITY_XXHIGH) return 4;
        else if (dens >= DisplayMetrics.DENSITY_XHIGH) return 3;
        else if (dens >= DisplayMetrics.DENSITY_HIGH) return 2;
        else if (dens >= DisplayMetrics.DENSITY_MEDIUM) return 1;
        else if (dens >= DisplayMetrics.DENSITY_LOW) return 0;
        else return 0;/**/
    }

    public static String getDeviceName() {
        final String manufacturer = Build.MANUFACTURER;
        final String model = Build.MODEL;
        if (model.startsWith(manufacturer)) return capitalize(model);
        else return String.format("%s %s", capitalize(manufacturer), model);
    }

    public static String getDeviceOS() {
        return String.format(Locale.US, "Android %s [sdk%d]", RELEASE, SDK_INT);
    }

    public static String capitalize(String string) {
        if (null == string || string.length() == 0) return "";
        char firstChar = string.charAt(0);
        if (Character.isUpperCase(firstChar)) return string;
        else return Character.toUpperCase(firstChar) + string.substring(1);
    }

    /******* Build.VERSION ******/
    /** API 21 : Android 5.0 */
    public static boolean isLollipop() {
        return SDK_INT >= LOLLIPOP;
    }

    /** API 17 : Android 4.2 */
    public static boolean isJellyBeanMR1() {
        return SDK_INT >= JELLY_BEAN_MR1;
    }

    /** API 16 : Android 4.1 */
    public static boolean isJellyBean() {
        return SDK_INT >= JELLY_BEAN;
    }

    /** API 14 : Android 4.0 */
    public static boolean isICS() {
        return SDK_INT >= ICE_CREAM_SANDWICH;
    }

    /** API 13 : Android 3.2 */
    public static boolean isHoneycombMR2() {
        return SDK_INT >= HONEYCOMB_MR2;
    }

    /** API 12 : Android 3.1 */
    public static boolean isHoneycombMR1() {
        return SDK_INT >= HONEYCOMB_MR1;
    }

    /** API 11 : Android 3.0 */
    public static boolean isHoneycomb() {
        return SDK_INT >= HONEYCOMB;
    }

    /** API 9 : Android 2.3 */
    public static boolean isGingerbread() {
        return SDK_INT >= GINGERBREAD;
    }
}
