package org.illegaller.ratabb.hishoot2i.ui.main.tools.template

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import org.illegaller.ratabb.hishoot2i.data.TemplateDataSource
import org.illegaller.ratabb.hishoot2i.data.pref.AppPref
import org.illegaller.ratabb.hishoot2i.data.rx.SchedulerProvider
import org.illegaller.ratabb.hishoot2i.data.rx.ioUI
import org.illegaller.ratabb.hishoot2i.ui.common.BasePresenter
import javax.inject.Inject

class TemplateToolPresenter @Inject constructor(
    private val templateDataSource: TemplateDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val appPref: AppPref
) : BasePresenter<TemplateToolView>() {
    private val disposables: CompositeDisposable = CompositeDisposable()
    override fun attachView(view: TemplateToolView) {
        super.attachView(view)
        templateDataSource.findById(appPref.templateCurrentId)
            .ioUI(schedulerProvider)
            .subscribeBy(view::onError) { view.currentTemplate(it, appPref) }
            .addTo(disposables)
    }

    override fun detachView() {
        super.detachView()
        disposables.clear()
    }
}