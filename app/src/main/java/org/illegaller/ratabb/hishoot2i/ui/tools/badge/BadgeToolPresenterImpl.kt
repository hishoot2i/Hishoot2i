package org.illegaller.ratabb.hishoot2i.ui.tools.badge

import androidx.annotation.ColorInt
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.illegaller.ratabb.hishoot2i.data.pref.BadgeToolPref
import org.illegaller.ratabb.hishoot2i.data.source.FileFontSource
import org.illegaller.ratabb.hishoot2i.ui.common.BasePresenter
import javax.inject.Inject

class BadgeToolPresenterImpl @Inject constructor(
    private val fileFontSource: FileFontSource,
    private val badgeToolPref: BadgeToolPref
) : BadgeToolPresenter, BasePresenter<BadgeView>() {
    private val tempPath = mutableListOf("DEFAULT")
    override fun attachView(view: BadgeView) {
        super.attachView(view)
        view.onEmit(badgeToolPref)
        launch {
            runCatching { withContext(IO) { getFileFontsPath().onEach { tempPath += it } } }
                .fold({ setUpDataAdapter() }, view::onError)
        }
    }

    override fun setBadgeColor(@ColorInt color: Int) {
        badgeToolPref.badgeColor = color
    }

    override fun setBadgeFont(position: Int): Boolean { //
        val absolutePath = tempPath[position]
        badgeToolPref.badgeTypefacePath = absolutePath
        return true
    }

    private suspend fun getFileFontsPath() =
        fileFontSource.fileFonts().map { it.absolutePath }

    private fun setUpDataAdapter() {
        val current = badgeToolPref.badgeTypefacePath
        val currentIndex = tempPath.indexOf(current).coerceAtLeast(minimumValue = 0)
        requiredView().submitListAdapter(tempPath, currentIndex, badgeToolPref.badgeEnable)
    }
}
