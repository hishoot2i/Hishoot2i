@file:Suppress("NOTHING_TO_INLINE")

package common.graphics

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint

@JvmOverloads
inline fun Canvas.drawBitmapSafely(
    bitmap: Bitmap?,
    left: Float = 0F,
    top: Float = 0F,
    paint: Paint? = DEFAULT_PAINT_BITMAP
) {
    if (null != bitmap && !bitmap.isRecycled) drawBitmap(bitmap, left, top, paint)
}
