package org.illegaller.ratabb.hishoot2i.ui.common

import androidx.recyclerview.widget.DiffUtil

class DiffUtilItemCallback<T : Any>(
    private val itemSame: (T, T) -> Boolean,
    private val contentSame: (T, T) -> Boolean,
    private val payload: (T, T) -> Any? = { _, _ -> null }
) : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = itemSame(oldItem, newItem)
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = contentSame(oldItem, newItem)
    override fun getChangePayload(oldItem: T, newItem: T): Any? = payload(oldItem, newItem)
}
