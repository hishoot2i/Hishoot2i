package org.illegaller.ratabb.hishoot2i.ui.main.tools.badge

import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.widget.AppCompatSeekBar
import android.support.v7.widget.AppCompatSpinner
import android.support.v7.widget.SwitchCompat
import android.text.InputFilter.AllCaps
import android.text.InputFilter.LengthFilter
import android.view.KeyEvent
import android.view.KeyEvent.ACTION_DOWN
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.TextView
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.data.pref.AppPref
import org.illegaller.ratabb.hishoot2i.ui.main.ColorMixDialog
import org.illegaller.ratabb.hishoot2i.ui.main.tools.AbsTools
import rbb.hishoot2i.common.ext.addInputFilter
import rbb.hishoot2i.common.ext.forEach
import rbb.hishoot2i.common.ext.hideSoftKey
import rbb.hishoot2i.common.ext.onEditorAction
import rbb.hishoot2i.common.ext.onKey
import rbb.hishoot2i.common.ext.onSeekBarChange
import rbb.hishoot2i.common.ext.preventMultipleClick
import rbb.hishoot2i.common.ext.setOnItemSelected
import timber.log.Timber
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

class BadgeTool : AbsTools(),
    BadgeView,
    ColorMixDialog.OnColorChangeListener {
    @Inject
    lateinit var presenter: BadgeToolPresenter
    private lateinit var toolBadgeLayout: ViewGroup
    private lateinit var toolBadgeHide: SwitchCompat
    private lateinit var toolBadgeColorPick: View
    private lateinit var toolBadgeInput: TextInputEditText
    private lateinit var toolBadgeSize: AppCompatSeekBar
    private lateinit var toolBadgePosition: AppCompatSpinner
    private lateinit var toolBadgeFont: AppCompatSpinner
    private val fontAdapter: ArrayAdapter<String> by lazy(NONE) {
        ArrayAdapter<String>(context, android.R.layout.simple_list_item_1)
            .apply { setNotifyOnChange(true) }
    }

    override fun tagName(): String = "BadgeTool"
    override fun layoutRes(): Int = R.layout.fragment_tool_badge
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(view) {
            toolBadgeLayout = findViewById(R.id.toolBadgeLayout)
            toolBadgeHide = findViewById(R.id.toolBadgeHide)
            toolBadgeColorPick = findViewById(R.id.toolBadgeColorPick)
            toolBadgeInput = findViewById(R.id.toolBadgeInput)
            toolBadgeSize = findViewById(R.id.toolBadgeSize)
            toolBadgeFont = findViewById(R.id.toolBadgeFont)
            toolBadgePosition = findViewById(R.id.toolBadgePosition)
        }
        presenter.attachView(this)
    }

    override fun onDestroyView() {
        presenter.detachView()
        super.onDestroyView()
    }

    override fun onColorChanged(color: Int) {
        presenter.setBadgeColor(color)
    }

    override fun onEmit(appPref: AppPref) {
        handleBadgeHide(appPref.badgeEnable)
        with(toolBadgeHide) {
            isChecked = appPref.badgeEnable
            setOnCheckedChangeListener { cb: CompoundButton, isChecked: Boolean ->
                cb.preventMultipleClick {
                    if (appPref.badgeEnable != isChecked) {
                        appPref.badgeEnable = isChecked
                        handleBadgeHide(isChecked)
                    }
                }
            }
        }
        toolBadgeColorPick.setOnClickListener {
            it.preventMultipleClick {
                ColorMixDialog.newInstance(
                    color = appPref.badgeColor,
                    withAlpha = false,
                    withHex = true
                )
                    .apply { listener = this@BadgeTool }
                    .show(childFragmentManager)
            }
        }
        with(toolBadgeInput) {
            setText(appPref.badgeText)
            addInputFilter(AllCaps(), LengthFilter(16)) //
            onEditorAction { actionID: Int ->
                when (actionID) {
                    IME_ACTION_DONE -> badgeInput(toolBadgeInput, appPref)
                    else -> false
                }
            }
            onKey { k: Int, e: KeyEvent ->
                when {
                    e.action == ACTION_DOWN && k == KEYCODE_ENTER -> {
                        badgeInput(toolBadgeInput, appPref)
                    }
                    else -> false
                }
            }
        }

        with(toolBadgeSize) {
            progress = appPref.badgeSize
            setOnSeekBarChangeListener(onSeekBarChange(onStopTrack = {
                if (appPref.badgeSize != it.progress) {
                    appPref.badgeSize = it.progress
                }
            }))
        }
        with(toolBadgePosition) {
            setSelection(appPref.badgePositionId, false)
            setOnItemSelected { _: AdapterView<*>?, v: View?, position: Int, _: Long ->
                v?.preventMultipleClick {
                    if (appPref.badgePositionId != position) {
                        appPref.badgePositionId = position
                    }
                }
            }
        }
        toolBadgeFont.adapter = fontAdapter
    }

    // FIXME: ?
    override fun submitListAdapter(list: List<String>, current: Int) {
        fontAdapter.clear()
        fontAdapter.addAll(list)
        toolBadgeFont.setSelection(current, false)
        toolBadgeFont.setOnItemSelected { _: AdapterView<*>?, v: View?, position: Int, _: Long ->
            v?.preventMultipleClick { presenter.setBadgeFont(position) }
        }
    }

    override fun onError(e: Throwable) {
        Timber.e(e)
        // context?.toast(e.localizedMessage)
    }

    private fun handleBadgeHide(isChecked: Boolean) { //
        toolBadgeLayout.forEach { view: View ->
            view.isEnabled = isChecked
            if (view is ViewGroup) {
                view.forEach {
                    it.isEnabled = isChecked
                }
            }
        }
    }

    private fun badgeInput(textView: TextView, appPref: AppPref): Boolean {
        with(textView) {
            val normalizeText = tryNormalizeText(text)
            text = normalizeText
            clearFocus()
            hideSoftKey()
            if (appPref.badgeText != normalizeText) {
                appPref.badgeText = normalizeText
            }
        }
        return true
    }

    private fun tryNormalizeText(text: CharSequence?): String = try {
        when {
            text.isNullOrBlank() -> DEFAULT_TEXT
            else -> text.toString().toUpperCase()
        }
    } catch (ignored: Exception) {
        DEFAULT_TEXT
    }

    companion object {
        private const val DEFAULT_TEXT = "HISHOOT"
    }
}