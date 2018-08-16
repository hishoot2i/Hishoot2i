package org.illegaller.ratabb.hishoot2i.ui.template.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import common.ext.actionUninstallApk
import common.ext.exhaustive
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.ui.common.BaseFragment
import org.illegaller.ratabb.hishoot2i.ui.template.TemplateManagerActivity
import template.Template
import javax.inject.Inject

abstract class AbsTemplateFragment : BaseFragment(), SwipeHelper.Listener {
    @Inject
    lateinit var adapter: TemplateAdapter
    private var positionUninstallTemplate: Int = -1
    protected var isFav: Boolean = false
    protected var isForceUpdateUI: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.template_list, menu)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_UNINSTALL_APP && positionUninstallTemplate > 0) {
            if (resultCode == Activity.RESULT_OK) {
                adapter.removeItemAt(positionUninstallTemplate) //
                if (!isFav) updateTabBadgeInstalled(adapter.itemCount)
                else updateTabBadgeFav(adapter.itemCount)
            } else {
                adapter.notifyItemChanged(positionUninstallTemplate)
            }
        }
    }

    protected fun tryUninstallTemplate(position: Int, template: Template?): Boolean {
        return try {
            when (template) {
                is Template.Version1,
                is Template.Version2,
                is Template.Version3
                -> {
                    startActivityForResult(actionUninstallApk(template.id), REQ_UNINSTALL_APP)
                    positionUninstallTemplate = position
                    true
                }
                is Template.VersionHtz -> {
                    // TODO: delete files at background thread?
                    // File(template.id).deleteRecursively()
                    // positionUninstallTemplate = position
                    // true
                    false
                }
                is Template.Default,
                is Template.Empty,
                null -> false
            }.exhaustive
        } catch (e: Exception) {
            false
        }
    }

    protected fun updateTabBadgeInstalled(count: Int): Unit? =
        updateTabBadge(TemplateManagerActivity.POSITION_INSTALLED, count)

    protected fun updateTabBadgeFav(count: Int): Unit? =
        updateTabBadge(TemplateManagerActivity.POSITION_FAVORITE, count)

    private fun updateTabBadge(position: Int, count: Int): Unit? =
        (activity as? TemplateManagerActivity)?.updateTabBadge(position, count)

    companion object {
        const val REQ_UNINSTALL_APP = 0x1234
    }
}