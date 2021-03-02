package org.illegaller.ratabb.hishoot2i.ui.crop

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
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
class CropFragment : Fragment(R.layout.fragment_crop) {
    @Inject
    lateinit var imageLoader: ImageLoader

    private val viewModel: CropViewModel by viewModels()

    private val args: CropFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentCropBinding.bind(view).apply {
            handleDataExtras(cropImageView)
            viewModel.uiState.observe(viewLifecycleOwner) { observer(it) }
            cropCancel.setOnClickListener {
                it.preventMultipleClick { findNavController().navigateUp() }
            }
            cropDone.setOnClickListener {
                it.preventMultipleClick {
                    viewModel.savingCrop(cropImageView.croppedBitmap)
                }
            }
        }
    }

    private fun observer(view: CropView) {
        when (view) {
            is Success -> {
                setFragmentResult(KEY_REQ_CROP, bundleOf(ARG_CROP_PATH to view.uriCrop))
                findNavController().navigateUp()
            }
            is Fail -> {
                val msg = view.cause.localizedMessage ?: "Oops"
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                Timber.e(view.cause)
                findNavController().navigateUp()
            }
        }
    }

    private fun handleDataExtras(cropImageView: CropImageView) {
        with(args) {
            cropImageView.setCustomRatio(ratio.x, ratio.y)
            imageLoader.display(cropImageView, path, cropImageView.context.deviceSizes)
        }
    }
}
