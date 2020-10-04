package org.illegaller.ratabb.hishoot2i.ui.template

import io.reactivex.Observable
import org.illegaller.ratabb.hishoot2i.ui.common.Mvp
import template.Template
import template.TemplateComparator
import java.io.File

interface TemplatePresenter : Mvp.Presenter<TemplateView> {
    fun search(queryObservable: Observable<String>)
    fun importHtz(htz: File)
    fun render()
    fun setCurrentTemplate(template: Template): Boolean
    var templateComparator: TemplateComparator
}
