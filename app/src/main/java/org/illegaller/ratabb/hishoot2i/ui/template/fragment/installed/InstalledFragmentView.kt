package org.illegaller.ratabb.hishoot2i.ui.template.fragment.installed

import org.illegaller.ratabb.hishoot2i.ui.common.Mvp
import template.Template

interface InstalledFragmentView : Mvp.View {
    fun setData(data: List<Template>)
    fun showProgress()
    fun hideProgress()
}
