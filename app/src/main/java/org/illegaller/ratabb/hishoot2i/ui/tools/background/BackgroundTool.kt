package org.illegaller.ratabb.hishoot2i.ui.tools.background

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.clearFragmentResult
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import common.ext.isVisible
import common.ext.preventMultipleClick
import common.ext.setOnItemSelected
import dagger.hilt.android.AndroidEntryPoint
import entity.BackgroundMode
import entity.ImageOption
import entity.fromIdRes
import entity.resId
import org.illegaller.ratabb.hishoot2i.data.pref.BackgroundToolPref
import org.illegaller.ratabb.hishoot2i.databinding.FragmentToolBackgroundBinding
import org.illegaller.ratabb.hishoot2i.ui.ARG_BACKGROUND_PATH
import org.illegaller.ratabb.hishoot2i.ui.ARG_COLOR
import org.illegaller.ratabb.hishoot2i.ui.ARG_PIPETTE_COLOR
import org.illegaller.ratabb.hishoot2i.ui.KEY_REQ_BACKGROUND
import org.illegaller.ratabb.hishoot2i.ui.KEY_REQ_MIX_COLOR
import org.illegaller.ratabb.hishoot2i.ui.KEY_REQ_PIPETTE
import org.illegaller.ratabb.hishoot2i.ui.common.doOnStopTouch
import org.illegaller.ratabb.hishoot2i.ui.common.registerGetContent
import javax.inject.Inject

@AndroidEntryPoint
class BackgroundTool : BottomSheetDialogFragment() {
    @Inject
    lateinit var backgroundToolPref: BackgroundToolPref
    private val args: BackgroundToolArgs by navArgs()
    private val isManualCrop: Boolean
        get() = backgroundToolPref.imageOption.isManualCrop
    private val backgroundMode: BackgroundMode
        get() = backgroundToolPref.backgroundMode

    private val imageBackgroundCrop = registerGetContent { uri ->
        findNavController().navigate(
            BackgroundToolDirections.actionToolsBackgroundToCrop(uri.toString(), args.ratio)
        )
        dismiss()
    }

    private val imageBackground = registerGetContent { uri ->
        setFragmentResult(
            KEY_REQ_BACKGROUND,
            bundleOf(ARG_BACKGROUND_PATH to uri.toString())
        )
    }
    private var backgroundBinding: FragmentToolBackgroundBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentToolBackgroundBinding.inflate(inflater, container, false).apply {
        backgroundBinding = this
        setViewListener(this)
        setFragmentResultListener(KEY_REQ_MIX_COLOR) { _, result ->
            val color = result.getInt(ARG_COLOR)
            if (backgroundToolPref.backgroundColorInt != color)
                backgroundToolPref.backgroundColorInt = color
        }
    }.run { root }

    override fun onDestroyView() {
        clearFragmentResult(KEY_REQ_MIX_COLOR)
        backgroundBinding?.toolBackgroundLayoutImage?.apply {
            backgroundImageOptionGroup.clearOnButtonCheckedListeners()
            backgroundImageBlurSlider.clearOnChangeListeners()
        }
        backgroundBinding = null
        super.onDestroyView()
    }

    private fun setViewListener(binding: FragmentToolBackgroundBinding) = with(binding) {
        handleVisibleLayoutMode(binding)
        backgroundModesSpinner.apply {
            setSelection(backgroundToolPref.backgroundMode.ordinal, false)
            setOnItemSelected { _, view, position, _ ->
                view?.preventMultipleClick {
                    if (position != backgroundToolPref.backgroundMode.ordinal) {
                        backgroundToolPref.backgroundMode = BackgroundMode.values()[position]
                        handleVisibleLayoutMode(binding)
                    }
                }
            }
        }

        // region BackgroundColor
        toolBackgroundLayoutColor.apply {
            backgroundColorMix.setOnClickListener {
                it.preventMultipleClick {
                    findNavController().navigate(
                        BackgroundToolDirections.actionToolsBackgroundToColorMix(
                            color = backgroundToolPref.backgroundColorInt,
                            withAlpha = true,
                            withHex = true
                        )
                    )
                }
            }
            backgroundColorPipette.setOnClickListener {
                it.preventMultipleClick {
                    setFragmentResult(
                        KEY_REQ_PIPETTE,
                        bundleOf(ARG_PIPETTE_COLOR to backgroundToolPref.backgroundColorInt)
                    )
                    dismiss()
                }
            }
        }
        // endregion

        // region BackgroundImage
        toolBackgroundLayoutImage.apply {
            backgroundImagePick.setOnClickListener {
                it.preventMultipleClick {
                    if (isManualCrop) imageBackgroundCrop.launch("image/*")
                    else imageBackground.launch("image/*")
                }
            }
            backgroundImageOptionGroup.apply {
                check(backgroundToolPref.imageOption.resId)
                addOnButtonCheckedListener { _, checkedId, _ ->
                    if (checkedId != backgroundToolPref.imageOption.resId) {
                        backgroundToolPref.imageOption = ImageOption.fromIdRes(checkedId)
                    }
                }
            }
            backgroundImageBlur.apply {
                isChecked = backgroundToolPref.backgroundImageBlurEnable
                setOnCheckedChangeListener { cb, isChecked ->
                    cb.preventMultipleClick {
                        if (backgroundToolPref.backgroundImageBlurEnable != isChecked) {
                            backgroundToolPref.backgroundImageBlurEnable = isChecked
                            backgroundImageBlurSlider.isEnabled = isChecked
                        }
                    }
                }
            }
            backgroundImageBlurSlider.apply {
                isEnabled = backgroundToolPref.backgroundImageBlurEnable
                value = backgroundToolPref.backgroundImageBlurRadius.toFloat()
                doOnStopTouch { slider ->
                    if (backgroundToolPref.backgroundImageBlurRadius != slider.value.toInt()) {
                        backgroundToolPref.backgroundImageBlurRadius = slider.value.toInt()
                    }
                }
            }
        }
        // endregion
    }

    private fun handleVisibleLayoutMode(binding: FragmentToolBackgroundBinding) = with(binding) {
        toolBackgroundLayoutColor.root.isVisible = backgroundMode.isColor
        toolBackgroundLayoutImage.root.isVisible = backgroundMode.isImage
        toolBackgroundLayoutTrans.isVisible = backgroundMode.isTransparent
    }
}
