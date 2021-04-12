package org.illegaller.ratabb.hishoot2i.ui.tools.badge

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckedTextView
import common.graphics.DEFAULT_TYPEFACE_KEY
import common.graphics.typeFaceOrDefault
import org.illegaller.ratabb.hishoot2i.R

class FontAdapter : BaseAdapter() {

    private var currentPosition: Int = 0
    private var data: List<String> = emptyList()

    fun current(current: Int): String =
        getItemAsString(current).also { currentPosition = current }

    fun submitList(data: List<String>, current: Int) {
        this.data = data
        currentPosition = current
        notifyDataSetChanged()
    }

    private fun getItemAsString(position: Int): String =
        getItem(position) as String

    override fun getCount(): Int = data.size

    override fun getItem(position: Int): Any = data[position]
    override fun hasStableIds(): Boolean = true
    override fun getItemId(position: Int): Long = getItem(position).hashCode().toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
        createView(position, convertView, parent, false)

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View =
        createView(position, convertView, parent, true)

    private val inflateLayout: (ViewGroup, Int) -> View = { parent, layout ->
        LayoutInflater.from(parent.context).inflate(layout, parent, false)
    }

    private fun createView(
        position: Int,
        convertView: View?,
        parent: ViewGroup,
        isDropDownView: Boolean
    ): View {
        val view = convertView ?: inflateLayout(parent, R.layout.item_font_adapter)
        (view as CheckedTextView).apply {
            val itemAsString = getItemAsString(position)
            text = itemAsString.toLabel()
            typeface = itemAsString.typeFaceOrDefault()
            if (isDropDownView) isChecked = position == currentPosition
        }
        return view
    }

    private fun String.toLabel(): String = when (this) {
        DEFAULT_TYPEFACE_KEY -> this
        else -> substringAfterLast('/').substringBeforeLast('.')
            .replace("[_-]".toRegex(), replacement = " ") //
            .capitalizeWord(" ")
    }

    @SuppressLint("DefaultLocale")
    private fun String.capitalizeWord(delimiter: String): String = buildString {
        this@capitalizeWord.split(delimiter).forEach { word -> append("${word.capitalize()} ") }
    }.trim()
}
