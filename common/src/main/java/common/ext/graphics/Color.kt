@file:Suppress("NOTHING_TO_INLINE")

package common.ext.graphics

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils

inline val @receiver:ColorInt Int.alpha: Int
    get() = Color.alpha(this)
inline val @receiver:ColorInt Int.red: Int
    get() = Color.red(this)
inline val @receiver:ColorInt Int.green: Int
    get() = Color.green(this)
inline val @receiver:ColorInt Int.blue: Int
    get() = Color.blue(this)
inline val @receiver:ColorInt Int.halfAlpha: Int
    get() = ColorUtils.setAlphaComponent(this, 127)
inline val @receiver:ColorInt Int.invert: Int
    get() = (0xFFFFFFFF - this).toInt()
inline val @receiver:ColorInt Int.darker: Int
    get() = ColorUtils.blendARGB(this, Color.BLACK, .5F)

inline fun @receiver:ColorInt Int.toHexString(): String =
    Integer.toHexString(this).apply {
        return when (length) {
            1 -> "0$this" //
            else -> this
        }
    }

inline fun @receiver:ColorInt Int.toPairWithHex(): Pair<Int, String> =
    Pair(this, toHexString())

@ColorInt
inline fun Context.color(@ColorRes colorResId: Int): Int =
    ContextCompat.getColor(this, colorResId)
