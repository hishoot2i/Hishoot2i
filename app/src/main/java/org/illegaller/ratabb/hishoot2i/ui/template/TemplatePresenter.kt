package org.illegaller.ratabb.hishoot2i.ui.template

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import org.illegaller.ratabb.hishoot2i.data.TemplateDataSource
import org.illegaller.ratabb.hishoot2i.data.pref.AppPref
import org.illegaller.ratabb.hishoot2i.data.rx.SchedulerProvider
import org.illegaller.ratabb.hishoot2i.data.rx.delayed
import org.illegaller.ratabb.hishoot2i.data.rx.ioUI
import org.illegaller.ratabb.hishoot2i.ui.common.BasePresenter
import template.Template
import template.TemplateComparator
import javax.inject.Inject

class TemplatePresenter @Inject constructor(
    private val templateDataSource: TemplateDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val appPref: AppPref
) : BasePresenter<TemplateView>() {

    private val disposables: CompositeDisposable = CompositeDisposable()
    private val tempData = mutableListOf<Template>()

    override fun detachView() {
        super.detachView()
        tempData.clear()
        disposables.clear()
    }

    fun search(queryObservable: Observable<String>) {
        queryObservable.delayed()
            .observeOn(schedulerProvider.ui())
            .subscribeBy(::viewOnError) { query: String ->
                val filteredData = mutableListOf<Template>()
                if (query.isEmpty()) {
                    filteredData.addAll(tempData)
                } else {
                    tempData.filter { it.containsNameOrAuthor(query) }
                        .also { filteredData.addAll(it) }
                }
                viewSetData(filteredData)
            }
            .addTo(disposables)
    }

    fun render() {
        requiredView().showProgress()
        tempData.clear()
        templateDataSource.allTemplate()
            .sorted(TemplateComparator.fromId(appPref.templateSortId))
            .ioUI(schedulerProvider)
            .subscribeBy(
                onError = ::viewOnError,
                onComplete = { viewSetData(tempData) },
                onNext = tempData::plusAssign
            )
            .addTo(disposables)
    }

    fun setCurrentTemplate(template: Template): Boolean {
        if (appPref.templateCurrentId != template.id) {
            appPref.templateCurrentId = template.id
            return true
        }
        return false
    }

    private fun viewOnError(e: Throwable) {
        with(requiredView()) {
            onError(e)
            hideProgress()
        }
    }

    private fun viewSetData(data: List<Template>) {
        with(requiredView()) {
            setData(data)
            hideProgress()
        }
    }
}