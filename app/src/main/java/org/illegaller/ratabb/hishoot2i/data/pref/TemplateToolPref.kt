package org.illegaller.ratabb.hishoot2i.data.pref

import kotlinx.coroutines.flow.Flow

interface TemplateToolPref {
    var templateCurrentId: String
    var templateFrameEnable: Boolean
    var templateGlareEnable: Boolean
    var templateShadowEnable: Boolean
    val mainFlow: List<Flow<*>>
}
