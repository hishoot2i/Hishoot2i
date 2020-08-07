@file:Suppress("NOTHING_TO_INLINE")

package common.ext

import android.app.Activity
import android.app.ActivityManager.TaskDescription
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi

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
