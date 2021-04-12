@file:Suppress("NOTHING_TO_INLINE")

package common.text

import android.os.Build.VERSION.SDK_INT
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

inline infix fun Long.toDateTimeFormat(pattern: String): String = SimpleDateFormat(
    pattern,
    if (SDK_INT >= 24) Locale.getDefault(Locale.Category.FORMAT) else Locale.getDefault()
).format(Date(this))
