package org.illegaller.ratabb.hishoot2i.ui.template.fragment.installed

import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import common.ext.exhaustive
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.data.TemplateDataSource
import org.illegaller.ratabb.hishoot2i.data.pref.AppPref
import org.illegaller.ratabb.hishoot2i.data.rx.SchedulerProvider
import org.illegaller.ratabb.hishoot2i.data.rx.delayed
import org.illegaller.ratabb.hishoot2i.data.rx.ioUI
import org.illegaller.ratabb.hishoot2i.ui.common.BasePresenter
import org.illegaller.ratabb.hishoot2i.ui.common.rx.RxSearchView
import template.Template
import template.TemplateComparator
import javax.inject.Inject

class InstalledFragmentPresenter @Inject constructor(
    private val templateDataSource: TemplateDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val appPref: AppPref
) : BasePresenter<InstalledFragmentView>() {
    private val disposables: CompositeDisposable = CompositeDisposable()
    private val tempData = mutableListOf<Template>()
    private var menuSort: MenuItem? = null
    override fun detachView() {
        super.detachView()
        tempData.clear()
        disposables.clear()
    }

    fun setUpMenu(menu: Menu) {
        val searchView: SearchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.context.getString(R.string.search_template_hint)
            ?.let { searchView.queryHint = it }
        RxSearchView.queryTextChange(searchView)
            .delayed()
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
        //
        menuSort = menu.findItem(R.id.action_sort_template)
        setIconMenuSort()
    }

    private fun setIconMenuSort() {
        val templateComparator = TemplateComparator.fromId(appPref.templateSortId)
        when (templateComparator) {
            is TemplateComparator.NameAsc -> R.drawable.ic_sort_az_up_black_24dp
            is TemplateComparator.NameDesc -> R.drawable.ic_sort_az_down_black_24dp
            is TemplateComparator.TypeAsc -> R.drawable.ic_sort_type_up_black_24dp
            is TemplateComparator.TypeDesc -> R.drawable.ic_sort_type_down_black_24dp
            is TemplateComparator.DateAsc -> R.drawable.ic_sort_clock_up_black_24dp
            is TemplateComparator.DateDesc -> R.drawable.ic_sort_clock_down_black_24dp
        }.exhaustive.also { menuSort?.setIcon(it) }
    }

    fun render() {
        setIconMenuSort()
        view?.showProgress()
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

    fun addRemoveTemplateFav(template: Template, isRemove: (Boolean) -> Unit) {
        appPref.templateFavSet.apply {
            if (contains(template.id)) {
                remove(template.id)
                isRemove(true)
            } else {
                add(template.id)
                isRemove(false)
            }
        }
    }

    fun setCurrentTemplate(template: Template): Boolean {
        if (appPref.templateCurrentId != template.id) {
            appPref.templateCurrentId = template.id
            return true
        }
        return false
    }

    private fun viewOnError(e: Throwable) {
        view?.apply {
            onError(e)
            hideProgress()
        }
    }

    private fun viewSetData(data: List<Template>) {
        view?.apply {
            setData(data)
            hideProgress()
        }
    }
}