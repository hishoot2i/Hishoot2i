package org.illegaller.ratabb.hishoot2i.ui.main

import android.graphics.Bitmap
import android.net.Uri

interface SaveNotification {
    fun start()
    fun error(e: Throwable)
    fun complete(bitmap: Bitmap, fileName: String, uri: Uri)
}
