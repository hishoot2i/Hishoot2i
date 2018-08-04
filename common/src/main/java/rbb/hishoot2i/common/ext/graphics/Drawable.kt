@file:Suppress("NOTHING_TO_INLINE")

package rbb.hishoot2i.common.ext.graphics

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.Px
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.content.ContextCompat
import android.support.v7.content.res.AppCompatResources

@JvmOverloads
inline fun Drawable.toBitmap(
    @Px width: Int = intrinsicWidth,
    @Px height: Int = intrinsicHeight,
    config: Bitmap.Config? = null
): Bitmap {
    if (this is BitmapDrawable) {
        if (config == null || config == bitmap.config) {
            if (width == intrinsicWidth && height == intrinsicHeight) {
                return bitmap
            }
            return Bitmap.createScaledBitmap(bitmap, width, height, true)
        }
    }
    val oldBounds = bounds
    val bitmap = Bitmap.createBitmap(width, height, config ?: ARGB_8888)
    setBounds(0, 0, width, height)
    draw(Canvas(bitmap))
    bounds = oldBounds
    return bitmap
}

inline fun Context.drawable(@DrawableRes drawableId: Int): Drawable? =
    AppCompatResources.getDrawable(this, drawableId)

inline fun Context.createVectorDrawableTint(
    @DrawableRes id: Int,
    @ColorRes tint: Int
): Drawable? = if (0 == id) null else VectorDrawableCompat.create(resources, id, theme)
    ?.also { it.setTint(ContextCompat.getColor(this, tint)) }