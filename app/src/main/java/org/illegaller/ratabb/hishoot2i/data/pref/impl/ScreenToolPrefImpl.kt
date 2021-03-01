package org.illegaller.ratabb.hishoot2i.data.pref.impl

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.illegaller.ratabb.hishoot2i.data.pref.ScreenToolPref
import pref.SimplePref
import pref.ext.asFlow
import pref.ext.booleanPref
import javax.inject.Inject

class ScreenToolPrefImpl @Inject constructor(
    @ApplicationContext context: Context
) : ScreenToolPref, SimplePref(context, "screen_tool_pref") {
    override var doubleScreenEnable: Boolean by booleanPref(default = false)
    @ExperimentalCoroutinesApi
    override val mainFlow = listOf(asFlow(::doubleScreenEnable))
}
