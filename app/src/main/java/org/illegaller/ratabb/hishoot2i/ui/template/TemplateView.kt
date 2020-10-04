package org.illegaller.ratabb.hishoot2i.ui.template

import org.illegaller.ratabb.hishoot2i.ui.common.Mvp
import template.Template
import template.Template.VersionHtz

interface TemplateView : Mvp.View {
    fun setData(templates: List<Template>)
    fun showProgress()
    fun hideProgress()
    fun htzImported(templateHtz: VersionHtz)
}

