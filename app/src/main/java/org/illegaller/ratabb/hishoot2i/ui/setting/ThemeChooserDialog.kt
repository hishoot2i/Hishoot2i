package org.illegaller.ratabb.hishoot2i.ui.setting

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import common.ext.preventMultipleClick
import entity.DayNightMode
import entity.fromIdRes
import entity.resId
import org.illegaller.ratabb.hishoot2i.databinding.DialogThemeChooserBinding
import org.illegaller.ratabb.hishoot2i.ui.ARG_THEME
import org.illegaller.ratabb.hishoot2i.ui.KEY_REQ_THEME

class ThemeChooserDialog : AppCompatDialogFragment() {
    private val args: ThemeChooserDialogArgs by navArgs()

    private var dialogBinding: DialogThemeChooserBinding? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AppCompatDialog(context).apply {
            setStyle(DialogFragment.STYLE_NO_FRAME, theme)
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DialogThemeChooserBinding.inflate(inflater, container, false)
        dialogBinding = binding
        binding.apply {
            actionThemeCancel.setOnClickListener { it.preventMultipleClick { dismiss() } }
            themeRadioGroup.apply {
                check(args.dayNightMode.resId)
                addOnButtonCheckedListener { _, checkedId, _ ->
                    setFragmentResult(
                        KEY_REQ_THEME,
                        bundleOf(ARG_THEME to DayNightMode.fromIdRes(checkedId).ordinal)
                    )
                    dismiss()
                }
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        dialogBinding?.themeRadioGroup?.clearOnButtonCheckedListeners()
        dialogBinding = null
        super.onDestroyView()
    }
}
