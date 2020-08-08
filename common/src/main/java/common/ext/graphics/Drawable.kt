@file:Suppress("NOTHING_TO_INLINE")

package common.ext.graphics

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat

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
