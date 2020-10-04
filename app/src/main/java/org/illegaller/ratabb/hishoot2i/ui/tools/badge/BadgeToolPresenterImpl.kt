package org.illegaller.ratabb.hishoot2i.ui.tools.badge

import androidx.annotation.ColorInt
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import org.illegaller.ratabb.hishoot2i.data.pref.BadgeToolPref
import org.illegaller.ratabb.hishoot2i.data.rx.SchedulerProvider
import org.illegaller.ratabb.hishoot2i.data.rx.ioUI
import org.illegaller.ratabb.hishoot2i.data.source.FileFontStorageSource
import org.illegaller.ratabb.hishoot2i.ui.common.BasePresenter
import javax.inject.Inject

class BadgeToolPresenterImpl @Inject constructor(
    private val fileFontStorageSource: FileFontStorageSource,
    private val schedulerProvider: SchedulerProvider,
    private val badgeToolPref: BadgeToolPref
) : BadgeToolPresenter, BasePresenter<BadgeView>() {
    private val disposables: CompositeDisposable =
        CompositeDisposable()
    private val tempPath = mutableListOf("DEFAULT")
    override fun attachView(view: BadgeView) {
        super.attachView(view)
        view.onEmit(badgeToolPref)
        fileFontStorageSource.fileFonts()
            .map { it.absolutePath }
            .ioUI(schedulerProvider)
            .subscribeBy(view::onError, ::setUpDataAdapter, tempPath::plusAssign)
            .addTo(disposables)
    }

    override fun detachView() {
        super.detachView()
        disposables.clear()
    }

    override fun setBadgeColor(@ColorInt color: Int) {
        badgeToolPref.badgeColor = color
    }

    override fun setBadgeFont(position: Int): Boolean { //
        val absolutePath = tempPath[position]
        badgeToolPref.badgeTypefacePath = absolutePath
        return true
    }

    private fun setUpDataAdapter() {
        val current = badgeToolPref.badgeTypefacePath
        val currentIndex = tempPath.indexOf(current).coerceAtLeast(minimumValue = 0)
        requiredView().submitListAdapter(tempPath, currentIndex, badgeToolPref.badgeEnable)
    }
}
