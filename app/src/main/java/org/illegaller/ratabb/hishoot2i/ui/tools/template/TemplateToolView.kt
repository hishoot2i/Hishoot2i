package org.illegaller.ratabb.hishoot2i.ui.tools.template

import org.illegaller.ratabb.hishoot2i.data.pref.TemplateToolPref
import org.illegaller.ratabb.hishoot2i.ui.common.Mvp
import template.Template

interface TemplateToolView : Mvp.View {
    fun currentTemplate(template: Template, templateToolPref: TemplateToolPref)
}
