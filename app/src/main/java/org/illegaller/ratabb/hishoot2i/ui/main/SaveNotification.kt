package org.illegaller.ratabb.hishoot2i.ui.main

import android.app.PendingIntent
import android.graphics.Bitmap

interface SaveNotification {
    fun start()
    fun error(e: Throwable)
    fun complete(bitmap: Bitmap, fileName: String, piShare: PendingIntent, piView: PendingIntent)
}
