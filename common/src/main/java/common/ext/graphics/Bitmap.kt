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
import entity.Sizes
import entity.SizesF
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream

inline val Bitmap.sizes get() = Sizes(width, height)

@JvmOverloads
inline fun Bitmap.resizeIfNotEqual(reqSizes: Sizes, filter: Boolean = false): Bitmap =
    if (reqSizes != sizes) Bitmap.createScaledBitmap(this, reqSizes.x, reqSizes.y, filter)
    else this

inline fun Bitmap.applyCanvas(block: Canvas.() -> Unit): Bitmap {
    val canvas = Canvas(this)
    canvas.block()
    return this
}

@JvmOverloads
inline fun Sizes.createBitmap(config: Bitmap.Config = ARGB_8888): Bitmap =
    Bitmap.createBitmap(x, y, config)

inline fun File.bitmapSize(): Sizes? {
    inputStream().use { stream: FileInputStream ->
        BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeStream(stream, null, this)
            if (outWidth > 0 && outHeight > 0) {
                return Sizes(outWidth, outHeight)
            }
        }
    }
    return null
}

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
    return reqSizes.createBitmap(config = reqConfig ?: config).applyCanvas {
        drawBitmap(
            this@scaleCenterCrop,
            null,
            targetRect,
            DEFAULT_PAINT_BITMAP
        )
    }
}

@JvmOverloads
inline fun Bitmap.roundedLargeIcon(
    iconSize: Int,
    config: Bitmap.Config? = null
): Bitmap {
    val halfIconSize = iconSize * POINT_OF_FIVE
    val paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
    val iconSizes = Sizes(iconSize, iconSize)
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

@JvmOverloads
inline fun Bitmap.rotate(degree: Float = 90F): Bitmap {
    val matrix = Matrix().apply { postRotate(degree) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, false)
}

inline val Bitmap.isLandScape: Boolean get() = width > height
