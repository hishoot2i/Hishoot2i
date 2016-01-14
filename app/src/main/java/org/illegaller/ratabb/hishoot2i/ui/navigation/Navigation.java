package org.illegaller.ratabb.hishoot2i.ui.navigation;

import org.illegaller.ratabb.hishoot2i.ui.activity.AboutActivity;
import org.illegaller.ratabb.hishoot2i.ui.activity.ImportHtzActivity;
import org.illegaller.ratabb.hishoot2i.ui.activity.MainActivity;
import org.illegaller.ratabb.hishoot2i.utils.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import java.util.List;

public class Navigation {

    protected Navigation() {
        throw new AssertionError("Navigation no construction");
    }

    public static void startMainActivity(@NonNull final Context context) {
        Intent intent = MainActivity.getIntent(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Utils.isHoneycomb()) intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void startImportHtz(@NonNull final Context context) {
        context.startActivity(ImportHtzActivity.getIntent(context));
    }

    public static void startAboutActivity(@NonNull final Context context) {
        context.startActivity(AboutActivity.getIntent(context));
    }

    public static void openBrowser(final Context context, final String urlTo) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(urlTo));
        if (Navigation.isAvailable(context, intent))
            context.startActivity(intent);
    }

    public static void openImagePicker(final Fragment fragment, final String title, int requestCode) {
        Intent intent = Navigation.intentImagePicker();
        if (Navigation.isAvailable(fragment.getActivity(), intent))
            fragment.startActivityForResult(Intent.createChooser(intent, title), requestCode);
    }

    public static void openImagePicker(final Activity activity, final String title, int requestCode) {
        Intent intent = Navigation.intentImagePicker();
        if (Navigation.isAvailable(activity, intent))
            activity.startActivityForResult(Intent.createChooser(intent, title), requestCode);
    }

    public static void openImageView(final Context context, final Uri imageUri) {
        Intent intent = Navigation.intentOpenImage(imageUri);
        if (Navigation.isAvailable(context, intent))
            context.startActivity(intent);
    }

    public static Intent intentShareImage(final String title, final Uri imageUri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        Intent chooser = Intent.createChooser(intent, title);
        int flag = Intent.FLAG_ACTIVITY_NEW_TASK;
        if (Utils.isHoneycomb()) flag |= Intent.FLAG_ACTIVITY_CLEAR_TASK;
        chooser.addFlags(flag);
        return chooser;
    }

    public static Intent intentImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        return intent;
    }

    public static Intent intentOpenImage(final Uri imageUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(imageUri, "image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    /**
     * Check if any apps are installed on the app to receive this intent.
     */
    public static boolean isAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        final List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}
