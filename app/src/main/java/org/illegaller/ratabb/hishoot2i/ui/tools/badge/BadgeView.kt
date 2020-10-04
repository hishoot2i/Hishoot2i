package org.illegaller.ratabb.hishoot2i.ui.tools.badge

import org.illegaller.ratabb.hishoot2i.data.pref.BadgeToolPref
import org.illegaller.ratabb.hishoot2i.ui.common.Mvp

interface BadgeView : Mvp.View {
    fun onEmit(badgeToolPref: BadgeToolPref)
    fun submitListAdapter(list: List<String>, current: Int, enable: Boolean)
}
