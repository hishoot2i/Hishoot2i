@file:Suppress("NOTHING_TO_INLINE")

package rbb.hishoot2i.common.ext.graphics

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import com.enrique.stackblur.StackBlurManager

inline fun Canvas.withConcatMatrix(matrix: Matrix, block: Canvas.() -> Unit) {
    val checkpoint = save()
    try {
        concat(matrix)
        block()
    } finally {
        restoreToCount(checkpoint)
    }
}

@JvmOverloads
inline fun Canvas.drawBitmapSafely(
    bitmap: Bitmap?,
    left: Float = 0F,
    top: Float = 0F,
    paint: Paint? = DEFAULT_PAINT_BITMAP
) {
    if (null != bitmap && !bitmap.isRecycled) {
        drawBitmap(bitmap, left, top, paint)
    }
}

@JvmOverloads
inline fun Canvas.drawBitmapPerspective(
    bitmap: Bitmap?,
    coordinate: FloatArray,
    paint: Paint? = DEFAULT_PAINT_BITMAP
) {
    if (null != bitmap && !bitmap.isRecycled) {
        val (width, height) = bitmap.sizes.toSizeF()
        val src = floatArrayOf(
            0F, 0F,
            width, 0F,
            0F, height,
            width, height
        )
        val matrix = Matrix().apply {
            setPolyToPoly(
                src,
                0,
                coordinate,
                0,
                src.size / 2
            )
        }
        withConcatMatrix(matrix) {
            drawBitmapSafely(bitmap, paint = paint)
        }
    }
}

const val MAX_BLUR_RADIUS = 100
inline fun Canvas.drawBitmapBlur(bitmap: Bitmap?, radius: Int) {
    if (null != bitmap && !bitmap.isRecycled) {
        val rad = radius.coerceAtMost(MAX_BLUR_RADIUS)
        val sizes = bitmap.sizes
        val scaledSize = sizes / 2
        val scaleDown = Bitmap.createScaledBitmap(bitmap, scaledSize.x, scaledSize.y, false)
        val stackBlurManager = StackBlurManager(scaleDown)
        val process = stackBlurManager.process(rad)
        val scaleUp = Bitmap.createScaledBitmap(process, sizes.x, sizes.y, false)
        drawBitmapSafely(scaleUp)
    }
}