package org.illegaller.ratabb.hishoot2i.util;

import static org.illegaller.ratabb.hishoot2i.Constants.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.illegaller.ratabb.hishoot2i.R;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Build;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

public class DeviceUtil {
	/** API >=21 **/
	public static boolean isLollipop() {
		return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
	}

	/** API >=19 **/
	public static boolean isKitkat() {
		return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT);
	}

	/** API >=17 **/
	public static boolean isJellyBean() {
		return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1);
	}

	/** API >=14 **/
	public static boolean isICS() {
		return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private static DisplayMetrics getDisplayMetrics(Display display) {
		DisplayMetrics result = new DisplayMetrics();

		if (isJellyBean()) {
			display.getRealMetrics(result);
		} else {
			display.getMetrics(result);
		}

		return result;
	}

	public static void setDeviceInfo(Display display, SharedPreferences pref) {
		String devicename = String
				.format("%s (%s)", Build.MODEL, Build.PRODUCT);
		String os_ver = String.format("Android %s", Build.VERSION.RELEASE);
		DisplayMetrics dm = getDisplayMetrics(display);

		int height = dm.heightPixels;
		int width = dm.widthPixels;
		int density = convertDensity(dm.densityDpi);

		Pref.commitPref(pref, KEY_PREF_REAL_DENSITY, dm.densityDpi);
		Pref.commitPref(pref, KEY_PREF_DENSITY, density);
		Pref.commitPref(pref, KEY_PREF_DEVICE_HEIGHT, height);
		Pref.commitPref(pref, KEY_PREF_DEVICE_WIDTH, width);
		Pref.commitPref(pref, KEY_PREF_DEVICE_OS, os_ver);
		Pref.commitPref(pref, KEY_PREF_DEVICE, devicename);
		Pref.commitPref(pref, KEY_FIRSTRUN, true);
	}

	@SuppressLint({ "NewApi", "ResourceAsColor" })
	public static void setTintSystemBar(Activity activity, boolean on) {
		Window window = activity.getWindow();
		int idcolor = R.color.latar_gelap;
		int idtrans = android.R.color.transparent;
		Resources res = activity.getResources();
		if (isLollipop()) {
			if (on) {
				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			}
			window.setStatusBarColor(on ? idtrans : res.getColor(idcolor));
		} else if (isKitkat()) {
			WindowManager.LayoutParams winParams = window.getAttributes();
			final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
			if (on) {
				winParams.flags |= bits;
			} else {
				winParams.flags &= ~bits;
			}
			window.setAttributes(winParams);
		} else {
			SystemBarTintManager tintManager = new SystemBarTintManager(
					activity);
			tintManager.setStatusBarTintEnabled(true);
			tintManager.setStatusBarTintResource(idcolor);
		}

	}

	private static int convertDensity(int i) {
		switch (i) {
		case DisplayMetrics.DENSITY_LOW:
			return 0;
		case DisplayMetrics.DENSITY_MEDIUM:
			return 1;
		case DisplayMetrics.DENSITY_HIGH:
			return 2;
		case DisplayMetrics.DENSITY_XHIGH:
			return 3;
		case DisplayMetrics.DENSITY_XXHIGH:
			return 4;
		case DisplayMetrics.DENSITY_XXXHIGH:
			return 5;
		default:
			return -1;

		}

	}

	// XXX
	public static String testHashKey(Context context, String algorithm) {
		String result = null;
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance(algorithm);// "SHA"
				md.update(signature.toByteArray());
				// Log.d("KeyHash:",
				// Base64.encodeToString(md.digest(), Base64.DEFAULT));
				result = Base64.encodeToString(md.digest(), Base64.DEFAULT);
			}
		} catch (NameNotFoundException e) {
			result = null;
		} catch (NoSuchAlgorithmException e) {
			result = null;
		}
		return result;
	}
}
