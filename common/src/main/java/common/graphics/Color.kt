@file:Suppress("NOTHING_TO_INLINE")

package common.graphics

import android.content.Context
import android.graphics.Color
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.res.use
import androidx.core.graphics.ColorUtils

inline val @receiver:ColorInt Int.halfAlpha: Int
    get() = ColorUtils.setAlphaComponent(this, 127)

@ColorInt
inline fun Context.themeColorOrDefault(@AttrRes attrsId: Int, @ColorInt default: Int): Int =
    obtainStyledAttributes(intArrayOf(attrsId)).use { it.getColor(0, default) }

@get:ColorInt
val @receiver:ColorInt Int.lightOrDarkContrast: Int
    get() {
        val src = ColorUtils.setAlphaComponent(this, 0xFF)
        val light = ColorUtils.calculateContrast(src, Color.WHITE)
        val dark = ColorUtils.calculateContrast(src, Color.BLACK)
        return if (light > dark) Color.WHITE else Color.BLACK
    }
