package org.illegaller.ratabb.hishoot2i;

import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;

import org.greenrobot.eventbus.EventBus;
import org.illegaller.ratabb.hishoot2i.events.EventServiceDone;
import org.illegaller.ratabb.hishoot2i.model.DataImagePath;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.tray.TrayManager;
import org.illegaller.ratabb.hishoot2i.utils.BitmapUtils;
import org.illegaller.ratabb.hishoot2i.utils.HishootProcess;
import org.illegaller.ratabb.hishoot2i.utils.Utils;
import org.illegaller.ratabb.hishoot2i.view.LauncherActivity;

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
    private static final String KEY_DATA_IMAGE_PATH = "data_image_path";
    private static final String KEY_TEMPLATE = "template";
    @Inject TrayManager mTrayManager;
    @Inject NotificationManager notificationManager;
    @InjectExtra(KEY_DATA_IMAGE_PATH) DataImagePath dataImagePath;
    @InjectExtra(KEY_TEMPLATE) Template template;
    HishootProcess hishootProcess;
    NotificationCompat.Builder notificationBuilder;

    public HishootService() {
        super("HishootService");
    }

    public static void start(Context context,
                             @NonNull DataImagePath dataImagePath,
                             @NonNull Template template) {
        Intent intent = new Intent(context, HishootService.class);
        intent.putExtra(KEY_DATA_IMAGE_PATH, dataImagePath);
        intent.putExtra(KEY_TEMPLATE, template);
        context.startService(intent);
    }

    @Override public void onCreate() {
        super.onCreate();
        HishootApplication.get(this).getComponent().inject(this);
    }

    @Override protected void onHandleIntent(Intent intent) {
        Dart.inject(this, intent.getExtras());
        hishootProcess = new HishootProcess(this, template,
                mTrayManager.getSsDoubleEnableTray().get(), mTrayManager.getBgColorEnableTray().get(),
                mTrayManager.getBgImageBlurEnableTray().get(), mTrayManager.getBadgeEnableTray().get(),
                mTrayManager.getGlareEnableTray().get(), mTrayManager.getShadowEnableTray().get(),
                mTrayManager.getBgColorIntTray().get(), mTrayManager.getBgImageBlurRadiusTray().get(),
                mTrayManager.getBadgeColorTray().get(), mTrayManager.getBadgeTextTray().get(),
                mTrayManager.getBadgeSizeTray().get(), this);
        hishootProcess.process(dataImagePath, true);
    }

    @Override public void startingImage(long startTime) {
        Intent nullIntent = LauncherActivity.getIntent(this);
        nullIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setTicker(getString(R.string.saving))
                .setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_notif)
                .setWhen(startTime)
                .setContentIntent(PendingIntent.getActivity(this, 0, nullIntent, 0))
                .setProgress(0, 0, true)
                .setOngoing(true)
                .setAutoCancel(false);
        Notification notification = notificationBuilder.build();
        notificationManager.notify(HISHOOT_NOTIFICATION_ID, notification);
    }

    @Override public void failedImage(String text, String extra) {
        final String title = getString(R.string.app_name);
        notificationBuilder.setTicker(title)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .setBigContentTitle(title)
                        .bigText(text)
                        .setSummaryText(extra))
                .setSmallIcon(R.drawable.ic_notif)
                .setWhen(System.currentTimeMillis())
                .setOngoing(false)
                .setAutoCancel(true)
                .setContentIntent(
                        PendingIntent.getActivity(this, 0, new Intent(this, LauncherActivity.class), 0))
                .build();
        Notification notification = notificationBuilder.build();
        notificationManager.notify(HISHOOT_NOTIFICATION_ID, notification);
    }

    @Override public void doneImage(Bitmap result) {//no-op
    }

    @Override public void doneService(Bitmap result, final Uri uri) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override public void run() {
                EventBus.getDefault().post(new EventServiceDone(uri));
            }
        });

        final String share = getString(R.string.share);
        final Intent sharingIntent = Utils.intentShareImage(share, uri);

        notificationBuilder.addAction(
                android.R.drawable.ic_menu_share, share,
                PendingIntent.getActivity(this, 0, sharingIntent, PendingIntent.FLAG_CANCEL_CURRENT));

        final Intent openIntent = Utils.intentOpenImage(uri);
        final File file = new File(uri.getPath());
        final Bitmap previewBigPicture = BitmapUtils.previewBigPicture(result);
        final Bitmap largeIcon = BitmapUtils.roundedLargeIcon(this, previewBigPicture);

        notificationBuilder.setContentIntent(PendingIntent.getActivity(this, 0, openIntent, 0))
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(previewBigPicture))
                .setLargeIcon(largeIcon)
                .setContentText(file.getName())
                .setProgress(0, 0, false)
                .setOngoing(false)
                .setAutoCancel(true);
        Notification notification = notificationBuilder.build();
        notificationManager.notify(HISHOOT_NOTIFICATION_ID, notification);

        stopSelf();
    }
}
