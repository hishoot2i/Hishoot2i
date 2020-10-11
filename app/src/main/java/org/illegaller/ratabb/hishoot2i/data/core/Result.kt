package org.illegaller.ratabb.hishoot2i.data.core

import android.graphics.Bitmap
import android.net.Uri

sealed class Result {
    data class Preview(val bitmap: Bitmap) : Result()
    data class Save(val bitmap: Bitmap, val uri: Uri, val name: String) : Result()
}
