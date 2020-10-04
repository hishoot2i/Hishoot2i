package org.illegaller.ratabb.hishoot2i.data.core

import android.graphics.Bitmap
import template.Template

interface MixTemplate {
    fun mixed(template: Template, config: Config, ss: String?, isSave: Boolean): Bitmap

    data class Config(val isFrame: Boolean, val isGlare: Boolean, val isShadow: Boolean)
}
