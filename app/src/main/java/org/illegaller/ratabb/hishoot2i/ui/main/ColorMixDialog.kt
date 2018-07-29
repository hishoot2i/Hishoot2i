package org.illegaller.ratabb.hishoot2i.ui.main

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.design.widget.TextInputEditText
import android.support.v4.app.DialogFragment
import android.support.v4.graphics.ColorUtils
import android.support.v7.app.AppCompatDialog
import android.support.v7.widget.AppCompatSeekBar
import android.text.InputFilter.AllCaps
import android.text.InputFilter.LengthFilter
import android.view.KeyEvent
import android.view.KeyEvent.ACTION_DOWN
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.View
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.widget.SeekBar
import android.widget.TextView
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.ui.common.BaseDialogFragment
import org.illegaller.ratabb.hishoot2i.ui.common.widget.ColorPreview
import rbb.hishoot2i.common.ext.addInputFilter
import rbb.hishoot2i.common.ext.graphics.alpha
import rbb.hishoot2i.common.ext.graphics.blue
import rbb.hishoot2i.common.ext.graphics.green
import rbb.hishoot2i.common.ext.graphics.red
import rbb.hishoot2i.common.ext.graphics.toPairWithHex
import rbb.hishoot2i.common.ext.hideSoftKey
import rbb.hishoot2i.common.ext.isVisible
import rbb.hishoot2i.common.ext.onEditorAction
import rbb.hishoot2i.common.ext.onKey
import rbb.hishoot2i.common.ext.onSeekBarChange
import rbb.hishoot2i.common.ext.preventMultipleClick

class ColorMixDialog : BaseDialogFragment() {
    /**/
    interface OnColorChangeListener {
        fun onColorChanged(@ColorInt color: Int)
    }

    @ColorInt
    private var color: Int =
        DEF_COLOR
    private var withAlpha: Boolean = true
    private var withHex: Boolean = true
    var listener: OnColorChangeListener? = null
    //
    private lateinit var colorAlphaLayout: View
    private lateinit var colorHexLayout: View
    private lateinit var colorCancel: View
    private lateinit var colorDone: View
    private lateinit var colorAlphaSeekBar: AppCompatSeekBar
    private lateinit var colorRedSeekBar: AppCompatSeekBar
    private lateinit var colorGreenSeekBar: AppCompatSeekBar
    private lateinit var colorBlueSeekBar: AppCompatSeekBar
    private lateinit var colorAlphaText: TextView
    private lateinit var colorRedText: TextView
    private lateinit var colorGreenText: TextView
    private lateinit var colorBlueText: TextView
    private lateinit var colorHexEditText: TextInputEditText
    private lateinit var colorPreview: ColorPreview
    //
    override fun tagName(): String = "ColorMixDialog"

    //
    override fun layoutRes(): Int = R.layout.dialog_color_mix

    override fun createDialog(context: Context): Dialog = AppCompatDialog(context).apply {
        setStyle(DialogFragment.STYLE_NO_FRAME, theme)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(view) {
            colorAlphaLayout = findViewById(R.id.colorAlphaLayout)
            colorHexLayout = findViewById(R.id.colorHexLayout)
            colorCancel = findViewById(R.id.colorCancel)
            colorDone = findViewById(R.id.colorDone)

            colorAlphaSeekBar = findViewById(R.id.colorAlphaSeekBar)
            colorRedSeekBar = findViewById(R.id.colorRedSeekBar)
            colorGreenSeekBar = findViewById(R.id.colorGreenSeekBar)
            colorBlueSeekBar = findViewById(R.id.colorBlueSeekBar)

            colorAlphaText = findViewById(R.id.colorAlphaText)
            colorRedText = findViewById(R.id.colorRedText)
            colorGreenText = findViewById(R.id.colorGreenText)
            colorBlueText = findViewById(R.id.colorBlueText)

            colorHexEditText = findViewById(R.id.colorHex)
            colorPreview = findViewById(R.id.colorPreview)
        }

        handleDataArguments() //
        internalColorChange(color, firstTime = true)

        colorAlphaLayout.isVisible = withAlpha
        colorHexLayout.isVisible = withHex

        setViewListener()
    }

    //
    private fun handleDataArguments() {
        arguments?.let { arg: Bundle ->
            color = arg.getInt(ARG_COLOR)
            withAlpha = arg.getBoolean(ARG_WITH_ALPHA)
            withHex = arg.getBoolean(ARG_WITH_HEX)
        }
    }

    private fun setViewListener() {
        colorCancel.setOnClickListener { it.preventMultipleClick { dismiss() } }
        colorDone.setOnClickListener {
            it.preventMultipleClick {
                listener?.onColorChanged(color)
                dismiss()
            }
        }
        val seekBarChangeListener = onSeekBarChange(
            onProgress = { _: SeekBar, _: Int, _: Boolean ->
                internalColorChange(
                    Color.argb(
                        colorAlphaSeekBar.progress,
                        colorRedSeekBar.progress,
                        colorGreenSeekBar.progress,
                        colorBlueSeekBar.progress
                    )
                )
            }
        )

        colorAlphaSeekBar.setOnSeekBarChangeListener(seekBarChangeListener)
        colorRedSeekBar.setOnSeekBarChangeListener(seekBarChangeListener)
        colorGreenSeekBar.setOnSeekBarChangeListener(seekBarChangeListener)
        colorBlueSeekBar.setOnSeekBarChangeListener(seekBarChangeListener)

        with(colorHexEditText) {
            addInputFilter(AllCaps(), LengthFilter(9))
            onEditorAction { actionId: Int ->
                when (actionId) {
                    IME_ACTION_DONE -> handleColorHex(this)
                    else -> false
                }
            }
            onKey { k: Int, e: KeyEvent ->
                when {
                    e.action == ACTION_DOWN && k == KEYCODE_ENTER -> {
                        handleColorHex(this)
                    }
                    else -> false
                }
            }
        }
    }

    private fun internalColorChange(@ColorInt color: Int, firstTime: Boolean = false) {
        if (this.color != color || firstTime) {
            val (colorAlpha, colorAlphaHex) = color.alpha.toPairWithHex()
            val (colorRed, colorRedHex) = color.red.toPairWithHex()
            val (colorGreen, colorGreenHex) = color.green.toPairWithHex()
            val (colorBlue, colorBlueHex) = color.blue.toPairWithHex()

            colorAlphaSeekBar.progress = colorAlpha
            colorRedSeekBar.progress = colorRed
            colorGreenSeekBar.progress = colorGreen
            colorBlueSeekBar.progress = colorBlue

            colorAlphaText.text = colorAlphaHex
            colorRedText.text = colorRedHex
            colorGreenText.text = colorGreenHex
            colorBlueText.text = colorBlueHex
            //
            var colorHex: String = if (withAlpha) colorAlphaHex else ""
            colorHex += "$colorRedHex$colorGreenHex$colorBlueHex"

            colorHexEditText.setText(colorHex.toUpperCase())
            if (firstTime) {
                colorPreview.srcColor = color
                colorPreview.dstColor = color
            } else {
                colorPreview.dstColor = color
                this.color = color
            }
        }
    }

    private fun handleColorHex(textView: TextView): Boolean = with(textView) {
        internalColorChange(colorFromHex(text))
        clearFocus()
        hideSoftKey()
        true
    }

    @ColorInt
    private fun colorFromHex(colorText: CharSequence?): Int {
        if (colorText == null || colorText.isNullOrBlank()) {
            return color
        }
        try {
            return Color.parseColor(colorText.toString())
        } catch (ignore: IndexOutOfBoundsException) {
            return color //
        } catch (ignore: IllegalArgumentException) {
            if (colorText[0] != '#') {
                when (colorText.length) {
                    3 -> {
                        val colorTexts = buildString {
                            append("#")
                            append("${colorText[0]}${colorText[0]}") // RR
                            append("${colorText[1]}${colorText[1]}") // GG
                            append("${colorText[2]}${colorText[2]}") // BB
                        }
                        return colorFromHex(colorTexts) // RGB -> #RRGGBB
                    }
                    6 -> return colorFromHex("#$colorText") // RRGGBB ->#RRGGBB
                    8 -> {
                        if (withAlpha) {
                            return colorFromHex("#$colorText") // AARRGGBB -> #AARRGGBB
                        }
                    }
                }
            }
            return color
        }
    }

    companion object {
        private const val ARG_COLOR = "arg_color"
        private const val ARG_WITH_ALPHA = "arg_with_alpha"
        private const val ARG_WITH_HEX = "arg_with_hex"
        private const val DEF_COLOR = 0xFF00FFFF.toInt() // fallback.
        @JvmStatic
        fun newInstance(
            @ColorInt color: Int,
            withAlpha: Boolean,
            withHex: Boolean
        ): ColorMixDialog = ColorMixDialog().apply {
            arguments = Bundle().apply {
                putInt(
                    ARG_COLOR,
                    if (!withAlpha) ColorUtils.setAlphaComponent(color, 0xFF)
                    else color
                )
                putBoolean(ARG_WITH_ALPHA, withAlpha)
                putBoolean(ARG_WITH_HEX, withHex)
            }
        }
    }
}