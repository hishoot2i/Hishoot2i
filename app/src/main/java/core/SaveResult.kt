package core

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat

interface SaveResult {
    suspend fun savingIt(
        bitmap: Bitmap,
        compressFormat: CompressFormat,
        saveQuality: Int
    ): Save
}
