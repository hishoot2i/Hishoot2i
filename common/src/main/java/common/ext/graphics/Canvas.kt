@file:Suppress("NOTHING_TO_INLINE")

package common.ext.graphics

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import com.enrique.stackblur.JavaBlurProcess

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
            bitmap.recycleSafely()
        }
    }
}

const val MAX_BLUR_RADIUS = 100
inline fun Canvas.drawBitmapBlur(bitmap: Bitmap?, radius: Int) {
    if (null != bitmap && !bitmap.isRecycled) {
        val rad = radius.coerceAtMost(MAX_BLUR_RADIUS).toFloat()
        val sizes = bitmap.sizes
        val scaledSize = sizes / 2
        val scaleDown = bitmap.resizeIfNotEqual(scaledSize)
        val process = JavaBlurProcess().blur(scaleDown, rad)
        scaleDown.recycleSafely()
        process?.let {
            val scaleUp = Bitmap.createScaledBitmap(it, sizes.x, sizes.y, false)
            it.recycleSafely()
            drawBitmapSafely(scaleUp)
            scaleUp.recycleSafely()
        }
    }
}
