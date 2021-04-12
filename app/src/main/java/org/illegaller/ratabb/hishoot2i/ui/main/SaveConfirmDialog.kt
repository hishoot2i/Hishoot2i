package org.illegaller.ratabb.hishoot2i.ui.main

import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import common.widget.setOnItemSelected
import dagger.hilt.android.AndroidEntryPoint
import org.illegaller.ratabb.hishoot2i.data.pref.SettingPref
import org.illegaller.ratabb.hishoot2i.databinding.DialogSaveConfirmBinding
import org.illegaller.ratabb.hishoot2i.ui.KEY_REQ_SAVE
import javax.inject.Inject

@AndroidEntryPoint
class SaveConfirmDialog : AppCompatDialogFragment() {

    @Inject
    lateinit var settingPref: SettingPref

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
    ): View = DialogSaveConfirmBinding.inflate(inflater, container, false).apply {
        dialogSaveQuality.apply {
            isEnabled = settingPref.compressFormat != Bitmap.CompressFormat.PNG
            value = settingPref.saveQuality.toFloat()
            addOnChangeListener { _, value, _ ->
                settingPref.saveQuality = value.toInt()
            }
        }
        dialogSaveFormat.apply {
            setSelection(settingPref.compressFormat.ordinal, false)
            setOnItemSelected { _, _, position, _ ->
                if (position != settingPref.compressFormat.ordinal) {
                    settingPref.compressFormat = Bitmap.CompressFormat.values()[position]
                    dialogSaveQuality.isEnabled =
                        settingPref.compressFormat != Bitmap.CompressFormat.PNG
                }
            }
        }
        actionSaveCancel.setOnClickListener { dismiss() }
        actionSaveOk.setOnClickListener {
            setFragmentResult(KEY_REQ_SAVE, bundleOf())
            dismiss()
        }
    }.run { root }
}
