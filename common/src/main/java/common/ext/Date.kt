@file:Suppress("NOTHING_TO_INLINE")

package common.ext

import android.os.Build.VERSION.SDK_INT
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale.Category
import java.util.Locale.getDefault

@JvmOverloads
inline fun Long.toDateTimeFormat(pattern: String = "yyyy-MMM-dd HH:mm:ss"): String = when {
    SDK_INT >= 24 -> getDefault(Category.FORMAT)
    else -> getDefault()
}.let { SimpleDateFormat(pattern, it).format(Date(this)) }
