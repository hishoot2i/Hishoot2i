package org.illegaller.ratabb.hishoot2i.data.core

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import io.reactivex.Single

interface SaveResult {
    fun save(
        bitmap: Bitmap,
        compressFormat: CompressFormat,
        saveQuality: Int
    ): Single<Result.Save>
}
