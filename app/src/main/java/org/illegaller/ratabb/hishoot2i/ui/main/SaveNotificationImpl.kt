package org.illegaller.ratabb.hishoot2i.ui.main

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.BigPictureStyle
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ShareCompat
import androidx.core.graphics.applyCanvas
import common.ext.activityPendingIntent
import common.ext.dpSize
import common.ext.graphics.createBitmap
import common.ext.graphics.drawBitmapSafely
import common.ext.graphics.halfAlpha
import common.ext.graphics.roundedLargeIcon
import common.ext.graphics.scaleCenterCrop
import common.ext.graphics.sizes
import dagger.hilt.android.qualifiers.ActivityContext
import entity.Sizes
import org.illegaller.ratabb.hishoot2i.HiShootActivity
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.data.pref.SettingPref
import timber.log.Timber
import javax.inject.Inject

class SaveNotificationImpl @Inject constructor(
    @ActivityContext context: Context,
    notificationManager: NotificationManagerCompat,
    settingPref: SettingPref
) : SaveNotification {

    private val savingString by lazy { context.getString(R.string.ticker_saving) }
    private val shareString by lazy { context.getString(R.string.share) }
    private val subTextErrString by lazy { context.getString(R.string.sub_text_error) }
    private val tapToViewString by lazy { context.getString(R.string.tap_to_view) }
    private val appIconSize by lazy { context.dpSize(android.R.dimen.app_icon_size) }

    private val shareIntent: (Uri) -> PendingIntent = {
        context.activityPendingIntent {
            ShareCompat.IntentBuilder(context)
                .setStream(it)
                .setType("image/*")
                .setChooserTitle(R.string.share)
                .createChooserIntent()
        }
    }
    private val viewIntent: (Uri) -> PendingIntent = {
        context.activityPendingIntent {
            Intent(Intent.ACTION_VIEW)
                .setDataAndTypeAndNormalize(it, "image/*")
                .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    private val areNotificationsEnabled: () -> Boolean =
        (notificationManager::areNotificationsEnabled)

    private val cancelAll: () -> Unit = (notificationManager::cancelAll)

    private val cancel: (String?, Int) -> Unit by lazy {
        { tag: String?, id: Int ->
            notificationManager.cancel(tag, id)
        }
    }

    private val notify: (String?, Int, Notification) -> Unit by lazy {
        { tag: String?, id: Int, notification: Notification ->
            notificationManager.notify(tag, id, notification)
        }
    }

    private val prepareChannel: (String, String) -> Unit by lazy {
        { channelId: String, channelName: String ->
            if (SDK_INT >= 26 && null == notificationManager.getNotificationChannel(channelId)) {
                notificationManager.createNotificationChannel(
                    NotificationChannel(channelId, channelName, IMPORTANCE_DEFAULT)
                )
            }
        }
    }

    private fun NotificationCompat.Builder.send(tag: String? = null, id: Int = 0x666) {
        if (areNotificationsEnabled()) {
            try {
                cancel(tag, id)
                notify(tag, id, build())
            } catch (e: Exception) {
                Timber.e(e)
                cancelAll()
            }
        }
    }

    private val notificationBuilder by lazy {
        val appName = context.getString(R.string.app_name)
        val channelId = "${context.packageName}.SaveChannelID"

        prepareChannel(channelId, appName)

        NotificationCompat.Builder(context, channelId).apply {
            setContentTitle(appName)
            setSmallIcon(R.drawable.ic_app_notification)
            setDefaults(NotificationCompat.DEFAULT_ALL)
            setContentIntent(HiShootActivity.contentIntent(context)) //
            setOnlyAlertOnce(true)
        }
    }

    private val isEnable: () -> Boolean = (settingPref::saveNotificationEnable)

    override fun start() {
        if (!isEnable()) return
        val localBuilder = notificationBuilder
        localBuilder.setWhen(System.currentTimeMillis())
            .setShowWhen(true)
            .setTicker(savingString)
            .setOngoing(true)
            .setAutoCancel(false)
            .setProgress(0, 0, true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .send()
    }

    override fun error(e: Throwable) {
        if (!isEnable()) return
        val localBuilder = notificationBuilder
        localBuilder.setOngoing(false)
            .setAutoCancel(true)
            .setProgress(100, 100, false)
            .setCategory(NotificationCompat.CATEGORY_ERROR)
            .setContentText(e.localizedMessage ?: "<unknown>") //
            .setSubText(subTextErrString) //
            .send()
    }

    override fun complete(
        bitmap: Bitmap,
        fileName: String,
        uri: Uri
    ) {
        if (!isEnable()) return
        val localBuilder = notificationBuilder
        localBuilder.addAction(android.R.drawable.ic_menu_share, shareString, shareIntent(uri))
            .setShowWhen(false)
            .setContentIntent(viewIntent(uri))
            .setContentTitle(fileName)
            .setContentText(tapToViewString)
            .setOngoing(false)
            .setAutoCancel(true)
            .setProgress(0, 0, false)
            .setStyle(bitmap.bigPictureStyle(fileName, tapToViewString))
            .setLargeIcon(bitmap.roundedLargeIcon(appIconSize))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .send()
    }

    private fun Bitmap.bigPictureStyle(
        title: String,
        summary: String
    ): BigPictureStyle = BigPictureStyle()
        .bigPicture(bigPictureBitmap())
        .bigLargeIcon(null) // NOTE: remove LargeIcon, at BigPictureStyle.
        .setBigContentTitle(title)
        .setSummaryText(summary)

    private val paintBigPicture: Paint by lazy {
        val filter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0.25F) })
        Paint(Paint.FILTER_BITMAP_FLAG).apply { colorFilter = filter }
    }

    private fun Bitmap.bigPictureBitmap(): Bitmap = sizes.shortSide().createBitmap(RGB_565)
        .applyCanvas {
            drawBitmapSafely(
                bitmap = this@bigPictureBitmap.scaleCenterCrop(Sizes(width, height), RGB_565),
                paint = paintBigPicture
            )
            drawColor(Color.DKGRAY.halfAlpha)
        }
}
