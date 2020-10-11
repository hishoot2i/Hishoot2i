package org.illegaller.ratabb.hishoot2i.ui.tools

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter.AllCaps
import android.text.InputFilter.LengthFilter
import android.view.KeyEvent.ACTION_DOWN
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.Slider
import common.ext.addInputFilter
import common.ext.graphics.alpha
import common.ext.graphics.blue
import common.ext.graphics.colorFromHex
import common.ext.graphics.green
import common.ext.graphics.lightOrDarkContrast
import common.ext.graphics.red
import common.ext.graphics.toHexString
import common.ext.graphics.toPairWithHex
import common.ext.hideSoftKey
import common.ext.isVisible
import common.ext.onEditorAction
import common.ext.onKey
import common.ext.preventMultipleClick
import org.illegaller.ratabb.hishoot2i.databinding.DialogColorMixBinding
import org.illegaller.ratabb.hishoot2i.ui.ARG_COLOR
import org.illegaller.ratabb.hishoot2i.ui.KEY_REQ_MIX_COLOR
import java.util.Locale
import kotlin.properties.Delegates

class ColorMixDialog : AppCompatDialogFragment() {
    private val args: ColorMixDialogArgs by navArgs()

    @get:ColorInt
    private var color: Int by Delegates.notNull()

    private var colorMixBinding: DialogColorMixBinding? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        super.onCreateDialog(savedInstanceState).apply {
            setStyle(DialogFragment.STYLE_NO_FRAME, theme)
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        DialogColorMixBinding.inflate(inflater, container, false).apply {
            color = args.color
            internalColorChange(color, this, emit = true)
            colorAlphaLayout.isVisible = args.withAlpha
            colorHexLayout.isVisible = args.withHex
            setViewListener(this)
            colorMixBinding = this
        }.run { return@onCreateView root }
    }

    override fun onDestroyView() {
        colorMixBinding?.apply {
            colorAlphaSeekBar.clearOnChangeListeners()
            colorRedSeekBar.clearOnChangeListeners()
            colorGreenSeekBar.clearOnChangeListeners()
            colorBlueSeekBar.clearOnChangeListeners()
        }
        colorMixBinding = null
        super.onDestroyView()
    }

    private fun setViewListener(binding: DialogColorMixBinding) = with(binding) {
        colorCancel.setOnClickListener { it.preventMultipleClick { dismiss() } }
        colorDone.setOnClickListener {
            it.preventMultipleClick {
                setFragmentResult(
                    KEY_REQ_MIX_COLOR,
                    bundleOf(ARG_COLOR to color)
                )
                dismiss()
            }
        }
        val hexFormatter = LabelFormatter { it.toInt().toHexString().toUpperCase(Locale.ROOT) }
        colorAlphaSeekBar.setLabelFormatter(hexFormatter)
        colorRedSeekBar.setLabelFormatter(hexFormatter)
        colorGreenSeekBar.setLabelFormatter(hexFormatter)
        colorBlueSeekBar.setLabelFormatter(hexFormatter)

        val sliderOnChangeListener: (Slider, Float, Boolean) -> Unit = { _, _, _ ->
            internalColorChange(
                Color.argb(
                    colorAlphaSeekBar.value.toInt(),
                    colorRedSeekBar.value.toInt(),
                    colorGreenSeekBar.value.toInt(),
                    colorBlueSeekBar.value.toInt()
                ),
                this
            )
        }
        colorAlphaSeekBar.addOnChangeListener(sliderOnChangeListener)
        colorRedSeekBar.addOnChangeListener(sliderOnChangeListener)
        colorGreenSeekBar.addOnChangeListener(sliderOnChangeListener)
        colorBlueSeekBar.addOnChangeListener(sliderOnChangeListener)

        colorHex.apply {
            addInputFilter(AllCaps(), LengthFilter(9))
            onEditorAction { actionId ->
                handleColorFromText(this@with) { actionId == IME_ACTION_DONE }
            }
            onKey { keyCode, keyEvent ->
                handleColorFromText(this@with) {
                    keyEvent.action == ACTION_DOWN && keyCode == KEYCODE_ENTER
                }
            }
        }
    }

    private fun internalColorChange(
        @ColorInt color: Int,
        binding: DialogColorMixBinding,
        emit: Boolean = false
    ) = with(binding) {
        if (this@ColorMixDialog.color != color || emit) {
            val (colorAlpha, colorAlphaHex) = color.alpha.toPairWithHex()
            val (colorRed, colorRedHex) = color.red.toPairWithHex()
            val (colorGreen, colorGreenHex) = color.green.toPairWithHex()
            val (colorBlue, colorBlueHex) = color.blue.toPairWithHex()
            //
            colorAlphaSeekBar.value = colorAlpha.toFloat()
            colorRedSeekBar.value = colorRed.toFloat()
            colorGreenSeekBar.value = colorGreen.toFloat()
            colorBlueSeekBar.value = colorBlue.toFloat()
            //
            colorAlphaText.text = colorAlphaHex
            colorRedText.text = colorRedHex
            colorGreenText.text = colorGreenHex
            colorBlueText.text = colorBlueHex
            //
            var textColorHex: String = if (args.withAlpha) colorAlphaHex else ""
            textColorHex += "$colorRedHex$colorGreenHex$colorBlueHex"

            colorHex.setText(textColorHex.toUpperCase(Locale.ROOT))
            val buttonTextColor = color.lightOrDarkContrast
            if (emit) {
                colorPreview.initColor(color)
                colorCancel.setTextColor(buttonTextColor)
                colorDone.setTextColor(buttonTextColor)
            } else {
                colorDone.setTextColor(buttonTextColor)
                colorPreview.changeColor(color)
                this@ColorMixDialog.color = color
            }
        } else {
            colorHex.setText(color.toHexString().toUpperCase(Locale.ROOT)) //
        }
    }

    private inline fun TextView.handleColorFromText(
        binding: DialogColorMixBinding,
        crossinline condition: () -> Boolean
    ): Boolean = condition().also {
        val hex = text?.toString()
        if (it && hex != null) {
            internalColorChange(hex.colorFromHex(args.withAlpha, color), binding)
            clearFocus()
            hideSoftKey()
        }
    }
}
