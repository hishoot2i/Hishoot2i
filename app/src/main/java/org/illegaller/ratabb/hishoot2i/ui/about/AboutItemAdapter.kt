package org.illegaller.ratabb.hishoot2i.ui.about

import android.view.ViewGroup
import androidx.core.util.ObjectsCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import common.ext.layoutInflater
import org.illegaller.ratabb.hishoot2i.databinding.RowItemAboutBinding
import org.illegaller.ratabb.hishoot2i.ui.about.AboutItemAdapter.AboutItemHolder
import org.illegaller.ratabb.hishoot2i.ui.common.DiffUtilItemCallback
import javax.inject.Inject

class AboutItemAdapter @Inject constructor(
) : ListAdapter<AboutItem, AboutItemHolder>(
    DiffUtilItemCallback<AboutItem>(
        itemSame = { o, n -> ObjectsCompat.equals(o.title, n.title) },
        contentSame = { o, n -> ObjectsCompat.equals(o, n) })
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AboutItemHolder {
        return AboutItemHolder(parent)
    }

    override fun onBindViewHolder(holder: AboutItemHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AboutItemHolder : ViewHolder {
        private val itemAboutBinding: RowItemAboutBinding

        private constructor(binding: RowItemAboutBinding) : super(binding.root) {
            this.itemAboutBinding = binding
        }

        constructor(parent: ViewGroup) : this(
            RowItemAboutBinding.inflate(parent.layoutInflater, parent, false)
        )

        fun bind(item: AboutItem) {
            itemAboutBinding.run {
                aboutItemIcon.setImageResource(item.icon)
                aboutItemTitle.text = item.title
                //aboutItemSub.text = item.sub
            }
        }
    }
}