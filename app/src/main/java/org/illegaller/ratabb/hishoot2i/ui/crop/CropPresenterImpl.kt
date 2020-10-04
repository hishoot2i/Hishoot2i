package org.illegaller.ratabb.hishoot2i.ui.crop

import android.graphics.Bitmap
import android.net.Uri
import common.FileConstants
import common.ext.DEFAULT_DELAY_MS
import common.ext.graphics.saveTo
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import org.illegaller.ratabb.hishoot2i.data.rx.SchedulerProvider
import org.illegaller.ratabb.hishoot2i.data.rx.ioUI
import org.illegaller.ratabb.hishoot2i.ui.common.BasePresenter
import java.io.File
import javax.inject.Inject

class CropPresenterImpl @Inject constructor(
    private val schedulerProvider: SchedulerProvider,
    fileConstants: FileConstants
) : CropPresenter, BasePresenter<CropView>() {
    private val disposables: CompositeDisposable = CompositeDisposable()
    private val fileCrop: () -> File = (fileConstants::bgCrop)
    override fun detachView() {
        disposables.clear()
        super.detachView()
    }

    override fun saveCrop(bitmap: Bitmap) {
        with(requiredView()) {
            savingProcess(fileCrop(), bitmap)
                .ioUI(schedulerProvider)
                .subscribeBy(::onErrorCrop, ::onSuccessCrop)
                .addTo(disposables)
        }
    }

    private fun savingProcess(file: File, bitmap: Bitmap): Single<Uri> = Single.fromCallable {
        if (file.exists()) file.delete()
        file.createNewFile() //
        bitmap.saveTo(file)
        Uri.fromFile(file)
    }
}
