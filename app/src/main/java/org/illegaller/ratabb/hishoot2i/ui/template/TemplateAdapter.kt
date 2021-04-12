package org.illegaller.ratabb.hishoot2i.ui.template

import android.view.View
import android.view.ViewGroup
import androidx.core.util.ObjectsCompat
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import common.view.layoutInflater
import common.view.preventMultipleClick
import imageloader.ImageLoader
import org.illegaller.ratabb.hishoot2i.databinding.RowItemTemplateBinding
import org.illegaller.ratabb.hishoot2i.ui.common.DiffUtilItemCallback
import org.illegaller.ratabb.hishoot2i.ui.template.TemplateAdapter.TemplateHolder
import template.Template
import javax.inject.Inject

class TemplateAdapter @Inject constructor(
    imageLoader: ImageLoader
) : ListAdapter<Template, TemplateHolder>(DIFF_ITEM), ImageLoader by imageLoader {

    private companion object {
        val DIFF_ITEM = DiffUtilItemCallback<Template>(
            itemSame = { o, n -> ObjectsCompat.equals(o.id, n.id) },
            contentSame = { o, n -> ObjectsCompat.equals(o, n) }
        )
    }

    internal var clickItem: (Template) -> Unit = { _ -> }

    internal var longClickItem: (View, Template) -> Boolean = { _, _ -> false }

    inner class TemplateHolder(val binding: RowItemTemplateBinding) : ViewHolder(binding.root)

    init {
        setHasStableIds(true) //
    }

    override fun getItemId(position: Int): Long = getItem(position).id.hashCode().toLong() //

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = TemplateHolder(RowItemTemplateBinding.inflate(parent.layoutInflater, parent, false)).apply {
        itemView.setOnClickListener {
            it.preventMultipleClick { clickItem(getItem(absoluteAdapterPosition)) }
        }
        itemView.setOnLongClickListener {
            longClickItem(it, getItem(absoluteAdapterPosition))
        }
    }

    override fun onBindViewHolder(
        holder: TemplateHolder,
        position: Int
    ) {
        val item = getItem(position)
        with(holder.binding) {
            itemTemplatePreview.let { display(it, it.findViewTreeLifecycleOwner(), item.preview) }
            itemTemplateName.text = item.name
        }
    }
}
