package org.illegaller.ratabb.hishoot2i.ui.crop

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import common.ext.deviceSizes
import common.ext.preventMultipleClick
import dagger.hilt.android.AndroidEntryPoint
import imageloader.ImageLoader
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.databinding.FragmentCropBinding
import org.illegaller.ratabb.hishoot2i.ui.ARG_CROP_PATH
import org.illegaller.ratabb.hishoot2i.ui.KEY_REQ_CROP
import org.illegaller.ratabb.hishoot2i.ui.common.widget.CropImageView
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class CropFragment : Fragment(R.layout.fragment_crop), CropView {
    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var presenter: CropPresenter
    private val args: CropFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentCropBinding.bind(view).apply {
            handleDataExtras(cropImageView)
            cropCancel.setOnClickListener { it.preventMultipleClick { setResultCanceled() } }
            cropDone.setOnClickListener {
                it.preventMultipleClick { presenter.saveCrop(cropImageView.croppedBitmap) }
            }
        }
        presenter.attachView(this)
    }

    override fun onDestroyView() {
        presenter.detachView()
        super.onDestroyView()
    }

    override fun onError(e: Throwable) {
        Toast.makeText(requireContext(), e.localizedMessage ?: "Oops", Toast.LENGTH_SHORT).show()
        Timber.e(e)
    }

    override fun onErrorCrop(throwable: Throwable) {
        onError(throwable)
        setResultCanceled()
    }

    override fun onSuccessCrop(uri: Uri) {
        setFragmentResult(KEY_REQ_CROP, bundleOf(ARG_CROP_PATH to uri.toString()))
        findNavController().navigateUp()
    }

    private fun handleDataExtras(cropImageView: CropImageView) {
        with(args) {
            cropImageView.setCustomRatio(ratio.x, ratio.y)
            imageLoader.display(cropImageView, path, cropImageView.context.deviceSizes)
        }
    }

    private fun setResultCanceled() {
        findNavController().navigateUp()
    }
}
