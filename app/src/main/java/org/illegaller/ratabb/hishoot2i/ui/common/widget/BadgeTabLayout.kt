package org.illegaller.ratabb.hishoot2i.ui.common.widget

import android.content.Context
import android.support.annotation.IntRange
import android.support.design.widget.TabLayout
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import android.widget.TextView
import org.illegaller.ratabb.hishoot2i.R
import rbb.hishoot2i.common.ext.inflateNotAttach
import rbb.hishoot2i.common.ext.isVisible

class BadgeTabLayout : TabLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(
        context: Context,
        attributeSet: AttributeSet,
        defStyle: Int
    ) : super(context, attributeSet, defStyle)

    private val tabBuilders = SparseArray<Builder>()
    fun with(position: Int): Builder =
        with(getTabAt(position) ?: throw IllegalStateException())

    fun with(tab: Tab): Builder {
        var builder = tabBuilders[tab.position]
        if (null == builder) {
            builder = Builder(this, tab)
            tabBuilders.put(tab.position, builder)
        }
        return builder
    }

    inner class Builder(parent: TabLayout, private val tab: Tab) {
        private val view: View = tab.customView ?: parent.inflateNotAttach(R.layout.title_tablayout)
        val titleView: TextView = view.findViewById(R.id.tabTitle)
        val iconView: AppCompatImageView = view.findViewById(R.id.tabIcon)
        private val badgeView: TextView = view.findViewById(R.id.tabBadge)
        private var hasBadge: Boolean = badgeView.isVisible
        @IntRange(from = 0L)
        var badgeCount: Int = 0
            set(value) {
                if (field == value) return
                field = value
                when {
                    value <= 0 -> {
                        hasBadge = false
                    }
                    value > 99 -> {
                        hasBadge = true
                        badgeView.setText(R.string.badge_tab_layout_max_count_overflow)
                    }
                    else -> {
                        hasBadge = true
                        badgeView.text = "$field"
                    }
                }
                badgeView.isVisible = hasBadge
                badgeView.invalidate()
            }

        fun build() {
            //
            badgeView.isVisible = hasBadge
            tab.customView = view
        }
    }
}