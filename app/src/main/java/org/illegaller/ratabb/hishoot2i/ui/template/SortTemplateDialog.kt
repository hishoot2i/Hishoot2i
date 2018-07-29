package org.illegaller.ratabb.hishoot2i.ui.template

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatDialog
import android.view.View
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.data.pref.AppPref
import org.illegaller.ratabb.hishoot2i.ui.common.BaseDialogFragment
import rbb.hishoot2i.common.ext.preventMultipleClick
import rbb.hishoot2i.template.TemplateComparator
import javax.inject.Inject

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
        when (appPref.templateSortId) {
            TemplateComparator.NAME_ASC_ID -> nameAsc
            TemplateComparator.NAME_DESC_ID -> nameDesc
            TemplateComparator.TYPE_ASC_ID -> typeAsc
            TemplateComparator.TYPE_DESC_ID -> typeDesc
            TemplateComparator.DATE_ASC_ID -> dateAsc
            TemplateComparator.DATE_DESC_ID -> dateDesc
            else -> nameAsc //
        }.also { view: View ->
            view.setBackgroundResource(R.drawable.sort_selected)
        }
    }
}