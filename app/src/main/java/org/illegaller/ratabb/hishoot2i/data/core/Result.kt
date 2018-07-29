package org.illegaller.ratabb.hishoot2i.data.core

import android.graphics.Bitmap
import android.net.Uri

sealed class Result(open val bitmap: Bitmap) {
    data class Preview(override val bitmap: Bitmap) : Result(bitmap)
    data class Save(override val bitmap: Bitmap, val uri: Uri) : Result(bitmap)
}