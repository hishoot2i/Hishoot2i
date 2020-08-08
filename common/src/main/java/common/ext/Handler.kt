package common.ext

import android.os.Handler

const val DEFAULT_DELAY_MS: Long = 600L // milliseconds
@JvmOverloads
inline fun postDelayed(
    durationOnMillis: Long = DEFAULT_DELAY_MS,
    crossinline block: () -> Unit
) {
    Handler().postDelayed({ block() }, durationOnMillis)
}
