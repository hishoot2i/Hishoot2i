package org.illegaller.ratabb.hishoot2i.ui.tools.template

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import org.illegaller.ratabb.hishoot2i.data.pref.TemplateToolPref
import org.illegaller.ratabb.hishoot2i.data.rx.SchedulerProvider
import org.illegaller.ratabb.hishoot2i.data.rx.ioUI
import org.illegaller.ratabb.hishoot2i.data.source.TemplateDataSource
import org.illegaller.ratabb.hishoot2i.ui.common.BasePresenter
import javax.inject.Inject

class TemplateToolPresenterImpl @Inject constructor(
    private val templateDataSource: TemplateDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val appPref: TemplateToolPref
) : TemplateToolPresenter, BasePresenter<TemplateToolView>() {
    private val disposables: CompositeDisposable = CompositeDisposable()
    override fun attachView(view: TemplateToolView) {
        super.attachView(view)
        templateDataSource.findByIdOrDefault(appPref.templateCurrentId)
            .ioUI(schedulerProvider)
            .subscribeBy(view::onError) { view.currentTemplate(it, appPref) }
            .addTo(disposables)
    }

    override fun detachView() {
        super.detachView()
        disposables.clear()
    }
}
