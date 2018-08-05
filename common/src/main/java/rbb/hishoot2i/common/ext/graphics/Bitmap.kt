@file:Suppress("NOTHING_TO_INLINE")

package rbb.hishoot2i.common.ext.graphics

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.support.annotation.Px
import rbb.hishoot2i.common.custombitmap.AlphaPatternBitmap
import rbb.hishoot2i.common.entity.Sizes
import rbb.hishoot2i.common.entity.SizesF
import rbb.hishoot2i.common.ext.POINT_OF_FIVE
import rbb.hishoot2i.common.ext.deviceHeight
import rbb.hishoot2i.common.ext.deviceWidth
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
inline fun Sizes.createBitmap(config: Bitmap.Config? = ARGB_8888): Bitmap =
    Bitmap.createBitmap(x, y, config)

inline fun File.bitmapSize(): Sizes? {
    inputStream().use { stream: FileInputStream ->
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(stream, null, options)
        if (options.outWidth > 0 && options.outHeight > 0) {
            return Sizes(options.outWidth, options.outHeight)
        }
    }
    return null
}

val DEFAULT_PAINT_BITMAP = Paint(Paint.FILTER_BITMAP_FLAG)
inline fun Bitmap.scaleCenterCrop(reqSizes: Sizes, reqConfig: Bitmap.Config? = null): Bitmap {
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

inline fun Bitmap.roundedLargeIcon(iconSize: Int, config: Bitmap.Config? = null): Bitmap {
    val halfIconSize = iconSize * POINT_OF_FIVE
    val paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
    val iconSizes = Sizes(iconSize, iconSize)
    return iconSizes.createBitmap(config = config ?: ARGB_8888).applyCanvas {
        drawCircle(halfIconSize, halfIconSize, halfIconSize, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        drawBitmapSafely(this@roundedLargeIcon.resizeIfNotEqual(iconSizes, true), paint = paint)
    }
}

inline fun Context.alphaPatternBitmap(sizes: Sizes): Bitmap = alphaPatternBitmap(sizes.x, sizes.y)
@JvmOverloads
inline fun Context.alphaPatternBitmap(
    @Px width: Int = deviceWidth,
    @Px height: Int = deviceHeight
): Bitmap = AlphaPatternBitmap(this).create(width = width, height = height)

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