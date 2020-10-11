package org.illegaller.ratabb.hishoot2i.data.pref

import android.graphics.Bitmap.CompressFormat
import entity.DayNightMode

interface SettingPref {
    var customFontPath: String?
    var systemFontEnable: Boolean
    var dayNightMode: DayNightMode
    var compressFormat: CompressFormat
    var saveQuality: Int
    var saveNotificationEnable: Boolean
}
