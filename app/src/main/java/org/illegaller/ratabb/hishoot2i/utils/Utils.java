package org.illegaller.ratabb.hishoot2i.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import com.nostra13.universalimageloader.utils.StorageUtils;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.illegaller.ratabb.hishoot2i.AppConstants;
import org.illegaller.ratabb.hishoot2i.BuildConfig;
import org.illegaller.ratabb.hishoot2i.HishootApplication;
import org.illegaller.ratabb.hishoot2i.HishootService;
import org.illegaller.ratabb.hishoot2i.view.AboutActivity;
import org.illegaller.ratabb.hishoot2i.view.CropActivity;
import org.illegaller.ratabb.hishoot2i.view.LauncherActivity;
import org.illegaller.ratabb.hishoot2i.view.MainActivity;

import static android.os.Build.VERSION_CODES.HONEYCOMB;

public class Utils {

  private static final String sSEPARATOR = ",";

  private Utils() {
    throw new AssertionError("no instance");
  }

  public static void avoidUiThread(String msg) {
    if (Looper.myLooper() == Looper.getMainLooper()) throw new IllegalThreadStateException(msg);
  }

  public static void onlyUiThread() {
    if (Looper.myLooper() != Looper.getMainLooper()) {
      throw new IllegalThreadStateException(
          "Must be called from the main thread. Was: " + Thread.currentThread());
    }
  }

  public static Point createPoint(int x, int y) {
    return new Point(x, y);
  }

  public static void tryClose(Closeable... closeables) {
    if (closeables == null) return;
    for (Closeable closeable : closeables) {
      if (closeable == null) continue;
      try {
        closeable.close();
      } catch (IOException e) {
        CrashLog.logError("tryClose", e);
      }
    }
  }

  public static void unbindDrawables(View view) {
    if (view == null) return;
    if (view.getBackground() != null) view.getBackground().setCallback(null);
    if (view instanceof ViewGroup) {
      final ViewGroup viewGroup = (ViewGroup) view;
      for (int i = 0; i < viewGroup.getChildCount(); i++) {
        Utils.unbindDrawables(viewGroup.getChildAt(i));
      }
      if (!(viewGroup instanceof AdapterView)) viewGroup.removeAllViews();
    }
  }

  public static <T> T checkNotNull(T value, String message) {
    if (value == null) throw new NullPointerException(message);
    return value;
  }

  public static String getFileNameWithoutExtension(String filePath) {
    if (Utils.isEmpty(filePath)) return filePath;
    int extPos = filePath.lastIndexOf('.');
    int filePos = filePath.lastIndexOf(File.separatorChar);
    if (filePos == -1) return (extPos == -1 ? filePath : filePath.substring(0, extPos));
    if (extPos == -1) return filePath.substring(filePos + 1);
    return (filePos < extPos ? filePath.substring(filePos + 1, extPos)
        : filePath.substring(filePos + 1));
  }

  static File saveHishoot(final Bitmap bitmap) throws IOException {
    avoidUiThread("avoid save on main thread");
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
    String imageFileName = "HiShoot_" + timeStamp + ".png";
    File hishootDir = AppConstants.getHishootDir();
    File file = new File(hishootDir, imageFileName);
    OutputStream outputStream = new FileOutputStream(file);
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
    outputStream.flush();
    outputStream.close();
    return file;
  }

  public static File saveBackgroundCrop(final Context context, final Bitmap bitmap)
      throws IOException {
    avoidUiThread("avoid save on main thread");
    File file = new File(StorageUtils.getCacheDirectory(context), ".crop");
    OutputStream outputStream = new FileOutputStream(file);
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
    outputStream.flush();
    outputStream.close();
    return file;
  }

  public static void galleryAddPic(final Context context, final Uri contentUri) {
    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
    mediaScanIntent.setData(contentUri);
    context.sendBroadcast(mediaScanIntent);
  }

  ////////////////////////////////////////////////
  public static boolean containsLowerCase(String from, String to) {
    return Utils.toLowerCase(from).contains(Utils.toLowerCase(to));
  }

  public static boolean isEmpty(CharSequence cs) {
    return cs.length() == 0 || TextUtils.isEmpty(cs);
  }

  public static String toLowerCase(String string) {
    return string.toLowerCase(Locale.US).trim();
  }

  public static String arrayToString(String[] array) {
    StringBuilder sb = new StringBuilder();
    for (String string : array) {
      if (!Utils.isEmpty(sb.toString())) sb.append(Utils.sSEPARATOR);
      sb.append(string);
    }
    return sb.toString();
  }

  public static String[] stringToArray(String string) {
    if (!Utils.isEmpty(string) && Utils.containsLowerCase(string, Utils.sSEPARATOR)) {
      return string.split(Utils.sSEPARATOR);
    } else {
      return new String[] { string };
    }
  }

  public static List<String> arrayToList(String[] array) {
    return new ArrayList<>(Arrays.asList(array));
  }

  public static String[] listToArray(List<String> list) {
    String[] result = new String[list.size()];
    return list.toArray(result);
  }

  public static String listToString(List<String> list) {
    return Utils.arrayToString(Utils.listToArray(list));
  }

  public static List<String> stringToList(String string) {
    return Utils.arrayToList(Utils.stringToArray(string));
  }

  /////////////////////////////////////////////////////
  public static void openImagePicker(final android.support.v4.app.Fragment fragment,
      @StringRes int title, int requestCode) {
    Utils.openImagePicker(fragment, fragment.getString(title), requestCode);
  }

  public static void openImagePicker(final android.support.v4.app.Fragment fragment,
      final String title, int requestCode) {
    final Intent intent = Utils.intentImagePicker();
    try {
      if (Utils.isAvailable(fragment.getActivity(), intent)) {
        fragment.startActivityForResult(Intent.createChooser(intent, title), requestCode);
      }
    } catch (Exception e) {
      CrashLog.logError("openImagePicker", e);
    }
  }

/*  public static void openImagePicker(final android.app.Fragment fragment, @StringRes int title,
      int requestCode) {
    Utils.openImagePicker(fragment, fragment.getString(title), requestCode);
  }

  public static void openImagePicker(final android.app.Fragment fragment, final String title,
      int requestCode) {
    Intent intent = intentImagePicker();
    if (Utils.isAvailable(fragment.getActivity(), intent)) {
      fragment.startActivityForResult(Intent.createChooser(intent, title), requestCode);
    }
  }*/

  private static Intent intentImagePicker() {
    final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    intent.setType("image/*");
    return intent;
  }

  public static void shareImage(Context context, Uri imageUri) {
    final Intent intent = Utils.intentShareImage("Share", imageUri);
    try {
      if (Utils.isAvailable(context, intent)) context.startActivity(intent);
    } catch (Exception e) {
      CrashLog.logError("shareImage", e);
    }
  }

  @TargetApi(HONEYCOMB)
  public static Intent intentShareImage(final String title, final Uri imageUri) {
    final Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("image/*");
    intent.putExtra(Intent.EXTRA_STREAM, imageUri);
    Intent chooser = Intent.createChooser(intent, title);
    int flag = Intent.FLAG_ACTIVITY_NEW_TASK;
    if (DeviceUtils.isCompatible(HONEYCOMB)) flag |= Intent.FLAG_ACTIVITY_CLEAR_TASK;
    chooser.addFlags(flag);
    return chooser;
  }

  public static Intent intentOpenImage(final Uri imageUri) {
    final Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setDataAndType(imageUri, "image/*");
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    return intent;
  }

  public static void openImageView(final Context context, final Uri imageUri) {
    final Intent intent = Utils.intentOpenImage(imageUri);
    try {
      if (Utils.isAvailable(context, intent)) context.startActivity(intent);
    } catch (Exception e) {
      CrashLog.logError("openImageView", e);
    }
  }

  /**
   * Check if any apps are installed on the app to receive this intent.
   * FIXME: NPE
   */
  public static boolean isAvailable(Context ctx, Intent intent) throws Exception {
    try {
      final PackageManager pm = ctx.getApplicationContext().getPackageManager();
      final List<ResolveInfo> list =
          pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
      return list.size() > 0;
    } catch (Exception e) {
      CrashLog.logError("Intent isAvailable", e);
      return false;
    }
  }

  public static void hideSoftKeyboard(@NonNull final View view) {
    InputMethodManager imm = (InputMethodManager) view.getContext().
        getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
  }

  public static List<ApplicationInfo> getInstalledApplications(Context ctx, int flags) {
    final PackageManager pm = ctx.getApplicationContext().getPackageManager();
    try {
      return pm.getInstalledApplications(flags);
    } catch (Exception ignored) {
      CrashLog.logError("getInstalledApplications", ignored);
    }
    List<ApplicationInfo> result = new ArrayList<>();
    BufferedReader reader = null;
    try {
      Process process = Runtime.getRuntime().exec("pm list packages");
      reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "iso-8859-1"));
      String line;
      while ((line = reader.readLine()) != null) {
        final String packageName = line.substring(line.indexOf(':') + 1);
        final ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, flags);
        result.add(applicationInfo);
      }
      process.waitFor();
    } catch (IOException | PackageManager.NameNotFoundException | InterruptedException e) {
      CrashLog.logError("getInstalledApplications", e);
    } finally {
      tryClose(reader);
    }
    return result;
  }

  public static Intent getIntentUninstall(String packageName) {
    return new Intent(Intent.ACTION_UNINSTALL_PACKAGE).setData(Uri.parse("package:" + packageName))
        .putExtra(Intent.EXTRA_RETURN_RESULT, true);
  }

  public static void fixInputMethodManager(final Activity activity) {
    final Object imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE);

    final Reflector.TypedObject windowToken =
        new Reflector.TypedObject(activity.getWindow().getDecorView().getWindowToken(),
            android.os.IBinder.class);

    Reflector.invokeMethodExceptionSafe(imm, "windowDismissed", windowToken);

    final Reflector.TypedObject view = new Reflector.TypedObject(null, View.class);

    Reflector.invokeMethodExceptionSafe(imm, "startGettingWindowFocus", view);
  }

  /**
   * for Debug only
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB) public static void enableStrictMode() {
    if (BuildConfig.DEBUG && DeviceUtils.isCompatible(Build.VERSION_CODES.GINGERBREAD)) {
      StrictMode.ThreadPolicy.Builder threadPolicyBuilder =
          new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog();
      StrictMode.VmPolicy.Builder vmPolicyBuilder =
          new StrictMode.VmPolicy.Builder().detectAll().penaltyLog();
      if (DeviceUtils.isCompatible(Build.VERSION_CODES.HONEYCOMB)) {
        threadPolicyBuilder.penaltyFlashScreen();
        vmPolicyBuilder.setClassInstanceLimit(HishootApplication.class, 1)
            .setClassInstanceLimit(AboutActivity.class, 1)
            .setClassInstanceLimit(CropActivity.class, 1)
            .setClassInstanceLimit(LauncherActivity.class, 1)
            .setClassInstanceLimit(MainActivity.class, 1)
            .setClassInstanceLimit(HishootService.class, 1);
      }
      StrictMode.setThreadPolicy(threadPolicyBuilder.build());
      StrictMode.setVmPolicy(vmPolicyBuilder.build());
    }
  }
}