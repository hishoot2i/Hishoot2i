package org.illegaller.ratabb.hishoot2i.ui.crop

import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import common.FileConstants
import common.ext.graphics.saveTo
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.illegaller.ratabb.hishoot2i.ui.common.BasePresenter
import java.io.File
import javax.inject.Inject

class CropPresenterImpl @Inject constructor(
    fileConstants: FileConstants
) : CropPresenter, BasePresenter<CropView>() {
    private val fileCrop: () -> File = (fileConstants::bgCrop)

    override fun saveCrop(bitmap: Bitmap) {
        val view = requiredView()
        launch {
            runCatching { withContext(IO) { savingProcess(fileCrop(), bitmap) } }
                .fold(view::onSuccessCrop, view::onErrorCrop)
        }
    }

    private fun savingProcess(file: File, bitmap: Bitmap): Uri {
        if (file.exists()) file.delete()
        file.createNewFile() //
        bitmap.saveTo(file)
        return file.toUri()
    }
}
