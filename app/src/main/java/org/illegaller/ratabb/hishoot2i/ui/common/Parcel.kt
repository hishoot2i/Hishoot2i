package org.illegaller.ratabb.hishoot2i.ui.common

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.Q
import android.os.Parcel

fun Parcel.readBooleanCompat(): Boolean = if (SDK_INT >= Q) readBoolean() else readInt() != 0
fun Parcel.writeBooleanCompat(value: Boolean) {
    if (SDK_INT >= Q) writeBoolean(value) else writeInt(if (value) 1 else 0)
}
