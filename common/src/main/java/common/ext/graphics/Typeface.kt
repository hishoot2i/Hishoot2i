@file:Suppress("NOTHING_TO_INLINE")

package common.ext.graphics

import android.graphics.Typeface

inline fun String.createFromFileOrDefault(): Typeface = try {
    Typeface.createFromFile(this)
} catch (e: Exception) {
    Typeface.DEFAULT
}
