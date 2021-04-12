package org.illegaller.ratabb.hishoot2i.ui.setting

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import common.view.preventMultipleClick
import entity.DayNightMode
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.databinding.DialogThemeChooserBinding
import org.illegaller.ratabb.hishoot2i.ui.ARG_THEME
import org.illegaller.ratabb.hishoot2i.ui.KEY_REQ_THEME

class ThemeChooserDialog : AppCompatDialogFragment() {
    private val args: ThemeChooserDialogArgs by navArgs()

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
    ): View = DialogThemeChooserBinding.inflate(inflater, container, false).apply {
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
    }.run { root }

    @get:IdRes
    private inline val DayNightMode.resId: Int
        get() = when (this) {
            DayNightMode.LIGHT -> R.id.themeLightRb
            DayNightMode.DARK -> R.id.themeDarkRb
            DayNightMode.SYSTEM -> R.id.themeSysDefRb
        }

    private fun DayNightMode.Companion.fromIdRes(@IdRes idRes: Int): DayNightMode = when (idRes) {
        R.id.themeLightRb -> DayNightMode.LIGHT
        R.id.themeDarkRb -> DayNightMode.DARK
        R.id.themeSysDefRb -> DayNightMode.SYSTEM
        else -> DayNightMode.SYSTEM // fallback
    }
}
