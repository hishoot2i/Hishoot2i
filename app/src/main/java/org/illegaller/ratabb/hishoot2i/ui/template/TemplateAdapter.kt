package org.illegaller.ratabb.hishoot2i.ui.template

import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.util.ObjectsCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import common.ext.deviceWidth
import common.ext.dpSize
import common.ext.layoutInflater
import common.ext.preventMultipleClick
import entity.Sizes
import imageloader.ImageLoader
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.databinding.RowItemTemplateBinding
import org.illegaller.ratabb.hishoot2i.ui.common.DiffUtilItemCallback
import org.illegaller.ratabb.hishoot2i.ui.template.TemplateAdapter.TemplateHolder
import template.Template
import javax.inject.Inject

class TemplateAdapter @Inject constructor(
    imageLoader: ImageLoader
) : ListAdapter<Template, TemplateHolder>(
    DiffUtilItemCallback<Template>(
        itemSame = { o, n -> ObjectsCompat.equals(o.id, n.id) },
        contentSame = { o, n -> ObjectsCompat.equals(o, n) }
    )
) {

    private val imageDisplay: (ImageView, String, Sizes) -> Unit = (imageLoader::display)

    init {
        setHasStableIds(true) //
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateHolder =
        TemplateHolder(parent)

    override fun getItemId(position: Int): Long =
        getItem(position).id.hashCode().toLong() //

    override fun onBindViewHolder(holder: TemplateHolder, position: Int) {
        holder.bind(getItem(position))
    }

    internal var clickItem: (Template) -> Unit = { _: Template -> }

    inner class TemplateHolder : ViewHolder {
        private val binding: RowItemTemplateBinding

        constructor(parent: ViewGroup) : this(
            RowItemTemplateBinding.inflate(parent.layoutInflater, parent, false)
        )

        private constructor(binding: RowItemTemplateBinding) : super(binding.root) {
            this.binding = binding
            itemView.setOnClickListener {
                it.preventMultipleClick {
                    clickItem(getItem(adapterPosition))
                }
            }
        }

        fun bind(item: Template) {
            binding.apply {
                itemTemplatePreview.apply {
                    val (reqWidth, reqHeight) = context.run {
                        deviceWidth to dpSize(R.dimen.itemPreviewHeight)
                    }
                    imageDisplay(this, item.preview, Sizes(reqWidth, reqHeight))
                }
                itemTemplateName.text = item.name
            }
        }
    }
}
