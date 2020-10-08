package org.illegaller.ratabb.hishoot2i.data.pref

import io.reactivex.rxjava3.core.Flowable

interface TemplateToolPref {
    var templateCurrentId: String
    var templateFrameEnable: Boolean
    var templateGlareEnable: Boolean
    var templateShadowEnable: Boolean
    val mainFlow: List<Flowable<*>>
}
