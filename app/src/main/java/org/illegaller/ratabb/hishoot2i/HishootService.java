package org.illegaller.ratabb.hishoot2i;

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
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;
import java.io.File;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;
import org.illegaller.ratabb.hishoot2i.events.EventPreview;
import org.illegaller.ratabb.hishoot2i.events.EventSave;
import org.illegaller.ratabb.hishoot2i.model.DataImagePath;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.utils.BitmapUtils;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.utils.HishootProcess;
import org.illegaller.ratabb.hishoot2i.utils.Utils;
import org.illegaller.ratabb.hishoot2i.view.LauncherActivity;

public class HishootService extends IntentService {
  private static final int HISHOOT_NOTIFICATION_ID = 0x01;
  private static final String PACKAGE_NAME = HishootService.class.getPackage().getName();
  private static final String ACTION_SAVE = PACKAGE_NAME + ".services.action.SAVE";
  private static final String ACTION_PREVIEW = PACKAGE_NAME + ".services.action.PREVIEW";
  private static final String KEY_DATA_IMAGE_PATH = "data_image_path";
  private static final String KEY_TEMPLATE = "template";
  @InjectExtra(KEY_DATA_IMAGE_PATH) DataImagePath mDataImagePath;
  @InjectExtra(KEY_TEMPLATE) Template mTemplate;
  @Inject NotificationManager mNotificationManager;
  private HishootProcess mHishootProcess;
  private NotificationCompat.Builder mNotificationBuilder;
  private HishootProcess.Callback mSaveCallback = new HishootProcess.Callback() {
    private final Context mContext = HishootService.this;

    @Override public void startProcess(long startTime) {
      Intent nullIntent = LauncherActivity.getIntent(mContext);
      nullIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      mNotificationBuilder = new NotificationCompat.Builder(mContext);
      mNotificationBuilder.setTicker(getString(R.string.saving))
          .setContentTitle(getString(R.string.app_name))
          .setSmallIcon(R.drawable.ic_notif)
          .setWhen(startTime)
          .setContentIntent(PendingIntent.getActivity(mContext, 0, nullIntent, 0))
          .setProgress(0, 0, true)
          .setOngoing(true)
          .setAutoCancel(false);
      Notification notification = mNotificationBuilder.build();
      mNotificationManager.notify(HISHOOT_NOTIFICATION_ID, notification);
    }

    @Override public void failProcess(String message, String extra) {
      final String title = getString(R.string.app_name);
      mNotificationBuilder.setTicker(title)
          .setContentTitle(title)
          .setContentText(message)
          .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(title)
              .bigText(message)
              .setSummaryText(extra))
          .setSmallIcon(R.drawable.ic_notif)
          .setWhen(System.currentTimeMillis())
          .setOngoing(false)
          .setAutoCancel(true)
          .setContentIntent(
              PendingIntent.getActivity(mContext, 0, new Intent(mContext, LauncherActivity.class),
                  0))
          .build();
      Notification notification = mNotificationBuilder.build();
      mNotificationManager.notify(HISHOOT_NOTIFICATION_ID, notification);
    }

    @Override public void doneProcess(Bitmap result, final Uri uri) {
      postRunnableMain(() -> EventBus.getDefault().post(new EventSave(uri)));
      final String share = getString(R.string.share);
      final Intent sharingIntent = Utils.intentShareImage(share, uri);
      mNotificationBuilder.addAction(android.R.drawable.ic_menu_share, share,
          PendingIntent.getActivity(mContext, 0, sharingIntent, PendingIntent.FLAG_CANCEL_CURRENT));
      final Intent openIntent = Utils.intentOpenImage(uri);
      final File file = new File(uri.getPath());
      final Bitmap previewBigPicture = BitmapUtils.previewBigPicture(result);
      final Bitmap largeIcon = BitmapUtils.roundedLargeIcon(mContext, previewBigPicture);
      mNotificationBuilder.setContentIntent(PendingIntent.getActivity(mContext, 0, openIntent, 0))
          .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(previewBigPicture))
          .setLargeIcon(largeIcon)
          .setContentText(file.getName())
          .setProgress(0, 0, false)
          .setOngoing(false)
          .setAutoCancel(true);
      Notification notification = mNotificationBuilder.build();
      mNotificationManager.notify(HISHOOT_NOTIFICATION_ID, notification);
    }
  };
  private HishootProcess.Callback mPreviewCallback = new HishootProcess.Callback() {

    @Override public void startProcess(long startTime) { /*no-op*/ }

    @Override public void failProcess(final String message, final String extra) {
      postRunnableMain(() -> EventBus.getDefault().post(new EventPreview(null, message, extra)));
    }

    @Override public void doneProcess(final Bitmap result, @Nullable final Uri uri) {
      postRunnableMain(() -> EventBus.getDefault().post(new EventPreview(result, "", "")));
    }
  };

  public HishootService() {
    super("HishootService");
  }

  public static void startActionSave(Context context, DataImagePath path, Template template) {
    Intent starterSave = thisIntent(context);
    starterSave.setAction(ACTION_SAVE);
    putExtra(starterSave, path, template);
    context.startService(starterSave);
  }

  public static void startActionPreview(Context context, DataImagePath path, Template template) {
    Intent starterPreview = thisIntent(context);
    starterPreview.setAction(ACTION_PREVIEW);
    putExtra(starterPreview, path, template);
    context.startService(starterPreview);
  }

  static Intent thisIntent(Context context) {
    return new Intent(context, HishootService.class);
  }

  static void putExtra(Intent intent, DataImagePath path, Template template) {
    Utils.checkNotNull(template, "Template == null");
    Utils.checkNotNull(path, "DataImagePath == null");
    intent.putExtra(KEY_DATA_IMAGE_PATH, path);
    intent.putExtra(KEY_TEMPLATE, template);
  }

  void postRunnableMain(Runnable runnable) {
    new Handler(Looper.getMainLooper()).post(runnable);
  }

  @Override public void onCreate() {
    super.onCreate();
    HishootApplication.get(this).getAppComponent().inject(this);
  }

  @Override protected void onHandleIntent(Intent intent) {
    Dart.inject(this, intent.getExtras());
    final String action = intent.getAction();
    HishootProcess.Callback callback = null;
    boolean isSave = false;
    if (ACTION_SAVE.equals(action)) {
      callback = mSaveCallback;
      isSave = true;
    } else if (ACTION_PREVIEW.equals(action)) {
      callback = mPreviewCallback;
      isSave = false;
    }
    Utils.checkNotNull(callback, "no valid action");
    try {
      if (mHishootProcess == null) mHishootProcess = new HishootProcess(this);
      mHishootProcess.process(mDataImagePath, mTemplate, callback, isSave);
    } catch (Exception e) {
      CrashLog.logError(mTemplate.toString() + "\naction: " + action, e);
    }
  }
}
