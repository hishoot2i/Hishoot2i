package org.illegaller.ratabb.hishoot2i.ui.tools.badge

import androidx.annotation.ColorInt
import org.illegaller.ratabb.hishoot2i.ui.common.Mvp.Presenter

interface BadgeToolPresenter : Presenter<BadgeView>{
    fun setBadgeColor(@ColorInt color: Int)
    fun setBadgeFont(position: Int): Boolean
}
