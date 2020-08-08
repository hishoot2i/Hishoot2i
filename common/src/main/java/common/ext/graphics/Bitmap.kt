@file:Suppress("NOTHING_TO_INLINE")

package common.ext.graphics

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import common.ext.POINT_OF_FIVE
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream

inline val Bitmap.sizes get() = entity.Sizes(width, height)

@JvmOverloads
inline fun Bitmap.resizeIfNotEqual(reqSizes: entity.Sizes, filter: Boolean = false): Bitmap =
    if (reqSizes != sizes) Bitmap.createScaledBitmap(this, reqSizes.x, reqSizes.y, filter)
    else this

inline fun Bitmap.applyCanvas(block: Canvas.() -> Unit): Bitmap {
    val canvas = Canvas(this)
    canvas.block()
    return this
}

@JvmOverloads
inline fun entity.Sizes.createBitmap(config: Bitmap.Config = ARGB_8888): Bitmap =
    Bitmap.createBitmap(x, y, config)

inline fun File.bitmapSize(): entity.Sizes? {
    inputStream().use { stream: FileInputStream ->
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(stream, null, options)
        if (options.outWidth > 0 && options.outHeight > 0) {
            return entity.Sizes(options.outWidth, options.outHeight)
        }
    }
    return null
}

val DEFAULT_PAINT_BITMAP = Paint(Paint.FILTER_BITMAP_FLAG)
inline fun Bitmap.scaleCenterCrop(
    reqSizes: entity.Sizes,
    reqConfig: Bitmap.Config? = null
): Bitmap {
    val (xScale, yScale) = (reqSizes.toSizeF() / sizes.toSizeF())
    val scale = xScale.coerceAtLeast(yScale)
    val scaled = sizes.toSizeF() * scale
    val (left, top) = (reqSizes.toSizeF() - scaled) * POINT_OF_FIVE
    val (right, bottom) = (scaled + entity.SizesF(left, top)) + POINT_OF_FIVE
    val targetRect = RectF(left, top, right, bottom)
    return reqSizes.createBitmap(config = reqConfig ?: config).applyCanvas {
        drawBitmap(
            this@scaleCenterCrop,
            null,
            targetRect,
            DEFAULT_PAINT_BITMAP
        )
    }
}

inline fun Bitmap.roundedLargeIcon(iconSize: Int, config: Bitmap.Config? = null): Bitmap {
    val halfIconSize = iconSize * POINT_OF_FIVE
    val paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
    val iconSizes = entity.Sizes(iconSize, iconSize)
    return iconSizes.createBitmap(config = config ?: ARGB_8888).applyCanvas {
        drawCircle(halfIconSize, halfIconSize, halfIconSize, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        drawBitmapSafely(this@roundedLargeIcon.resizeIfNotEqual(iconSizes, true), paint = paint)
    }
}

@JvmOverloads
inline fun Bitmap.saveTo(
    file: File,
    compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
    quality: Int = 100
) {
    BufferedOutputStream(file.outputStream()).use { stream: BufferedOutputStream ->
        compress(compressFormat, quality, stream)
        stream.flush()
    }
}

inline fun Bitmap.recycleSafely() {
    if (!isRecycled) {
        try {
            recycle()
        } catch (ignore: Exception) {
        }
    }
}

inline fun Bitmap.rotate(degree: Float = 90F): Bitmap {
    val matrix = Matrix().apply { postRotate(degree) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, false)
}
