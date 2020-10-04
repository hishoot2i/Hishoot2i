package org.illegaller.ratabb.hishoot2i.ui.crop

import android.graphics.Bitmap
import org.illegaller.ratabb.hishoot2i.ui.common.Mvp.Presenter

interface CropPresenter : Presenter<CropView> {
    fun saveCrop(bitmap: Bitmap)
}
