@file:Suppress("unused", "NOTHING_TO_INLINE")

package common.ext

import android.app.Activity
import android.graphics.Rect
import android.view.View
import androidx.core.view.WindowInsetsCompat
import timber.log.Timber
import kotlin.math.roundToInt

fun Activity.getRootView(): View = findViewById(android.R.id.content)
/* Failed */
fun Activity.isKeyboardOpen(): Boolean {
    val visibleBounds = Rect()
    this.getRootView().getWindowVisibleDisplayFrame(visibleBounds)
    val heightDiff = getRootView().height - visibleBounds.height()
    val marginOfError = this.dp2px(100F).roundToInt()
    Timber.d("heightDiff:$heightDiff marginOfError:$marginOfError")
    return heightDiff > marginOfError
}
