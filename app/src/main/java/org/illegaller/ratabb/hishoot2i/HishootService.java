package org.illegaller.ratabb.hishoot2i;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
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
import org.illegaller.ratabb.hishoot2i.di.compenent.IntentServiceComponent;
import org.illegaller.ratabb.hishoot2i.events.EventPreview;
import org.illegaller.ratabb.hishoot2i.events.EventSave;
import org.illegaller.ratabb.hishoot2i.model.DataImagePath;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.utils.HishootProcess;
import org.illegaller.ratabb.hishoot2i.view.LauncherActivity;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;
import static android.app.PendingIntent.getActivity;
import static org.illegaller.ratabb.hishoot2i.utils.BitmapUtils.previewBigPicture;
import static org.illegaller.ratabb.hishoot2i.utils.BitmapUtils.roundedLargeIcon;
import static org.illegaller.ratabb.hishoot2i.utils.CrashLog.logError;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.checkNotNull;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.intentOpenImage;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.intentShareImage;

public class HishootService extends IntentService {
  private static final int HISHOOT_NOTIFICATION_ID = 0x01;
  private static final String PACKAGE_NAME = HishootService.class.getPackage().getName();
  private static final String ACTION_SAVE = PACKAGE_NAME + ".services.action.SAVE";
  private static final String ACTION_PREVIEW = PACKAGE_NAME + ".services.action.PREVIEW";
  private static final String KEY_DATA_IMAGE_PATH = "data_image_path";
  private static final String KEY_TEMPLATE = "template";
  private final Handler mMainHandler = new Handler(Looper.getMainLooper());
  @InjectExtra(KEY_DATA_IMAGE_PATH) DataImagePath mDataImagePath;
  @InjectExtra(KEY_TEMPLATE) Template mTemplate;
  @Inject NotificationManager mNotificationManager;
  @Inject HishootProcess mHishootProcess;
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
          .setContentIntent(getActivity(mContext, 0, nullIntent, 0))
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
              getActivity(mContext, 0, new Intent(mContext, LauncherActivity.class), 0))
          .build();
      Notification notification = mNotificationBuilder.build();
      notification.flags |= Notification.FLAG_AUTO_CANCEL;
      mNotificationManager.notify(HISHOOT_NOTIFICATION_ID, notification);
    }

    /* FIXME: StrictMode: file:// Uri exposed through Intent.getData() */
    @Override public void doneProcess(Bitmap result, final Uri uri) {
      postRunnableMain(() -> EventBus.getDefault().post(EventSave.create(uri)));
      final String share = getString(R.string.share);
      final Intent sharingIntent = intentShareImage(share, uri);
      mNotificationBuilder.addAction(android.R.drawable.ic_menu_share, share,
          getActivity(mContext, 0, sharingIntent, FLAG_CANCEL_CURRENT));
      final Intent openIntent = intentOpenImage(uri);
      final File file = new File(uri.getPath());
      final Bitmap previewBigPicture = previewBigPicture(result);
      final Bitmap largeIcon = roundedLargeIcon(mContext, previewBigPicture);
      mNotificationBuilder.setContentIntent(getActivity(mContext, 0, openIntent, 0))
          .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(previewBigPicture))
          .setLargeIcon(largeIcon)
          .setContentText(file.getName())
          .setProgress(0, 0, false)
          .setOngoing(false)
          .setAutoCancel(true);
      Notification notification = mNotificationBuilder.build();
      notification.flags |= Notification.FLAG_AUTO_CANCEL;
      mNotificationManager.notify(HISHOOT_NOTIFICATION_ID, notification);
    }
  };
  private HishootProcess.Callback mPreviewCallback = new HishootProcess.Callback() {

    @Override public void startProcess(long startTime) { /*no-op*/ }

    @Override public void failProcess(final String message, final String extra) {
      postRunnableMain(() -> EventBus.getDefault().post(EventPreview.messageExtra(message, extra)));
    }

    @Override public void doneProcess(final Bitmap result, @Nullable final Uri uri) {
      postRunnableMain(() -> EventBus.getDefault().post(EventPreview.result(result)));
    }
  };
  private IntentServiceComponent mIntentServiceComponent;

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

  private static Intent thisIntent(Context context) {
    return new Intent(context, HishootService.class);
  }

  private static void putExtra(Intent intent, DataImagePath path, Template template) {
    intent.putExtra(KEY_DATA_IMAGE_PATH, checkNotNull(path, "DataImagePath == null"));
    intent.putExtra(KEY_TEMPLATE, checkNotNull(template, "Template == null"));
  }

  private void postRunnableMain(Runnable runnable) {
    mMainHandler.post(runnable);
  }

  @Override public void onCreate() {
    super.onCreate();
    getIntentServiceComponent().inject(this);
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
    try {
      mHishootProcess.process(mDataImagePath, mTemplate, checkNotNull(callback, "no valid action"),
          isSave);
    } catch (Exception e) {
      logError(mTemplate.toString() + "\naction: " + action, e);
    }
  }

  public IntentServiceComponent getIntentServiceComponent() {
    if (mIntentServiceComponent == null) {
      mIntentServiceComponent = IntentServiceComponent.Initializer.init(this);
    }
    return mIntentServiceComponent;
  }
}
