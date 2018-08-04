package org.illegaller.ratabb.hishoot2i.ui.template

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import org.illegaller.ratabb.hishoot2i.ui.template.fragment.favorite.FavoriteFragment
import org.illegaller.ratabb.hishoot2i.ui.template.fragment.installed.InstalledFragment

class TemplatePagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val data = arrayOf(
        TemplateManagerActivity.POSITION_INSTALLED,
        TemplateManagerActivity.POSITION_FAVORITE
    )
    override fun getItem(position: Int): Fragment = when (position) {
        TemplateManagerActivity.POSITION_INSTALLED -> InstalledFragment()
        TemplateManagerActivity.POSITION_FAVORITE -> FavoriteFragment()
        else -> throw IllegalStateException("TemplatePagerAdapter unknown position:$position")
    }

    override fun getCount(): Int = data.size
}