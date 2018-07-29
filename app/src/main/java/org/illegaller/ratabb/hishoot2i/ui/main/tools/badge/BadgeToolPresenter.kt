package org.illegaller.ratabb.hishoot2i.ui.main.tools.badge

import android.support.annotation.ColorInt
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import org.illegaller.ratabb.hishoot2i.data.CacheFileTypefaces
import org.illegaller.ratabb.hishoot2i.data.FileFontStorageSource
import org.illegaller.ratabb.hishoot2i.data.pref.AppPref
import org.illegaller.ratabb.hishoot2i.data.rx.SchedulerProvider
import org.illegaller.ratabb.hishoot2i.data.rx.ioUI
import org.illegaller.ratabb.hishoot2i.ui.common.BasePresenter
import java.io.File
import javax.inject.Inject

class BadgeToolPresenter @Inject constructor(
    private val fileFontStorageSource: FileFontStorageSource,
    private val schedulerProvider: SchedulerProvider,
    private val appPref: AppPref
) : BasePresenter<BadgeView>() {
    private val disposables: CompositeDisposable = CompositeDisposable()
    private val tempPath = mutableListOf<String>().apply { add(0, "DEFAULT") }

    override fun attachView(view: BadgeView) {
        super.attachView(view)
        view.onEmit(appPref)
        fileFontStorageSource.fileFonts()
            .ioUI(schedulerProvider)
            .subscribeBy(view::onError, ::setUpDataAdapter, ::addToTemp)
            .addTo(disposables)
    }

    override fun detachView() {
        super.detachView()
        disposables.clear()
    }

    fun setBadgeColor(@ColorInt color: Int) {
        appPref.badgeColor = color
    }

    fun setBadgeFont(position: Int): Boolean { //
        val absolutePath = tempPath[position]
        appPref.badgeTypefacePath = absolutePath
        return CacheFileTypefaces.put(absolutePath)
    }

    private fun setUpDataAdapter() {
        val current = appPref.badgeTypefacePath
        val currentIndex = tempPath.indexOf(current).coerceAtLeast(minimumValue = 0)
        val listName: List<String> = tempPath.map {
            File(it).nameWithoutExtension
                .replace("[_-]".toRegex(), replacement = SPACE)
                .capitalizeEachWord()
        }
        view?.submitListAdapter(listName, currentIndex)
    }

    private fun addToTemp(file: File) {
        tempPath.add(file.absolutePath)
    }

    private fun String.capitalizeEachWord(): String {
        val ret = StringBuilder()
        split(SPACE).forEach { ret.append("${it.capitalize()} ") }
        return ret.toString().trim()
    }

    companion object {
        private const val SPACE = " "
    }
}