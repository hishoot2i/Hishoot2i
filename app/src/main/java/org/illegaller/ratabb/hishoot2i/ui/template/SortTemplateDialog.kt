package org.illegaller.ratabb.hishoot2i.ui.template

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import common.view.preventMultipleClick
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.databinding.DialogSortTemplateBinding
import org.illegaller.ratabb.hishoot2i.ui.ARG_SORT
import org.illegaller.ratabb.hishoot2i.ui.KEY_REQ_SORT
import template.TemplateComparator
import template.TemplateComparator.DATE_ASC
import template.TemplateComparator.DATE_DESC
import template.TemplateComparator.NAME_ASC
import template.TemplateComparator.NAME_DESC
import template.TemplateComparator.TYPE_ASC
import template.TemplateComparator.TYPE_DESC

class SortTemplateDialog : AppCompatDialogFragment() {
    private val args: SortTemplateDialogArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        super.onCreateDialog(savedInstanceState).apply {
            setStyle(DialogFragment.STYLE_NO_FRAME, theme)
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = DialogSortTemplateBinding.inflate(inflater, container, false).apply {
        // views order must sync with TemplateComparator#ordinal
        val views = arrayOf(
            actionSortNameAsc, actionSortNameDesc, actionSortTypeAsc,
            actionSortTypeDesc, actionSortDateAsc, actionSortDateDesc
        )
        // NOTE: are we need setBackground view to null ?
        views.forEach { it.background = null }
        // NOTE: emit | setBackground selected indicator.
        views[args.templateComparator.ordinal].setBackgroundResource(R.drawable.sort_selected)
        //
        val click: (View, TemplateComparator) -> Unit = { view, comparator ->
            view.preventMultipleClick {
                if (args.templateComparator != comparator) {
                    setFragmentResult(KEY_REQ_SORT, bundleOf(ARG_SORT to comparator.ordinal))
                }
                dismiss()
            }
        }
        actionSortNameAsc.setOnClickListener { click(it, NAME_ASC) }
        actionSortNameDesc.setOnClickListener { click(it, NAME_DESC) }
        actionSortTypeAsc.setOnClickListener { click(it, TYPE_ASC) }
        actionSortTypeDesc.setOnClickListener { click(it, TYPE_DESC) }
        actionSortDateAsc.setOnClickListener { click(it, DATE_ASC) }
        actionSortDateDesc.setOnClickListener { click(it, DATE_DESC) }
        //
        actionSortCancel.setOnClickListener { click(it, args.templateComparator) }
    }.run { root }
}
