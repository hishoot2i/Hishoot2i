package org.illegaller.ratabb.hishoot2i.ui.crop

import android.net.Uri
import org.illegaller.ratabb.hishoot2i.ui.common.Mvp

interface CropView : Mvp.View {
    fun onErrorCrop(throwable: Throwable)
    fun onSuccessCrop(uri: Uri)
}
