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
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.Slider
import common.graphics.lightOrDarkContrast
import common.view.hideSoftKey
import common.view.onKey
import common.view.preventMultipleClick
import common.widget.addInputFilter
import common.widget.onEditorAction
import org.illegaller.ratabb.hishoot2i.databinding.DialogColorMixBinding
import org.illegaller.ratabb.hishoot2i.ui.ARG_COLOR
import org.illegaller.ratabb.hishoot2i.ui.KEY_REQ_MIX_COLOR
import java.util.Locale
import kotlin.properties.Delegates

class ColorMixDialog : AppCompatDialogFragment() {
    private val args: ColorMixDialogArgs by navArgs()

    @get:ColorInt
    private var color: Int by Delegates.notNull()

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
    ): View = DialogColorMixBinding.inflate(inflater, container, false).apply {
        color = args.color
        internalColorChange(color, emit = true)
        colorAlphaGroup.isVisible = args.withAlpha
        colorHexLayout.isVisible = args.withHex
        setViewListener()
    }.run { root }

    private fun DialogColorMixBinding.setViewListener() {
        colorCancel.setOnClickListener { it.preventMultipleClick { dismiss() } }
        colorDone.setOnClickListener {
            it.preventMultipleClick {
                setFragmentResult(KEY_REQ_MIX_COLOR, bundleOf(ARG_COLOR to color))
                dismiss()
            }
        }
        val hexFormatter = LabelFormatter { it.toInt().toHexString().uppercase(Locale.ROOT) }
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
                )
            )
        }
        colorAlphaSeekBar.addOnChangeListener(sliderOnChangeListener)
        colorRedSeekBar.addOnChangeListener(sliderOnChangeListener)
        colorGreenSeekBar.addOnChangeListener(sliderOnChangeListener)
        colorBlueSeekBar.addOnChangeListener(sliderOnChangeListener)

        colorHex.addInputFilter(AllCaps(), LengthFilter(9))
        colorHex.onEditorAction { handleColorFromText { it == IME_ACTION_DONE } }
        colorHex.onKey { keyCode, keyEvent ->
            handleColorFromText { keyEvent.action == ACTION_DOWN && keyCode == KEYCODE_ENTER }
        }
    }

    private fun DialogColorMixBinding.internalColorChange(
        @ColorInt color: Int,
        emit: Boolean = false
    ) {
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
            val textColorHex: String = buildString {
                if (args.withAlpha) append(colorAlphaHex)
                append(colorRedHex).append(colorGreenHex).append(colorBlueHex)
            }
            colorHex.setText(textColorHex.uppercase(Locale.ROOT))
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
            colorHex.setText(color.toHexString().uppercase(Locale.ROOT)) //
        }
    }

    private inline fun DialogColorMixBinding.handleColorFromText(
        crossinline condition: () -> Boolean
    ): Boolean = condition().also {
        val hex = colorHex.text?.toString()
        if (it && hex != null) {
            internalColorChange(hex.colorFromHex(args.withAlpha, color))
            colorHex.clearFocus()
            colorHex.hideSoftKey()
        }
    }

    private fun @receiver:ColorInt Int.toHexString(): String =
        Integer.toHexString(this).apply {
            return when (length) {
                1 -> "0$this" //
                else -> this
            }
        }

    private fun @receiver:ColorInt Int.toPairWithHex(): Pair<Int, String> =
        this to toHexString()

    @ColorInt
    private fun String.colorFromHex(isWithAlpha: Boolean, @ColorInt fallback: Int): Int {
        val value = this
        return try {
            Color.parseColor(value)
        } catch (e: IndexOutOfBoundsException) {
            fallback
        } catch (e: IllegalArgumentException) {
            if (value[0] != '#') when (value.length) {
                // RGB -> #AARRGGBB {A=FF: full alpha }
                3 -> buildString(9) {
                    append("#FF")
                    append(value[0]).repeat(2)
                    append(value[1]).repeat(2)
                    append(value[2]).repeat(2)
                }.colorFromHex(isWithAlpha, fallback)
                // RRGGBB -> #AARRGGBB {A=FF: full alpha }
                6 -> buildString(9) { append("#FF").append(value) }
                    .colorFromHex(isWithAlpha, fallback)
                // AARRGGBB -> #AARRGGBB {if not isWithAlpha A=FF: full alpha }
                8 -> buildString(9) {
                    append("#")
                    if (isWithAlpha) append(value)
                    else append("FF").append(value.substring(2))
                }.colorFromHex(isWithAlpha, fallback)
                else -> fallback
            } else fallback
        }
    }
}
