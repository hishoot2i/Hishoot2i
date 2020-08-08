package org.illegaller.ratabb.hishoot2i.ui.main

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import common.ext.graphics.applyCanvas
import common.ext.graphics.createBitmap
import common.ext.graphics.drawBitmapSafely
import common.ext.graphics.halfAlpha
import common.ext.graphics.roundedLargeIcon
import common.ext.graphics.scaleCenterCrop
import common.ext.graphics.sizes
import common.ext.prepareNotificationChannel
import dagger.hilt.android.qualifiers.ActivityContext
import org.illegaller.ratabb.hishoot2i.R
import timber.log.Timber
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

class SaveNotification @Inject constructor(
    @ActivityContext context: Context
) {
    private val savingString by lazy(NONE) { context.getString(R.string.ticker_saving) }
    private val shareString by lazy(NONE) { context.getString(R.string.share) }
    private val subTextErrString by lazy(NONE) { context.getString(R.string.sub_text_error) }
    private val tapToViewString by lazy(NONE) { context.getString(R.string.tap_to_view) }
    private val appIconSize by lazy(NONE) {
        context.resources.getDimensionPixelSize(android.R.dimen.app_icon_size)
    }
    private val notificationManager by lazy(NONE) { NotificationManagerCompat.from(context) }
    /**/
    private val notificationBuilder by lazy(NONE) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.prepareNotificationChannel(CHANNEL_ID, CHANNEL_NAME)
        }
        NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setContentTitle(context.getString(R.string.app_name))
            setSmallIcon(R.drawable.ic_app_notification)
            setDefaults(NotificationCompat.DEFAULT_ALL)
            setContentIntent(MainActivity.contentIntent(context))
            setOnlyAlertOnce(true)
        }
    }

    /**/
    fun start() {
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

    /**/
    fun error(e: Throwable) {
        val localBuilder = notificationBuilder
        localBuilder.setOngoing(false)
            .setAutoCancel(true)
            .setProgress(100, 100, false)
            .setCategory(NotificationCompat.CATEGORY_ERROR)
            .setContentText(e.localizedMessage)
            .setSubText(subTextErrString) //
            .send()
    }

    /**/
    fun complete(
        bitmap: Bitmap,
        fileName: String,
        intentShare: PendingIntent,
        intentView: PendingIntent
    ) {
        val localBuilder = notificationBuilder
        localBuilder.addAction(android.R.drawable.ic_menu_share, shareString, intentShare)
            .setContentIntent(intentView)
            .setContentTitle(fileName)
            .setContentText(tapToViewString)
            .setOngoing(false)
            .setAutoCancel(true)
            .setProgress(0, 0, false)
            .setStyle(bigPictureStyle(bitmap, fileName, tapToViewString))
            .setLargeIcon(bitmap.roundedLargeIcon(appIconSize))
            .send()
    }

    private fun NotificationCompat.Builder.send() {
        if (notificationManager.areNotificationsEnabled()) {
            try {
                notificationManager.notify(ID, build())
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    private fun bigPictureStyle(
        bitmap: Bitmap,
        title: String,
        summary: String
    ): NotificationCompat.BigPictureStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(bitmap.bigPicture())
        .bigLargeIcon(null) // NOTE: remove LargeIcon, when BigPicture is showing.
        .setBigContentTitle(title)
        .setSummaryText(summary)

    private fun Bitmap.bigPicture(): Bitmap {
        val (imageWidth, imageHeight) = sizes
        val shortSide = imageWidth.coerceAtMost(imageHeight)
        val shortSideSizes = entity.Sizes(shortSide, shortSide)
        return shortSideSizes.createBitmap(Bitmap.Config.RGB_565).applyCanvas {
            drawBitmapSafely(
                this@bigPicture.scaleCenterCrop(shortSideSizes, Bitmap.Config.RGB_565),
                paint = paintBigPicture
            )
            drawColor(DARK_GRAY_HALF_ALPHA)
        }
    }

    private val paintBigPicture = ColorMatrixColorFilter(ColorMatrix().apply {
        setSaturation(POINT_OF_TWENTY_FIVE)
    }).let { filter: ColorMatrixColorFilter ->
        Paint(Paint.FILTER_BITMAP_FLAG).apply { colorFilter = filter }
    }

    companion object {
        private const val ID = 0x666
        private const val CHANNEL_ID = "org.illegaller.ratabb.hishoot2i.SaveChannelID"
        private const val CHANNEL_NAME = "HiShoot2i"
        private const val POINT_OF_TWENTY_FIVE = .25F
        private val DARK_GRAY_HALF_ALPHA = Color.DKGRAY.halfAlpha
    }
}
