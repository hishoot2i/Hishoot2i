package org.illegaller.ratabb.hishoot2i.utils;

import org.illegaller.ratabb.hishoot2i.AppConstants;
import org.illegaller.ratabb.hishoot2i.BuildConfig;
import org.illegaller.ratabb.hishoot2i.model.Sizes;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Toast;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.GINGERBREAD;
import static android.os.Build.VERSION_CODES.HONEYCOMB;
import static android.os.Build.VERSION_CODES.HONEYCOMB_MR1;
import static android.os.Build.VERSION_CODES.HONEYCOMB_MR2;
import static android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;
import static android.os.Build.VERSION_CODES.JELLY_BEAN;
import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR1;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

public class Utils {

    protected Utils() {
        throw new AssertionError("Utils no construction");
    }

    /*********** Resources ***********/
    public static InputStream getAssetsStream(final Context context, final String packageName,
                                              final String assetName)
            throws PackageManager.NameNotFoundException, IOException {
        return Utils.createPackageContext(context, packageName).getAssets().open(assetName);
    }

    public static Sizes getSizesBitmapTemplate(final Context context, final String packageName,
                                               final String drawableName)
            throws PackageManager.NameNotFoundException {
        Context contextTemplate = Utils.createPackageContext(context, packageName);
        int resID = Utils.getResIdDrawableTemplate(contextTemplate, packageName, drawableName);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(Utils.getResourcesFrom(contextTemplate), resID, options);
        return Sizes.create(options.outWidth, options.outHeight);
    }

    public static String getStringFilePathHtz(final File currentPath, String fileName) {
        return UILHelper.stringFiles(new File(currentPath, fileName));
    }

    public static String getStringFilePath(final Context context, final String packageName,
                                           final String fileName)
            throws PackageManager.NameNotFoundException {
        @DrawableRes int drawableRes = Utils.getResIdDrawableTemplate(context, packageName, fileName);
        return UILHelper.stringTemplateApp(packageName, drawableRes);
    }

    @DrawableRes public static int getResIdDrawableTemplate(final Context context, final String packageName,
                                                            final String resourceName)
            throws PackageManager.NameNotFoundException {
        Context contextTarget = Utils.createPackageContext(context, packageName);
        Resources resources = Utils.getResourcesFrom(contextTarget);
        return resources.getIdentifier(resourceName, "drawable", packageName);
    }

    @ColorInt public static int getColorInt(final Context context, @ColorRes int colorRes) {
        return ContextCompat.getColor(context, colorRes);
    }

    public static Resources getResourcesFrom(final Context context) {
        return context.getResources();
    }

    public static Context createPackageContext(final Context context, final String packageName)
            throws PackageManager.NameNotFoundException {
        return context.createPackageContext(packageName, 0);
    }

    public static int getDimensionPixelSize(final Context context, @DimenRes int dimenId) {
        return context.getResources().getDimensionPixelSize(dimenId);
    }

    /*********** Display ***********/
    /**
     * reflection  method {@link Display#getRealMetrics(DisplayMetrics)}
     **/
    @TargetApi(17) public static int getDeviceHeight(final Display display) {
        if (Utils.isJellyBeanMR1())
            return Utils.getDisplayMetrics(display).heightPixels;
        else if (Utils.isHoneycombMR2()) {
            try {
                Method mGetRawH = Display.class.getMethod("getRawHeight");
                return (Integer) mGetRawH.invoke(display);
            } catch (Exception e) {
                HLog.setTAG(Utils.class);
                HLog.e("getRawHeight reflection", e);
                return 0;
            }
        } else {
            return display.getHeight();
        }
    }

    public static int getDeviceWidth(final Display display) {
        return Utils.getDisplayMetrics(display).widthPixels;
    }

    @TargetApi(17) public static DisplayMetrics getDisplayMetrics(final Display display) {
        DisplayMetrics result = new DisplayMetrics();
        if (Utils.isJellyBeanMR1()) {
            display.getRealMetrics(result);
        } else {
            display.getMetrics(result);
        }
        return result;
    }

    public static int getDensity(final Display display) {
        int dens = Utils.getDisplayMetrics(display).densityDpi;
        if (dens >= DisplayMetrics.DENSITY_XXXHIGH) {
            return 5;
        } else if (dens >= DisplayMetrics.DENSITY_XXHIGH) {
            return 4;
        } else if (dens >= DisplayMetrics.DENSITY_XHIGH) {
            return 3;
        } else if (dens >= DisplayMetrics.DENSITY_HIGH) {
            return 2;
        } else if (dens >= DisplayMetrics.DENSITY_MEDIUM) {
            return 1;
        } else if (dens >= DisplayMetrics.DENSITY_LOW) {
            return 0;
        } else {
            HLog.setTAG(Utils.class);
            HLog.e("Density:\n" + dens);
            return 0;
        }
    }

    /*********** I/O ***********/
    public static void tryClose(Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*********** File ***********/
    public static boolean saveTextToFile(final String text, final File file) {
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            PrintWriter printWriter = new PrintWriter(outputStream);
            printWriter.append(text);
            printWriter.flush();
            Utils.tryClose(printWriter);
            Utils.tryClose(outputStream);
        } catch (FileNotFoundException e) {
            HLog.setTAG(Utils.class);
            HLog.e("save text to file", e);
            return false;
        }
        return true;
    }

    public static String getFileNameWithoutExtension(String filePath) {
        if (TextUtils.isEmpty(filePath)) return filePath;

        int extPos = filePath.lastIndexOf(".");
        int filePos = filePath.lastIndexOf(File.separator);
        if (filePos == -1) return (extPos == -1 ? filePath : filePath.substring(0, extPos));

        if (extPos == -1) return filePath.substring(filePos + 1);

        return (filePos < extPos ?
                filePath.substring(filePos + 1, extPos)
                : filePath.substring(filePos + 1));
    }

    public static File saveHishoot(final Bitmap bitmap) throws IOException {
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

    public static void galleryAddPic(final Context context, final Uri contentUri) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public static String getStringFromUri(final Context context, final Uri uri) {
        File f = new File(uri.getPath());
        String result = (!f.isFile()) ? Utils.getImagePath(context, uri) : uri.getPath();
        return (new File(result)).getAbsolutePath();
    }

    public static String getImagePath(final Context context, final Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null,
                null, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        String result = cursor.getString(idx);
        cursor.close();
        return result;
    }

    /*********** Toast ***********/
    public static void makeToast(final Context context, final String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void makeLongToast(final Context context, final String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /*********** StatusBar ***********/
    @TargetApi(23) public static void setTransparentStatusBar(@NonNull final Window window) {
        @ColorInt int transparent = android.R.color.transparent;
        if (Utils.isLollipop()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(transparent);
        }
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

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return Utils.capitalize(model);
        } else {
            return String.format("%s %s", capitalize(manufacturer), model);
        }
    }

    public static String getDeviceOS() {
        String release = Build.VERSION.RELEASE;
        return String.format(Locale.US, "Android %s [sdk%d]", release, SDK_INT);
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

    /******* View *******/
    public static LayoutInflater getInflater(View view) {
        return LayoutInflater.from(view.getContext());
    }

    public static View inflateViewHolder(ViewGroup parent, @LayoutRes int layout) {
        return Utils.getInflater(parent).inflate(layout, parent, false);
    }

    public static void unbindDrawables(View view) {
        if (view == null) return;
        if (view.getBackground() != null) view.getBackground().setCallback(null);

        if (view instanceof ViewGroup) {
            final ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++)
                Utils.unbindDrawables(viewGroup.getChildAt(i));

            if (viewGroup instanceof AdapterView) HLog.d(viewGroup);//no-op
            else viewGroup.removeAllViews();
        }
    }

    @TargetApi(11) public static void enableStrictMode() {
        if (BuildConfig.DEBUG) {//for Debug only
            if (Utils.isGingerbread()) {
                StrictMode.ThreadPolicy.Builder threadPolicyBuilder = new StrictMode.ThreadPolicy.Builder()
                        .detectAll().penaltyLog();
                StrictMode.VmPolicy.Builder vmPolicyBuilder = new StrictMode.VmPolicy.Builder()
                        .detectAll().penaltyLog();
                if (Utils.isHoneycomb()) threadPolicyBuilder.penaltyFlashScreen();

                StrictMode.setThreadPolicy(threadPolicyBuilder.build());
                StrictMode.setVmPolicy(vmPolicyBuilder.build());
            }
        }
    }

    /**
     * memory-leak {@link android.view.inputmethod.InputMethodManager}
     **/
    public static void fixInputMethodManager(final Activity activity) {
        final Object imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        final Reflector.TypedObject windowToken
                = new Reflector.TypedObject(activity.getWindow().getDecorView().getWindowToken(),
                android.os.IBinder.class);

        Reflector.invokeMethodExceptionSafe(imm, "windowDismissed", windowToken);

        final Reflector.TypedObject view
                = new Reflector.TypedObject(null, View.class);

        Reflector.invokeMethodExceptionSafe(imm, "startGettingWindowFocus", view);
    }

    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
