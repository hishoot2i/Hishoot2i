package org.illegaller.ratabb.hishoot2i.data.pref.impl

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.illegaller.ratabb.hishoot2i.data.pref.TemplateToolPref
import pref.SimplePref
import pref.ext.asFlow
import pref.ext.booleanPref
import pref.ext.stringPref
import template.TemplateConstants.DEFAULT_TEMPLATE_ID
import javax.inject.Inject

class TemplateToolPrefImpl @Inject constructor(
    @ApplicationContext context: Context
) : TemplateToolPref, SimplePref(context, "template_tool_pref") {
    override var templateCurrentId: String by stringPref(default = DEFAULT_TEMPLATE_ID)
    override var templateFrameEnable: Boolean by booleanPref(default = true)
    override var templateGlareEnable: Boolean by booleanPref(default = true)
    override var templateShadowEnable: Boolean by booleanPref(default = true)
    @ExperimentalCoroutinesApi
    override val mainFlow = listOf(
        asFlow(::templateFrameEnable),
        asFlow(::templateGlareEnable),
        asFlow(::templateShadowEnable)
    )
}
