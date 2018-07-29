package org.illegaller.ratabb.hishoot2i.ui.template.fragment

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.ui.common.asyncListDiffer
import rbb.hishoot2i.common.ext.graphics.color
import rbb.hishoot2i.common.ext.inflateNotAttach
import rbb.hishoot2i.common.ext.isVisible
import rbb.hishoot2i.common.ext.preventMultipleClick
import rbb.hishoot2i.common.ext.toDateTimeFormat
import rbb.hishoot2i.common.imageloader.ImageLoader
import rbb.hishoot2i.template.Template
import javax.inject.Inject

class TemplateAdapter @Inject constructor(
    private val imageLoader: ImageLoader,
    templateDiffCallback: TemplateDiffCallback
) : RecyclerView.Adapter<TemplateAdapter.TemplateHolder>() {

    init {
        setHasStableIds(true)
    }

    private val differ = asyncListDiffer(templateDiffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateHolder =
        with(parent) { inflateNotAttach(R.layout.row_item_template).let { TemplateHolder(it) } }

    override fun getItemId(position: Int): Long =
        differ.currentList[position].id.hashCode().toLong()

    override fun getItemCount(): Int = differ.currentList.size
    override fun onBindViewHolder(holder: TemplateHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    internal var clickItem: (Template) -> Unit = { _: Template -> }
    @Throws(IndexOutOfBoundsException::class)
    fun getItemAt(position: Int): Template = differ.currentList[position]

    @Throws(IndexOutOfBoundsException::class)
    fun removeItemAt(position: Int): Template {
        var itemRemoved = getItemAt(position)
        val list = differ.currentList.toMutableList().apply {
            itemRemoved = removeAt(position)
        }
        submitList(list, true)
        return itemRemoved
    }

    fun submitList(newList: List<Template>, isForceUpdateUI: Boolean) {
        differ.submitList(newList)
        if (isForceUpdateUI) notifyItemRangeChanged(0, itemCount) //
    }

    inner class TemplateHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val colorBackFav: Int = itemView.context.color(R.color.accent)
        private val colorBackDelete: Int = itemView.context.color(R.color.red)
        private val itemTemplateBack: View = itemView.findViewById(R.id.itemTemplateBack)
        private val itemIndicatorFav: View = itemView.findViewById(R.id.itemIndicatorFav)
        private val itemIndicatorDelete: View = itemView.findViewById(R.id.itemIndicatorDelete)
        private val itemTemplatePreview: ImageView = itemView.findViewById(R.id.itemTemplatePreview)
        private val itemTemplateName: TextView = itemView.findViewById(R.id.itemTemplateName)
        private val itemTemplateId: TextView = itemView.findViewById(R.id.itemTemplateId)
        private val itemTemplateInfo: TextView = itemView.findViewById(R.id.itemTemplateInfo)
        /*SwipeHelper*/
        internal val foregroundView: View = itemView.findViewById(R.id.itemTemplateForeground)

        init {
            itemView.setOnClickListener {
                it.preventMultipleClick {
                    clickItem(differ.currentList[adapterPosition])
                }
            }
        }

        /* SwipeHelper: Template Default can not deleted. */
        fun isItemDeletable(): Boolean = !differ.currentList[adapterPosition].isDefault

        fun handleIndicatorVisible(dX: Float, isActive: Boolean) {
            itemTemplateBack.isVisible = isActive
            val isFav = dX > 0
            itemIndicatorFav.isVisible = isFav
            itemIndicatorDelete.isVisible = !isFav
            (if (isFav) colorBackFav else colorBackDelete).let {
                itemTemplateBack.setBackgroundColor(it)
            }
        }

        fun bind(item: Template) {
            imageLoader.display(itemTemplatePreview, item.preview)
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