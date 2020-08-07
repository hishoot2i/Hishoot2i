package org.illegaller.ratabb.hishoot2i.ui.common

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentManager

abstract class BaseDialogFragment : AppCompatDialogFragment() {
    private var isShow: Boolean = false

    @LayoutRes
    protected abstract fun layoutRes(): Int

    protected abstract fun tagName(): String

    @CheckResult
    protected abstract fun createDialog(context: Context): Dialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        createDialog(requireContext())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layoutRes(), container, false)

    fun show(fragmentManager: FragmentManager) {
        if (isShow) return
        isShow = true
        show(fragmentManager, tagName())
    }

    override fun onDismiss(dialog: DialogInterface) {
        isShow = false
        super.onDismiss(dialog)
    }

    override fun onDetach() {
        isShow = false
        super.onDetach()
    }
}