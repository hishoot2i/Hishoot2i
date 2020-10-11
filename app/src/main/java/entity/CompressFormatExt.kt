package entity

import android.graphics.Bitmap.CompressFormat
import common.ext.exhaustive
import java.util.Locale

inline val CompressFormat.ext: String
    get() = when (this) {
        CompressFormat.JPEG -> "jpg"
        CompressFormat.PNG -> "png"
        CompressFormat.WEBP -> "webp"
    }.exhaustive

inline val CompressFormat.mimeType: String
    get() = "image/${name.toLowerCase(Locale.ROOT)}"
