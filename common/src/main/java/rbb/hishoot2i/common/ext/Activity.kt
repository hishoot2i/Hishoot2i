@file:Suppress("NOTHING_TO_INLINE")

package rbb.hishoot2i.common.ext

import android.app.Activity
import android.app.ActivityManager.TaskDescription
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.RequiresApi

@JvmOverloads
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
inline fun Activity.taskDescription(
    label: String? = null,
    icon: Bitmap? = null,
    @ColorInt colorPrimary: Int = Color.BLACK
) {
    setTaskDescription(TaskDescription(label, icon, colorPrimary))
}

/*
inline fun Activity.windowAnimations(@StyleRes animStyle: Int) {
    window.setWindowAnimations(animStyle)
}*/
