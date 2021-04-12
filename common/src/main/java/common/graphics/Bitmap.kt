@file:Suppress("NOTHING_TO_INLINE")

package common.graphics

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import common.content.POINT_OF_FIVE
import entity.Sizes
import entity.SizesF
import java.io.File

inline val Bitmap.sizes get() = Sizes(width, height)

val DEFAULT_PAINT_BITMAP = Paint(Paint.FILTER_BITMAP_FLAG)

inline fun Bitmap.scaleCenterCrop(
    reqSizes: Sizes,
    reqConfig: Bitmap.Config? = null
): Bitmap {
    val (xScale, yScale) = (reqSizes.toSizeF() / sizes.toSizeF())
    val scale = xScale.coerceAtLeast(yScale)
    val scaled = sizes.toSizeF() * scale
    val (left, top) = (reqSizes.toSizeF() - scaled) * POINT_OF_FIVE
    val (right, bottom) = (scaled + SizesF(left, top)) + POINT_OF_FIVE
    val targetRect = RectF(left, top, right, bottom)
    return createBitmap(reqSizes.x, reqSizes.y, config = reqConfig ?: config).applyCanvas {
        drawBitmap(this@scaleCenterCrop, null, targetRect, DEFAULT_PAINT_BITMAP)
    }
}

const val MAX_QUALITY_SAVE_BITMAP = 100

@JvmOverloads
inline fun Bitmap.saveTo(
    file: File,
    compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
    quality: Int = MAX_QUALITY_SAVE_BITMAP
) {
    file.outputStream().buffered().use { stream ->
        compress(compressFormat, quality.coerceIn(0..MAX_QUALITY_SAVE_BITMAP), stream)
        stream.flush()
    }
}

inline fun Bitmap.toBitmapShader(tileX: Shader.TileMode, tileY: Shader.TileMode) =
    BitmapShader(this, tileX, tileY)
