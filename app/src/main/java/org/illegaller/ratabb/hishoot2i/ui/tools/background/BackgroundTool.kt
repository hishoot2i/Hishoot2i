package org.illegaller.ratabb.hishoot2i.ui.tools.background

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import common.ext.compoundVectorDrawables
import common.ext.isVisible
import common.ext.onSeekBarChange
import common.ext.preventMultipleClick
import common.ext.setOnItemSelected
import dagger.hilt.android.AndroidEntryPoint
import org.illegaller.ratabb.hishoot2i.GlobalDirections
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.data.pref.AppPref
import org.illegaller.ratabb.hishoot2i.ui.tools.BaseTools
import org.illegaller.ratabb.hishoot2i.ui.tools.ColorMixDialog
import javax.inject.Inject

/*TODO: ?*/
@AndroidEntryPoint
class BackgroundTool : BaseTools(), ColorMixDialog.OnColorChangeListener {
    @Inject
    lateinit var appPref: AppPref

    override fun tagName(): String = "BackgroundTool"
    override fun layoutRes(): Int = R.layout.fragment_tool_background
    private lateinit var toolBackgroundModes: Spinner

    //
    private lateinit var toolBackgroundLayoutColor: View
    private lateinit var toolBackgroundLayoutImage: View
    private lateinit var toolBackgroundLayoutTrans: View

    //
    private lateinit var toolBackgroundImagePick: AppCompatTextView
    private lateinit var toolBackgroundImageSwitchBlur: SwitchCompat
    private lateinit var toolBackgroundImageSeekBlur: AppCompatSeekBar
    private lateinit var toolBackgroundImageOptionGroup: RadioGroup

    //
    private lateinit var toolBackgroundColorMix: AppCompatTextView
    private lateinit var toolBackgroundColorPipette: AppCompatTextView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(view) {
            toolBackgroundModes = findViewById(R.id.toolBackgroundModes)
            toolBackgroundImagePick = findViewById(R.id.toolBackgroundImagePick)
            toolBackgroundLayoutColor = findViewById(R.id.toolBackgroundLayoutColor)
            toolBackgroundLayoutImage = findViewById(R.id.toolBackgroundLayoutImage)
            toolBackgroundLayoutTrans = findViewById(R.id.toolBackgroundLayoutTrans)
            toolBackgroundImageSwitchBlur = findViewById(R.id.toolBackgroundImageSwitchBlur)
            toolBackgroundImageSeekBlur = findViewById(R.id.toolBackgroundImageSeekBlur)
            toolBackgroundColorMix = findViewById(R.id.toolBackgroundColorMix)
            toolBackgroundColorPipette = findViewById(R.id.toolBackgroundColorPipette)
            toolBackgroundImageOptionGroup = findViewById(R.id.toolBackgroundImageOptionGroup)
        }

        /*toolBackgroundImagePick.compoundVectorDrawables(
            top = R.drawable.ic_image_black_24dp,
            tint = R.color.accent
        )
        toolBackgroundColorMix.compoundVectorDrawables(
            top = R.drawable.ic_color_lens_black_24dp,
            tint = R.color.accent
        )
        toolBackgroundColorPipette.compoundVectorDrawables(
            top = R.drawable.ic_colorize_black_24dp,
            tint = R.color.accent
        )*/
        handleVisibleLayoutMode()

        with(toolBackgroundModes) {
            setSelection(appPref.backgroundModeId, false)
            setOnItemSelected { _: AdapterView<*>?, view: View?, position: Int, _: Long ->
                view?.preventMultipleClick {
                    if (position != appPref.backgroundModeId) {
                        appPref.backgroundModeId = position
                        handleVisibleLayoutMode()
                    }
                }
            }
        }
        // /////////////////////////////////
        toolBackgroundColorMix.setOnClickListener {
            it.preventMultipleClick {
                ColorMixDialog.newInstance(
                    color = appPref.backgroundColorInt,
                    withAlpha = true,
                    withHex = true
                )
                    .apply { listener = this@BackgroundTool }
                    .show(childFragmentManager)
            }
        }
        toolBackgroundColorPipette.setOnClickListener {
            it.preventMultipleClick {
                callback?.startingPipette(appPref.backgroundColorInt)
                dismissAllowingStateLoss()
            }
        }
        // /////////////////////////////////
        toolBackgroundImagePick.setOnClickListener {
            it.preventMultipleClick {
//                startActivityForResult(
//                    chooserGetContentWith(type = "image/*", title = getString(R.string.background)),
//                    if (isManualCrop) REQ_BG_IMAGE_CROP else REQ_BG_IMAGE_PICK
//                )
                if (isManualCrop) getContentImageCrop.launch("image/*")
                else getContentImage.launch("image/*")
            }
        }
        with(toolBackgroundImageOptionGroup) {
            check(appPref.backgroundImageOptionId)
            setOnCheckedChangeListener { rg: RadioGroup?, checkedId: Int ->
                rg?.preventMultipleClick {
                    if (checkedId != appPref.backgroundImageOptionId) {
                        appPref.backgroundImageOptionId = checkedId
                    }
                }
            }
        }
        with(toolBackgroundImageSwitchBlur) {
            isChecked = appPref.backgroundImageBlurEnable
            setOnCheckedChangeListener { cb: CompoundButton?, isChecked: Boolean ->
                cb?.preventMultipleClick { handleImageBlurSwitch(isChecked) }
            }
        }
        with(toolBackgroundImageSeekBlur) {
            isEnabled = appPref.backgroundImageBlurEnable
            progress = appPref.backgroundImageBlurRadius
            setOnSeekBarChangeListener(
                onSeekBarChange(onStopTrack = { handleImageBlurProgress(it.progress) })
            )
        }
    }

    private val getContentImageCrop = registerForActivityResult(GetContent()) { uri ->
        handleImageBackgroundCrop(uri.toString())
    }

    private val getContentImage = registerForActivityResult(GetContent()) { uri ->
        handleImageBackground(uri.toString())
    }

    override fun onColorChanged(color: Int) {
        if (appPref.backgroundColorInt != color) appPref.backgroundColorInt = color
        dismissAllowingStateLoss()
    }

    private fun handleImageBlurSwitch(isChecked: Boolean) {
        if (appPref.backgroundImageBlurEnable != isChecked) {
            appPref.backgroundImageBlurEnable = isChecked
            toolBackgroundImageSeekBlur.isEnabled = isChecked
        }
    }

    private fun handleImageBlurProgress(progress: Int) {
        if (appPref.backgroundImageBlurRadius != progress) {
            appPref.backgroundImageBlurRadius = progress
        }
    }

    private fun handleImageBackground(dataString: String) {
        callback?.changePathBackground(dataString)
    }

    private fun handleImageBackgroundCrop(dataString: String) {
        findNavController().navigate(
            GlobalDirections.crop(dataString, ratioCropX, ratioCropY)
        )
        dismissAllowingStateLoss()
    }

    private fun handleVisibleLayoutMode() {
        val mode = entity.BackgroundMode.fromId(appPref.backgroundModeId)
        toolBackgroundLayoutColor.isVisible = mode.isColor
        toolBackgroundLayoutImage.isVisible = mode.isImage
        toolBackgroundLayoutTrans.isVisible = mode.isTransparent
    }

    private val ratioCropX: Int
        get() = arguments?.getInt(ARG_RATIO_X_CROP) ?: 0
    private val ratioCropY: Int
        get() = arguments?.getInt(ARG_RATIO_Y_CROP) ?: 0
    private val isManualCrop: Boolean
        get() = appPref.backgroundImageOptionId == R.id.toolBackgroundImageOptionManualCrop

    companion object {
        private const val REQ_BG_IMAGE_PICK = 0x03
        private const val REQ_BG_IMAGE_CROP = 0x04
        private const val ARG_RATIO_X_CROP = "ratioCropX"
        private const val ARG_RATIO_Y_CROP = "ratioCropY"

        @JvmStatic
        fun newInstance(ratioCropX: Int, ratioCropY: Int): BackgroundTool = BackgroundTool()
            .apply {
                arguments = bundleOf(ARG_RATIO_X_CROP to ratioCropX, ARG_RATIO_Y_CROP to ratioCropY)
            }
    }
}
