package org.illegaller.ratabb.hishoot2i.ui.tools.badge

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckedTextView
import android.widget.TextView
import androidx.appcompat.widget.ThemedSpinnerAdapter
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import common.ext.graphics.createFromFileOrDefault
import common.ext.graphics.drawable
import org.illegaller.ratabb.hishoot2i.R
import java.io.File
import kotlin.LazyThreadSafetyMode.NONE

class FontAdapter(context: Context) : BaseAdapter(), ThemedSpinnerAdapter {
    private val checkMark by lazy(NONE) {
        context.drawable(R.drawable.ic_done_black_24dp)?.also {
            DrawableCompat.setTint(it, ContextCompat.getColor(context, R.color.accent))
        }
    }

    //private val inflater by lazy(NONE) { LayoutInflater.from(context) }
    private val helper by lazy { ThemedSpinnerAdapter.Helper(context) }
    private val data = mutableListOf<String>() // absolute path font file.
    private var selectedPosition: Int = 0
    override fun getItem(position: Int): Any = data[position]
    override fun getItemId(position: Int): Long = position.toLong()
    override fun hasStableIds(): Boolean = true
    override fun getCount(): Int = data.size

    override fun getDropDownViewTheme(): Resources.Theme? = helper.dropDownViewTheme

    override fun setDropDownViewTheme(theme: Resources.Theme?) {
        helper.dropDownViewTheme = theme
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val ret = convertView ?: helper.dropDownViewInflater.inflate(
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

        @SuppressLint("DefaultLocale")
        @JvmStatic
        private fun String.capitalizeEachWord(): String {
            val ret = StringBuilder()
            split(SPACE).forEach { ret.append("${it.capitalize()} ") }
            return ret.toString().trim()
        }
    }
}
