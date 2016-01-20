package org.illegaller.ratabb.hishoot2i;

import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;

import org.illegaller.ratabb.hishoot2i.di.TemplateProvider;
import org.illegaller.ratabb.hishoot2i.di.ir.BackgroundColorEnable;
import org.illegaller.ratabb.hishoot2i.di.ir.BackgroundColorInt;
import org.illegaller.ratabb.hishoot2i.di.ir.BackgroundImageBlurEnable;
import org.illegaller.ratabb.hishoot2i.di.ir.BackgroundImageBlurRadius;
import org.illegaller.ratabb.hishoot2i.di.ir.BadgeColor;
import org.illegaller.ratabb.hishoot2i.di.ir.BadgeEnable;
import org.illegaller.ratabb.hishoot2i.di.ir.BadgeSize;
import org.illegaller.ratabb.hishoot2i.di.ir.BadgeText;
import org.illegaller.ratabb.hishoot2i.di.ir.ScreenDoubleEnable;
import org.illegaller.ratabb.hishoot2i.di.ir.TemplateUsedID;
import org.illegaller.ratabb.hishoot2i.model.DataImagePath;
import org.illegaller.ratabb.hishoot2i.model.pref.BooleanPreference;
import org.illegaller.ratabb.hishoot2i.model.pref.IntPreference;
import org.illegaller.ratabb.hishoot2i.model.pref.StringPreference;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.ui.activity.MainActivity;
import org.illegaller.ratabb.hishoot2i.ui.navigation.BusProvider;
import org.illegaller.ratabb.hishoot2i.ui.navigation.EventHishoot;
import org.illegaller.ratabb.hishoot2i.ui.navigation.Navigation;
import org.illegaller.ratabb.hishoot2i.utils.BitmapUtils;
import org.illegaller.ratabb.hishoot2i.utils.HishootProcess;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import java.io.File;

import javax.inject.Inject;

public class HishootService extends IntentService implements HishootProcess.Callback {
    public static final int HISHOOT_NOTIFICATION_ID = 0x01;
    public static final String KEY_EXTRA_PATH_DATA = "extra_path_data";

    @Inject @ScreenDoubleEnable BooleanPreference screenDoublePref;
    @Inject @BackgroundColorEnable BooleanPreference bgColorEnablePref;
    @Inject @BackgroundImageBlurEnable BooleanPreference bgImageBlurEnablePref;
    @Inject @BackgroundImageBlurRadius IntPreference bgImageBlurRadiusPref;
    @Inject @BackgroundColorInt IntPreference bgColorIntPref;
    @Inject @BadgeEnable BooleanPreference badgeEnablePref;
    @Inject @BadgeText StringPreference badgeTextPref;
    @Inject @BadgeColor IntPreference badgeColorPref;
    @Inject @BadgeSize IntPreference badgeSizePref;
    @Inject @TemplateUsedID StringPreference templateUsedIDPref;
    @Inject NotificationManager notificationManager;
    NotificationCompat.Builder notificationBuilder;


    @InjectExtra(KEY_EXTRA_PATH_DATA) DataImagePath dataPath;
    HishootProcess mHishootProcess;

    public HishootService() {
        super("HishootService");
    }

    public static void start(final Context context, @NonNull final DataImagePath dataPath) {
        Intent intent = new Intent(context, HishootService.class);
        intent.putExtra(KEY_EXTRA_PATH_DATA, dataPath);
        context.startService(intent);
    }

    @Override public void onCreate() {
        super.onCreate();
        ((HishootApplication) getApplication()).inject(this);
    }

    @Override public void onDestroy() {
        super.onDestroy();
    }

    @Override protected void onHandleIntent(Intent intent) {
        Dart.inject(this, intent.getExtras());
        TemplateProvider templateProvider = new TemplateProvider(this);
        final Template template = templateProvider.findById(templateUsedIDPref.get());
        mHishootProcess = new HishootProcess(this, this, template, screenDoublePref.get(),
                bgColorEnablePref.get(), bgImageBlurEnablePref.get(), badgeEnablePref.get(),
                bgColorIntPref.get(), bgImageBlurRadiusPref.get(), badgeColorPref.get(),
                badgeTextPref.get(), badgeSizePref.get()
        );
        mHishootProcess.process(dataPath);
    }


    @Override public void startingImage(long startTime) {
        Intent nullIntent = MainActivity.getIntent(this);
        nullIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationBuilder = new NotificationCompat.Builder(this)
                .setTicker(getString(R.string.saving))
                .setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_notif)
                .setWhen(startTime)
                .setContentIntent(PendingIntent.getActivity(this, 0, nullIntent, 0))
                .setProgress(0, 0, true)
                .setAutoCancel(false);
        Notification notification = notificationBuilder.build();
        notificationManager.notify(HISHOOT_NOTIFICATION_ID, notification);
    }

    @Override public void failedImage(String msg, String extra) {
        final String title = getString(R.string.app_name);
         Notification notification = new NotificationCompat.Builder(this)
                .setTicker(title)
                .setContentTitle(title)
                .setContentText(msg)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .setBigContentTitle(title)
                        .bigText(msg)
                        .setSummaryText(extra))
                .setSmallIcon(R.drawable.ic_notif)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentIntent(
                        PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0))
                .build();
        notificationManager.notify(HISHOOT_NOTIFICATION_ID, notification);
    }

    @Override public void doneImage(final Uri imageUri, final Bitmap result) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override public void run() {
                BusProvider.getInstance().post(new EventHishoot.EventProcessDone(imageUri));
            }
        });

        //NOTIFICATION
        //share
        final String share = getString(R.string.share);
        final Intent sharingIntent = Navigation.intentShareImage(share, imageUri);
        notificationBuilder.addAction(
                android.R.drawable.ic_menu_share, share,
                PendingIntent.getActivity(this, 0, sharingIntent, PendingIntent.FLAG_CANCEL_CURRENT));

        //open
        final Intent openIntent = Navigation.intentOpenImage(imageUri);
        final File file = new File(imageUri.getPath());
        final Bitmap previewBigPicture = BitmapUtils.previewBigPicture(result);
        final Bitmap largeIcon = BitmapUtils.roundedLargeIcon(this, previewBigPicture);

        notificationBuilder.setContentIntent(
                PendingIntent.getActivity(this, 0, openIntent, 0))
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(previewBigPicture))
                .setLargeIcon(largeIcon)
                .setContentText(file.getName())
                .setProgress(0, 0, false)
                .setAutoCancel(true);
        Notification notification = notificationBuilder.build();

        notificationManager.notify(HISHOOT_NOTIFICATION_ID, notification);

        stopSelf();
    }
}