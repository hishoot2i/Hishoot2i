@file:Suppress("NOTHING_TO_INLINE")

package common.ext.graphics

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import androidx.annotation.AttrRes
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
/*inline val @receiver:ColorInt Int.invert: Int
    get() = (0xFFFFFFFF - this).toInt()
inline val @receiver:ColorInt Int.darker: Int
    get() = ColorUtils.blendARGB(this, Color.BLACK, .5F)*/

inline fun @receiver:ColorInt Int.toHexString(): String =
    Integer.toHexString(this).apply {
        return when (length) {
            1 -> "0$this" //
            else -> this
        }
    }

inline fun @receiver:ColorInt Int.toPairWithHex(): Pair<Int, String> =
    this to toHexString()

@ColorInt
inline fun Context.color(@ColorRes colorResId: Int): Int =
    ContextCompat.getColor(this, colorResId)

@ColorInt
inline fun Context.themeColorOrDefault(@AttrRes attrsId: Int, @ColorInt default: Int): Int {
    val ret: Int
    val ta: TypedArray = obtainStyledAttributes(intArrayOf(attrsId))
    ret = ta.getColor(0, default)
    ta.recycle()
    return ret
}

@get:ColorInt
val @receiver:ColorInt Int.lightOrDarkContrast: Int
    get() {
        val src = ColorUtils.setAlphaComponent(this, 0xFF)
        val light = ColorUtils.calculateContrast(src, Color.WHITE)
        val dark = ColorUtils.calculateContrast(src, Color.BLACK)
        return if (light > dark) Color.WHITE else Color.BLACK
    }

/* */
@ColorInt
fun String.colorFromHex(isWithAlpha: Boolean, @ColorInt fallback: Int): Int {
    var ret = fallback
    val value = this
    try {
        ret = Color.parseColor(value)
    } catch (e: IndexOutOfBoundsException) {
        ret = fallback
    } catch (e: IllegalArgumentException) {
        if (value[0] != '#') {
            ret = when (value.length) {
                // RGB -> #AARRGGBB {A=FF: full alpha }
                3 -> "#FF${value[0]}${value[0]}${value[1]}${value[1]}${value[2]}${value[2]}"
                    .colorFromHex(isWithAlpha, fallback)
                // RRGGBB -> #AARRGGBB {A=FF: full alpha }
                6 -> "#FF$value".colorFromHex(isWithAlpha, fallback)
                // AARRGGBB -> #AARRGGBB {if not isWithAlpha A=FF: full alpha }
                8 -> "#${if (isWithAlpha) value else "FF${value.substring(2)}"}"
                    .colorFromHex(isWithAlpha, fallback)
                else -> fallback
            }
        }
    }
    return ret
}
