package org.illegaller.ratabb.hishoot2i.ui.main.tools.template

import org.illegaller.ratabb.hishoot2i.data.pref.AppPref
import org.illegaller.ratabb.hishoot2i.ui.common.Mvp
import rbb.hishoot2i.template.Template

interface TemplateToolView : Mvp.View {
    fun currentTemplate(template: Template, appPref: AppPref)
}