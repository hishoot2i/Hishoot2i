package org.illegaller.ratabb.hishoot2i.ui.main.tools

import android.app.Dialog
import android.content.Context
import androidx.annotation.ColorInt
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.illegaller.ratabb.hishoot2i.ui.common.BaseDialogFragment

abstract class AbsTools : BaseDialogFragment() {
    override fun createDialog(context: Context): Dialog = BottomSheetDialog(context, theme)
    var callback: ChangeImageSourcePath? = null
    override fun onDestroyView() {
        callback = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        callback = null
        super.onDestroy()
    }

    override fun onDetach() {
        callback = null
        super.onDetach()
    }

    interface ChangeImageSourcePath {
        fun changePathScreen1(path: String)
        fun changePathScreen2(path: String)
        fun changePathBackground(path: String)
        fun startingPipette(@ColorInt srcColor: Int)
    }
}
