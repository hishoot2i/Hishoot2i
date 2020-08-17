package org.illegaller.ratabb.hishoot2i.ui.template

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.util.ObjectsCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import common.ext.inflateNotAttach
import common.ext.itemDiffCallback
import common.ext.preventMultipleClick
import common.ext.toDateTimeFormat
import imageloader.ImageLoader
import org.illegaller.ratabb.hishoot2i.R
import template.Template
import javax.inject.Inject

private val ITEM_DIFF by itemDiffCallback<Template>(
    itemSame = { o, n -> ObjectsCompat.equals(o.id, n.id) },
    contentSame = { o, n -> ObjectsCompat.equals(o, n) }
)

class TemplateAdapter @Inject constructor(
    imageLoader: ImageLoader
) : ListAdapter<Template, TemplateAdapter.TemplateHolder>(ITEM_DIFF) {

    private val imageDisplay: (ImageView, String) -> Unit by lazy {
        { view: ImageView, path: String ->
            imageLoader.display(view, path)
        }
    }

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

    inner class TemplateHolder(
        parent: ViewGroup
    ) : RecyclerView.ViewHolder(
        parent.inflateNotAttach(R.layout.row_item_template)
    ) {

        private val itemTemplatePreview: ImageView =
            itemView.findViewById(R.id.itemTemplatePreview)
        private val itemTemplateName: TextView =
            itemView.findViewById(R.id.itemTemplateName)
        private val itemTemplateId: TextView =
            itemView.findViewById(R.id.itemTemplateId)
        private val itemTemplateInfo: TextView =
            itemView.findViewById(R.id.itemTemplateInfo)

        init {
            itemView.setOnClickListener {
                it.preventMultipleClick {
                    clickItem(getItem(adapterPosition))
                }
            }
        }

        fun bind(item: Template) {
            imageDisplay(itemTemplatePreview, item.preview)
            itemTemplateName.text = item.name
            itemTemplateId.text = item.id
            with(itemTemplateInfo) {
                text = context.getString(
                    R.string.template_info_format,
                    item.author,
                    item.desc,
                    item.installedDate.toDateTimeFormat()
                )
            }
        }
    }
}
