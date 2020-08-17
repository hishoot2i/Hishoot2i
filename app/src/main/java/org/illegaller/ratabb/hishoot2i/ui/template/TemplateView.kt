package org.illegaller.ratabb.hishoot2i.ui.template

import org.illegaller.ratabb.hishoot2i.ui.common.Mvp
import template.Template

interface TemplateView : Mvp.View{
    fun setData(data: List<Template>)
    fun showProgress()
    fun hideProgress()
}

