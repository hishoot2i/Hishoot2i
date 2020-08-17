package org.illegaller.ratabb.hishoot2i.ui.tools.badge

import org.illegaller.ratabb.hishoot2i.data.pref.AppPref
import org.illegaller.ratabb.hishoot2i.ui.common.Mvp

interface BadgeView : Mvp.View {
    fun onEmit(appPref: AppPref)
    fun submitListAdapter(list: List<String>, current: Int)
}
