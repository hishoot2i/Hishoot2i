package core

import android.graphics.Bitmap
import android.net.Uri

sealed class CoreResult
data class Preview(val bitmap: Bitmap) : CoreResult()
data class Save(val bitmap: Bitmap, val uri: Uri, val name: String) : CoreResult()
