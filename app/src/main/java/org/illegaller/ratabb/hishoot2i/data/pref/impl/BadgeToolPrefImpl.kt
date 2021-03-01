@file:Suppress("SpellCheckingInspection")

package org.illegaller.ratabb.hishoot2i.data.pref.impl

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import entity.BadgePosition
import entity.BadgePosition.CENTER_BOTTOM
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.illegaller.ratabb.hishoot2i.data.pref.BadgeToolPref
import pref.SimplePref
import pref.ext.asFlow
import pref.ext.booleanPref
import pref.ext.enumOrdinalPref
import pref.ext.floatPref
import pref.ext.intPref
import pref.ext.stringPref
import javax.inject.Inject

class BadgeToolPrefImpl @Inject constructor(
    @ApplicationContext context: Context
) : BadgeToolPref, SimplePref(context, "badge_tool_pref") {
    override var badgePosition: BadgePosition by enumOrdinalPref(default = CENTER_BOTTOM)
    override var badgeColor: Int by intPref(default = -65536) // 0xFFFF0000
    override var badgeEnable: Boolean by booleanPref(default = true)
    override var badgeSize: Float by floatPref(default = 25F)
    override var badgeText: String by stringPref(default = "HISHOOT")
    private val quot = "\""
    private var badgeTypeface: String? by stringPref() //
    override var badgeTypefacePath: String?
        get() = badgeTypeface?.removeSurrounding(quot)
        set(value) {
            val valuePlusQuote = "$quot$value$quot"
            if (badgeTypeface != valuePlusQuote) {
                badgeTypeface = valuePlusQuote
            }
        }
    @ExperimentalCoroutinesApi
    override val mainFlow = listOf(
        asFlow(::badgeTypeface),
        asFlow(::badgePosition),
        asFlow(::badgeColor),
        asFlow(::badgeEnable),
        asFlow(::badgeSize),
        asFlow(::badgeText)
    )
}
