package org.illegaller.ratabb.hishoot2i.ui.main.tools.badge

import android.content.Context
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckedTextView
import android.widget.TextView
import common.ext.graphics.createFromFileOrDefault
import common.ext.graphics.drawable
import org.illegaller.ratabb.hishoot2i.R
import java.io.File
import kotlin.LazyThreadSafetyMode.NONE

class FontAdapter(context: Context) : BaseAdapter() {
    private val checkMark by lazy(NONE) {
        context.drawable(R.drawable.ic_done_black_24dp)?.also {
            DrawableCompat.setTint(it, ContextCompat.getColor(context, R.color.accent))
        }
    }
    private val inflater by lazy(NONE) { LayoutInflater.from(context) }
    private val data = mutableListOf<String>() // absolute path font file.
    private var selectedPosition: Int = 0
    override fun getItem(position: Int): Any = data[position]
    override fun getItemId(position: Int): Long = position.toLong()
    override fun hasStableIds(): Boolean = true
    override fun getCount(): Int = data.size
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val ret = convertView ?: inflater.inflate(
            android.R.layout.simple_spinner_dropdown_item,
            parent,
            false
        )
        (ret as? TextView)?.text = getLabel(position)
        ret.tag = position
        return ret
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val ret = getView(position, convertView, parent)
        (ret as? CheckedTextView)?.apply {
            typeface = getTypeface(position)
            checkMarkDrawable = if (position == selectedPosition) checkMark else null
        }
        return ret
    }

    internal fun setSelection(position: Int) {
        selectedPosition = position
    }

    internal fun submitList(list: List<String>, position: Int) {
        data.clear()
        data.addAll(list)
        selectedPosition = position
        notifyDataSetChanged()
    }

    private fun getItemAsString(position: Int): String = getItem(position) as String
    private fun getLabel(position: Int): String =
        getItemAsString(position).let {
            File(it).nameWithoutExtension
                .replace("[_-]".toRegex(), replacement = SPACE)
                .capitalizeEachWord()
        }

    private fun getTypeface(position: Int): Typeface =
        getItemAsString(position).createFromFileOrDefault()

    companion object {
        private const val SPACE = " "
        @JvmStatic private fun String.capitalizeEachWord(): String {
            val ret = StringBuilder()
            split(SPACE).forEach { ret.append("${it.capitalize()} ") }
            return ret.toString().trim()
        }
    }
}