package org.illegaller.ratabb.hishoot2i.ui.setting

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import common.ext.preventMultipleClick
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.ui.common.BaseDialogFragment

class ThemeChooserDialog : BaseDialogFragment() {

    override fun layoutRes() = R.layout.dialog_theme_chooser

    override fun tagName() = "ThemeChooserDialog"

    override fun createDialog(context: Context): Dialog = AppCompatDialog(context).apply {
        setStyle(DialogFragment.STYLE_NO_FRAME, theme)
        setCanceledOnTouchOutside(false)
        setCancelable(false)
    }

    internal var callback: (Int) -> Unit = { _ -> Unit }
    private lateinit var themeRadioGroup: RadioGroup
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(view) {
            themeRadioGroup = findViewById(R.id.themeRadioGroup)
            handleDataArg() //
            themeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                callback(checkedIdToMode(checkedId))
                dismiss()
            }
            findViewById<Button>(R.id.action_theme_cancel)
                .setOnClickListener {
                    it.preventMultipleClick {
                        dismiss()
                    }
                }
        }

    }

    private fun handleDataArg() {
        arguments?.let { arg ->
            val selectedId = arg.getInt(ARG_SELECTED_THEME_ID, DEF_SELECTED)
            themeRadioGroup.check(selectedId)
        }
    }

    private fun checkedIdToMode(id: Int): Int = when (id) {
        R.id.themeLightRb -> MODE_NIGHT_NO
        R.id.themeDarkRb -> MODE_NIGHT_YES
        else -> MODE_NIGHT_FOLLOW_SYSTEM
    }

    companion object {
        private const val ARG_SELECTED_THEME_ID = "arg_selected_theme_id"
        private const val DEF_SELECTED = R.id.themeSysDefRb
        fun newInstance(mode: Int) = ThemeChooserDialog().apply {
            val selectedId = when (mode) {
                MODE_NIGHT_NO -> R.id.themeLightRb
                MODE_NIGHT_YES -> R.id.themeDarkRb
                else -> DEF_SELECTED
            }
            arguments = bundleOf(ARG_SELECTED_THEME_ID to selectedId)
        }
    }
}
