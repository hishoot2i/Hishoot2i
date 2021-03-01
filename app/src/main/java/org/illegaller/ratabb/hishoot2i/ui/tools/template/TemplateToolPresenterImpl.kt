package org.illegaller.ratabb.hishoot2i.ui.tools.template

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.illegaller.ratabb.hishoot2i.data.pref.TemplateToolPref
import org.illegaller.ratabb.hishoot2i.data.source.TemplateSource
import org.illegaller.ratabb.hishoot2i.ui.common.BasePresenter
import javax.inject.Inject

class TemplateToolPresenterImpl @Inject constructor(
    private val templateSource: TemplateSource,
    private val appPref: TemplateToolPref
) : TemplateToolPresenter, BasePresenter<TemplateToolView>() {

    override fun attachView(view: TemplateToolView) {
        super.attachView(view)
        launch {
            runCatching {
                withContext(IO) { templateSource.findByIdOrDefault(appPref.templateCurrentId) }
            }.fold({ view.currentTemplate(it, appPref) }, view::onError)
        }
    }
}
