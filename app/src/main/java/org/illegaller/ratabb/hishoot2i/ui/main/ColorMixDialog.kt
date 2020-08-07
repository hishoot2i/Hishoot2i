package org.illegaller.ratabb.hishoot2i.ui.main

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter.AllCaps
import android.text.InputFilter.LengthFilter
import android.view.KeyEvent.ACTION_DOWN
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.View
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import common.ext.addInputFilter
import common.ext.graphics.alpha
import common.ext.graphics.blue
import common.ext.graphics.green
import common.ext.graphics.red
import common.ext.graphics.toPairWithHex
import common.ext.hideSoftKey
import common.ext.isVisible
import common.ext.onEditorAction
import common.ext.onKey
import common.ext.onSeekBarChange
import common.ext.preventMultipleClick
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.ui.common.BaseDialogFragment
import org.illegaller.ratabb.hishoot2i.ui.common.widget.ColorPreview
import java.util.Locale

class ColorMixDialog : BaseDialogFragment() {
    /**/
    interface OnColorChangeListener {
        fun onColorChanged(@ColorInt color: Int)
    }

    @ColorInt
    private var color: Int = DEF_COLOR
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
            onEditorAction { actionId -> handleColorHex { actionId == IME_ACTION_DONE } }
            onKey { keyCode, keyEvent ->
                handleColorHex { keyEvent.action == ACTION_DOWN && keyCode == KEYCODE_ENTER }
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

            colorHexEditText.setText(colorHex.toUpperCase(Locale.ROOT))
            if (firstTime) {
                colorPreview.srcColor = color
                colorPreview.dstColor = color
            } else {
                colorPreview.dstColor = color
                this.color = color
            }
        }
    }

    private inline fun TextView.handleColorHex(crossinline condition: () -> Boolean): Boolean =
        condition().also {
            if (it) {
                internalColorChange(text.colorFromHex())
                clearFocus()
                hideSoftKey()
            }
        }

    @ColorInt private fun CharSequence?.colorFromHex(): Int = this?.let {
        try {
            return@let Color.parseColor(toString())
        } catch (e: IndexOutOfBoundsException) {
            return@let color
        } catch (e: IllegalArgumentException) {
            if (it[0] != '#') {
                when (it.length) {
                    3 -> return@let buildString {
                        append("#")
                        append("${it[0]}${it[0]}")
                        append("${it[1]}${it[1]}")
                        append("${it[2]}${it[2]}")
                    }.colorFromHex()
                    6 -> return@let "#$it".colorFromHex()
                    8 -> if (withAlpha) return@let "#$it".colorFromHex()
                }
            }
            return@let color
        }
    } ?: color

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