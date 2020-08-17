@file:Suppress("NOTHING_TO_INLINE")

package common.ext

import android.app.Activity
import android.app.ActivityManager.TaskDescription
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import kotlin.math.roundToInt

@JvmOverloads
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
inline fun Activity.taskDescription(
    label: String? = null,
    icon: Bitmap? = null,
    @ColorInt colorPrimary: Int = Color.BLACK
) {
    @Suppress("DEPRECATION")
    setTaskDescription(TaskDescription(label, icon, colorPrimary))
}

/*
inline fun Activity.windowAnimations(@StyleRes animStyle: Int) {
    window.setWindowAnimations(animStyle)
}*/
// Add these extension functions to an empty kotlin file
fun Activity.getRootView(): View {
    return findViewById<View>(android.R.id.content)
}

fun Activity.isKeyboardOpen(): Boolean {
    val visibleBounds = Rect()
    this.getRootView().getWindowVisibleDisplayFrame(visibleBounds)
    val heightDiff = getRootView().height - visibleBounds.height()
    val marginOfError = this.dp2px(50F).roundToInt()
    return heightDiff > marginOfError
}

fun Activity.isKeyboardClosed(): Boolean {
    return !this.isKeyboardOpen()
}
