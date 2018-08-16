package org.illegaller.ratabb.hishoot2i.ui.template.fragment.favorite

import org.illegaller.ratabb.hishoot2i.ui.common.Mvp
import template.Template

interface FavoriteFragmentView : Mvp.View {
    fun setData(data: List<Template>)
    fun showProgress()
    fun hideProgress()
}