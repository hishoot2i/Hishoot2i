package org.illegaller.ratabb.hishoot2i.utils;

import org.illegaller.ratabb.hishoot2i.AppConstants;
import org.illegaller.ratabb.hishoot2i.view.AboutActivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.IBinder;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

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

public class Utils {

    private final static String sSEPARATOR = ",";

    private Utils() {        //no instance
    }

    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static void tryClose(Closeable... closeables) {
        if (closeables == null) return;
        for (Closeable closeable : closeables) {
            if (closeable == null) continue;
            try { closeable.close(); } catch (IOException e) { CrashLog.logError("tryClose", e); }
        }
    }

    public static String getFileNameWithoutExtension(String filePath) {
        if (Utils.isEmpty(filePath)) return filePath;
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

    public static File saveTempBackgroundCrop(final Context context, final Bitmap bitmap) throws IOException {
        File file = new File(context.getFilesDir(), "HiShoot_crop");
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
        Cursor cursor = context.getContentResolver()
                .query(uri, null, null, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        String result = cursor.getString(idx);
        cursor.close();
        return result;
    }

    ////////////////////////////////////////////////
    public static boolean containsLowerCase(String from, String to) {
        return Utils.toLowerCase(from).contains(Utils.toLowerCase(to));
    }

    public static boolean isEmpty(CharSequence charSequence) {
        return charSequence.length() == 0 || TextUtils.isEmpty(charSequence);
    }

    public static String toLowerCase(String string) {
        return string.toLowerCase(Locale.US).trim();
    }

    public static String arrayToString(String[] array) {
        String result = "";
        for (String string : array) {
            if (!Utils.isEmpty(result)) result += Utils.sSEPARATOR;
            result += string;
        }
        return result;
    }

    public static String[] stringToArray(String string) {
        if (!Utils.isEmpty(string) && Utils.containsLowerCase(string, Utils.sSEPARATOR))
            return string.split(Utils.sSEPARATOR);
        else return new String[]{string};
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
    public static void startAbout(Context context) {
        AboutActivity.start(context);
    }

    public static void openImagePicker(final Fragment fragment, final String title, int requestCode) {
        Intent intent = intentImagePicker();
        if (isAvailable(fragment.getActivity(), intent))
            fragment.startActivityForResult(Intent.createChooser(intent, title), requestCode);
    }

    private static Intent intentImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        return intent;
    }

    @TargetApi(11) public static Intent intentShareImage(final String title, final Uri imageUri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        Intent chooser = Intent.createChooser(intent, title);
        int flag = Intent.FLAG_ACTIVITY_NEW_TASK;
        if (DeviceUtils.isHoneycomb()) flag |= Intent.FLAG_ACTIVITY_CLEAR_TASK;
        chooser.addFlags(flag);
        return chooser;
    }

    public static Intent intentOpenImage(final Uri imageUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(imageUri, "image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static void openImageView(final Context context, final Uri imageUri) {
        Intent intent = intentOpenImage(imageUri);
        if (isAvailable(context, intent))
            context.startActivity(intent);
    }

    /* Check if any apps are installed on the app to receive this intent. */
    public static boolean isAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        final List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public static void hideSoftKeyboard(Context context, IBinder token) {
        InputMethodManager imm = (InputMethodManager) context.
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //////////////////////////////////////////////
    public static List<ApplicationInfo> getInstalledApplications(final Context context, int flags) {
        final PackageManager manager = context.getPackageManager();
        try { return manager.getInstalledApplications(flags); } catch (Exception ignored) {}
        List<ApplicationInfo> result = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec("pm list packages");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                final String packageName = line.substring(line.indexOf(':') + 1);
                final ApplicationInfo applicationInfo = manager.getApplicationInfo(packageName, flags);
                result.add(applicationInfo);
            }
            process.waitFor();
            Utils.tryClose(reader);
        } catch (IOException | PackageManager.NameNotFoundException | InterruptedException e) {
            CrashLog.logError("getInstalledApplications", e);
        }
        return result;
    }
}