@file:Suppress("SpellCheckingInspection")

package org.illegaller.ratabb.hishoot2i.data.pref.impl

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import entity.BackgroundMode
import entity.BackgroundMode.COLOR
import entity.ImageOption
import entity.ImageOption.SCALE_FILL
import org.illegaller.ratabb.hishoot2i.data.pref.BackgroundToolPref
import pref.SimplePref
import pref.ext.asFlow
import pref.ext.booleanPref
import pref.ext.enumOrdinalPref
import pref.ext.intPref
import javax.inject.Inject

class BackgroundToolPrefImpl @Inject constructor(
    @ApplicationContext context: Context
) : BackgroundToolPref, SimplePref(context, "background_tool_pref") {
    override var backgroundMode: BackgroundMode by enumOrdinalPref(default = COLOR)
    override var imageOption: ImageOption by enumOrdinalPref(default = SCALE_FILL)
    override var backgroundColorInt: Int by intPref(default = -16711681) // 0xFF00FFFF
    override var backgroundImageBlurEnable: Boolean by booleanPref(default = false)
    override var backgroundImageBlurRadius: Int by intPref(default = 25)

    override val mainFlow = listOf(
        asFlow(::backgroundMode),
        asFlow(::backgroundColorInt),
        asFlow(::imageOption),
        asFlow(::backgroundImageBlurEnable),
        asFlow(::backgroundImageBlurRadius)
    )
}
