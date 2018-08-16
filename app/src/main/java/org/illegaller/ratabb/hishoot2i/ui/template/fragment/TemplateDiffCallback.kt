package org.illegaller.ratabb.hishoot2i.ui.template.fragment

import android.support.v7.util.DiffUtil
import template.Template
import javax.inject.Inject

/*TODO: Payload? */
class TemplateDiffCallback @Inject constructor() : DiffUtil.ItemCallback<Template>() {
    /*override fun getChangePayload(oldItem: Template?, newItem: Template?): Any {
        return super.getChangePayload(oldItem, newItem)
    }*/
    override fun areItemsTheSame(oldItem: Template, newItem: Template): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Template, newItem: Template): Boolean =
        oldItem == newItem
}