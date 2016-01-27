package org.illegaller.ratabb.hishoot2i.ui.navigation;

import com.nononsenseapps.filepicker.FilePickerActivity;

import org.illegaller.ratabb.hishoot2i.ui.activity.AboutActivity;
import org.illegaller.ratabb.hishoot2i.ui.activity.HtzFilePickActivity;
import org.illegaller.ratabb.hishoot2i.ui.activity.ImportHtzActivity;
import org.illegaller.ratabb.hishoot2i.ui.activity.MainActivity;
import org.illegaller.ratabb.hishoot2i.utils.Utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import java.util.List;

public class Navigation {

    protected Navigation() {
        throw new AssertionError("Navigation no construction");
    }

    public static void startImportHtz(@NonNull final Context context, @NonNull Uri data) {
        Intent intent = ImportHtzActivity.getIntent(context);
        intent.setData(data);
        context.startActivity(intent);
    }

    public static void startAboutActivity(@NonNull final Context context) {
        context.startActivity(AboutActivity.getIntent(context));
    }

    public static void openImagePicker(final Fragment fragment, final String title, int requestCode) {
        Intent intent = Navigation.intentImagePicker();
        if (Navigation.isAvailable(fragment.getActivity(), intent))
            fragment.startActivityForResult(Intent.createChooser(intent, title), requestCode);
    }

    public static void openHtzPicker(final Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), HtzFilePickActivity.class);
        intent.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        intent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
        intent.putExtra(FilePickerActivity.EXTRA_START_PATH,
                Environment.getExternalStorageDirectory().getPath());
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void openImageView(final Context context, final Uri imageUri) {
        Intent intent = Navigation.intentOpenImage(imageUri);
        if (Navigation.isAvailable(context, intent))
            context.startActivity(intent);
    }

    @TargetApi(11)public static Intent intentShareImage(final String title, final Uri imageUri) {
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

//    public static void restartActivity(final Activity activity) {
//        if (activity == null) return;
//        final int enter_anim = android.R.anim.fade_in;
//        final int exit_anim = android.R.anim.fade_out;
//        activity.overridePendingTransition(enter_anim, exit_anim);
//        activity.finish();
//        activity.overridePendingTransition(enter_anim, exit_anim);
//        activity.startActivity(activity.getIntent());
//    }

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
