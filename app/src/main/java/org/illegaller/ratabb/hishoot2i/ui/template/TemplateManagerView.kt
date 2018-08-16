package org.illegaller.ratabb.hishoot2i.ui.template

import org.illegaller.ratabb.hishoot2i.ui.common.Mvp
import template.Template

interface TemplateManagerView : Mvp.View {
    fun onSuccessImportHtz(htz: Template.VersionHtz)
}