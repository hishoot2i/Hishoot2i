package org.illegaller.ratabb.hishoot2i.ui.crop

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.view.View
import common.FileConstants
import common.ext.isVisible
import common.ext.preventMultipleClick
import dagger.hilt.android.AndroidEntryPoint
import imageloader.ImageLoader
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.ui.common.BaseActivity
import org.illegaller.ratabb.hishoot2i.ui.common.widget.CropImageView
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class CropActivity : BaseActivity(), CropActivityView {
    @Inject
    lateinit var imageLoader: ImageLoader
    @Inject
    lateinit var presenter: CropActivityPresenter
    @Inject
    lateinit var fileConstants: FileConstants
    //
    private lateinit var cropImageView: CropImageView
    private lateinit var cropDone: View
    private lateinit var cropCancel: View
    private lateinit var cropProgress: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)
        cropImageView = findViewById(R.id.cropImageView)
        cropDone = findViewById(R.id.cropDone)
        cropCancel = findViewById(R.id.cropCancel)
        cropProgress = findViewById(R.id.loading)

        presenter.attachView(this)
        handleDataExtras()
        setViewListener()
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    override fun onError(e: Throwable) {
        Timber.e(e)
    }

    override fun onErrorCrop(throwable: Throwable) {
        onError(throwable)
        hideProgress()
        setResultCanceled()
    }

    override fun onSuccessCrop(uri: Uri) {
        hideProgress()
        setResult(RESULT_OK, Intent().setData(uri))
        finish()
    }

    private fun handleDataExtras() {
        intent.getParcelableExtra<Point>(KEY_CROP_RATIO)?.let {
            cropImageView.setCustomRatio(it.x, it.y)
        }
        intent.getStringExtra(KEY_IMAGE_PATH)?.let {
            imageLoader.display(cropImageView, it)
        }
    }

    private fun setViewListener() {
        cropCancel.setOnClickListener { v: View? ->
            v?.preventMultipleClick { setResultCanceled() }
        }
        cropDone.setOnClickListener { v: View? ->
            v?.preventMultipleClick {
                showProgress()
                presenter.saveCrop(fileConstants.bgCrop(), cropImageView.croppedBitmap)
            }
        }
    }

    private fun setResultCanceled() {
        setResult(RESULT_CANCELED)
        finish()
    }

    private fun hideProgress() {
        cropProgress.isVisible = false
    }

    private fun showProgress() {
        cropProgress.isVisible = true
    }

    companion object {
        private const val KEY_IMAGE_PATH = "_image_path"
        private const val KEY_CROP_RATIO = "_crop_ratio"
        @JvmStatic
        fun intentCrop(
            context: Context,
            data: String,
            ratio: Point?
        ): Intent = Intent(context, CropActivity::class.java).apply {
            putExtra(KEY_IMAGE_PATH, data)
            ratio?.let { putExtra(KEY_CROP_RATIO, it) }
            putExtra(Intent.EXTRA_RETURN_RESULT, true)
        }
    }
}