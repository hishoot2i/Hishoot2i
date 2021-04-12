@file:Suppress("NOTHING_TO_INLINE")

package common.graphics

import android.graphics.Typeface
import java.io.File

const val DEFAULT_TYPEFACE_KEY = "DEFAULT"

inline fun String?.typeFaceOrDefault(): Typeface = when (this) {
    null, DEFAULT_TYPEFACE_KEY -> Typeface.DEFAULT
    else -> File(this).takeIf(File::canRead)?.let { file ->
        try { // only try create typeface if file is can read.
            Typeface.createFromFile(file)
        } catch (_: Exception) {
            Typeface.DEFAULT
        }
    } ?: Typeface.DEFAULT
}
