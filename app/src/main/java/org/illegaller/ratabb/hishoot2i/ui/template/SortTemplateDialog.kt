package org.illegaller.ratabb.hishoot2i.ui.template

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDialog
import androidx.fragment.app.DialogFragment
import common.ext.exhaustive
import common.ext.preventMultipleClick
import dagger.hilt.android.AndroidEntryPoint
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.data.pref.AppPref
import org.illegaller.ratabb.hishoot2i.ui.common.BaseDialogFragment
import template.TemplateComparator
import javax.inject.Inject

@AndroidEntryPoint
class SortTemplateDialog : BaseDialogFragment() {
    @Inject
    lateinit var appPref: AppPref
    var callback: () -> Unit = {}

    //
    private lateinit var nameAsc: View
    private lateinit var nameDesc: View
    private lateinit var typeAsc: View
    private lateinit var typeDesc: View
    private lateinit var dateAsc: View
    private lateinit var dateDesc: View

    //
    override fun tagName(): String = "SortTemplateDialog"

    override fun layoutRes(): Int = R.layout.dialog_sort_template
    override fun createDialog(context: Context): Dialog = AppCompatDialog(context).apply {
        setStyle(DialogFragment.STYLE_NO_FRAME, theme)
        setCanceledOnTouchOutside(false)
        setCancelable(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(view) {
            nameAsc = findViewById(R.id.action_sort_name_asc)
            nameDesc = findViewById(R.id.action_sort_name_desc)
            typeAsc = findViewById(R.id.action_sort_type_asc)
            typeDesc = findViewById(R.id.action_sort_type_desc)
            dateAsc = findViewById(R.id.action_sort_date_asc)
            dateDesc = findViewById(R.id.action_sort_date_desc)
        }
        emitSelectedSortItem()
        setViewListener()
    }

    private fun setViewListener() {
        nameAsc.setOnClickListener { onClick(it, TemplateComparator.NAME_ASC_ID) }
        nameDesc.setOnClickListener { onClick(it, TemplateComparator.NAME_DESC_ID) }
        typeAsc.setOnClickListener { onClick(it, TemplateComparator.TYPE_ASC_ID) }
        typeDesc.setOnClickListener { onClick(it, TemplateComparator.TYPE_DESC_ID) }
        dateAsc.setOnClickListener { onClick(it, TemplateComparator.DATE_ASC_ID) }
        dateDesc.setOnClickListener { onClick(it, TemplateComparator.DATE_DESC_ID) }
    }

    private fun onClick(view: View, sortId: Int) {
        with(view) {
            preventMultipleClick {
                if (appPref.templateSortId != sortId) {
                    setBackgroundResource(R.drawable.sort_selected)
                    appPref.templateSortId = sortId
                    callback()
                }
                dismiss()
            }
        }
    }

    private fun disSelectedAll() {
        nameAsc.background = null
        nameDesc.background = null
        typeAsc.background = null
        typeDesc.background = null
        dateAsc.background = null
        dateDesc.background = null
    }

    private fun emitSelectedSortItem() {
        disSelectedAll()
        val templateComparator = TemplateComparator.fromId(appPref.templateSortId)
        when (templateComparator) {
            is TemplateComparator.NameAsc -> nameAsc
            is TemplateComparator.NameDesc -> nameDesc
            is TemplateComparator.TypeAsc -> typeAsc
            is TemplateComparator.TypeDesc -> typeDesc
            is TemplateComparator.DateAsc -> dateAsc
            is TemplateComparator.DateDesc -> dateDesc
        }.exhaustive.also { it.setBackgroundResource(R.drawable.sort_selected) }
    }
}
