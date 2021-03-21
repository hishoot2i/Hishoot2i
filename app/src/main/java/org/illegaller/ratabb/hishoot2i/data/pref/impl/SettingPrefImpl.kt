package org.illegaller.ratabb.hishoot2i.data.pref.impl

import android.content.Context
import android.graphics.Bitmap.CompressFormat
import android.graphics.Bitmap.CompressFormat.PNG
import dagger.hilt.android.qualifiers.ApplicationContext
import entity.DayNightMode
import entity.DayNightMode.SYSTEM
import org.illegaller.ratabb.hishoot2i.data.pref.SettingPref
import pref.SimplePref
import pref.ext.booleanPref
import pref.ext.enumOrdinalPref
import pref.ext.intPref
import pref.ext.stringPref
import javax.inject.Inject

class SettingPrefImpl @Inject constructor(
    @ApplicationContext context: Context
) : SettingPref, SimplePref(context, "setting_pref") {
    private val quot = "\""
    private var customFont: String? by stringPref()
    override var customFontPath: String?
        get() = customFont?.removeSurrounding(quot)
        set(value) {
            val valuePlusQuote = "$quot$value$quot"
            if (valuePlusQuote != customFont) {
                customFont = valuePlusQuote
            }
        }
    override var systemFontEnable: Boolean by booleanPref(default = false)
    override var dayNightMode: DayNightMode by enumOrdinalPref(default = SYSTEM)
    override var compressFormat: CompressFormat by enumOrdinalPref(default = PNG)
    override var saveQuality: Int by intPref(default = 100)
    override var saveNotificationEnable: Boolean by booleanPref(default = true)
    override var saveConfirmEnable: Boolean by booleanPref(default = true)
}
