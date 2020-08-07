package org.illegaller.ratabb.hishoot2i.ui.main.tools.badge

import android.os.Bundle
import android.text.InputFilter.AllCaps
import android.text.InputFilter.LengthFilter
import android.view.KeyEvent.ACTION_DOWN
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.textfield.TextInputEditText
import common.ext.addInputFilter
import common.ext.forEach
import common.ext.hideSoftKey
import common.ext.onEditorAction
import common.ext.onKey
import common.ext.onSeekBarChange
import common.ext.preventMultipleClick
import common.ext.setOnItemSelected
import dagger.hilt.android.AndroidEntryPoint
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.data.pref.AppPref
import org.illegaller.ratabb.hishoot2i.ui.main.ColorMixDialog
import org.illegaller.ratabb.hishoot2i.ui.main.tools.AbsTools
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

@AndroidEntryPoint
class BadgeTool : AbsTools(), BadgeView, ColorMixDialog.OnColorChangeListener {
    @Inject
    lateinit var presenter: BadgeToolPresenter
    private val fontAdapter: FontAdapter by lazy(NONE) { FontAdapter(requireContext()) }
    private lateinit var toolBadgeLayout: ViewGroup
    private lateinit var toolBadgeHide: SwitchCompat
    private lateinit var toolBadgeColorPick: View
    private lateinit var toolBadgeInput: TextInputEditText
    private lateinit var toolBadgeSize: AppCompatSeekBar
    private lateinit var toolBadgePosition: AppCompatSpinner
    private lateinit var toolBadgeFont: AppCompatSpinner
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
            onEditorAction { actionId ->
                handleBadgeInput(appPref) { actionId == IME_ACTION_DONE }
            }
            onKey { keyCode, keyEvent ->
                handleBadgeInput(appPref) {
                    keyEvent.action == ACTION_DOWN && keyCode == KEYCODE_ENTER
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
        toolBadgeFont.setOnItemSelected { _: AdapterView<*>?, v: View?, position: Int, _: Long ->
            v?.preventMultipleClick {
                fontAdapter.setSelection(position)
                presenter.setBadgeFont(position)
            }
        }
    }

    override fun submitListAdapter(list: List<String>, current: Int) {
        fontAdapter.submitList(list, current)
        toolBadgeFont.setSelection(current, false)
    }

    override fun onError(e: Throwable) {
        Timber.e(e)
    }

    private fun handleBadgeHide(isChecked: Boolean) { //
        toolBadgeLayout.forEach { view: View ->
            view.isEnabled = isChecked
            (view as? ViewGroup)?.forEach { it.isEnabled = isChecked }
        }
    }

    private inline fun TextView.handleBadgeInput(
        appPref: AppPref,
        crossinline condition: () -> Boolean
    ): Boolean = condition().also {
        if (it) {
            val upperCaseText = text?.toString()?.toUpperCase(Locale.ROOT) ?: AppPref.DEF_BADGE_TEXT
            text = upperCaseText
            clearFocus()
            hideSoftKey()
            if (appPref.badgeText != upperCaseText) appPref.badgeText = upperCaseText
        }
    }
}