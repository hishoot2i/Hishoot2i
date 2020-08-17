package org.illegaller.ratabb.hishoot2i.ui.crop

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import common.FileConstants
import common.ext.preventMultipleClick
import dagger.hilt.android.AndroidEntryPoint
import imageloader.ImageLoader
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.ui.common.widget.CropImageView
import org.illegaller.ratabb.hishoot2i.ui.main.MainFragment.Companion.KEY_CROP_PATH
import org.illegaller.ratabb.hishoot2i.ui.main.MainFragment.Companion.KEY_REQ_CROP
import timber.log.Timber
import javax.inject.Inject

// FIXME: Toolbar wrong
@AndroidEntryPoint
class CropFragment : Fragment(R.layout.fragment_crop), CropView {
    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var presenter: CropPresenter

    @Inject
    lateinit var fileConstants: FileConstants

    //
    private lateinit var cropImageView: CropImageView
    private lateinit var cropDone: View
    private lateinit var cropCancel: View
//    private lateinit var cropProgress: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(view) {
            cropImageView = findViewById(R.id.cropImageView)
            cropDone = findViewById(R.id.cropDone)
            cropCancel = findViewById(R.id.cropCancel)
//            cropProgress = findViewById(R.id.loading)
        }
        presenter.attachView(this)
        handleDataExtras()
        setViewListener()
    }

    override fun onDestroyView() {
        presenter.detachView()
        super.onDestroyView()
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
        setFragmentResult(KEY_REQ_CROP, bundleOf(KEY_CROP_PATH to uri.toString()))
        findNavController().navigateUp()
    }

    private val args: CropFragmentArgs by navArgs()

    private fun handleDataExtras() {
        Timber.d("args= $args")
        with(args) {
            cropImageView.setCustomRatio(ratioX, ratioY)
            path?.let { imageLoader.display(cropImageView, it) }
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
        findNavController().navigateUp()
    }

    private fun hideProgress() {
        // cropProgress.isVisible = false
    }

    private fun showProgress() {
//        cropProgress.isVisible = true
    }
}
