@file:Suppress("SpellCheckingInspection")

package org.illegaller.ratabb.hishoot2i.ui.tools.badge

import android.os.Bundle
import android.text.InputFilter.AllCaps
import android.text.InputFilter.LengthFilter
import android.view.KeyEvent.ACTION_DOWN
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.view.forEach
import androidx.fragment.app.clearFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import common.ext.addInputFilter
import common.ext.hideSoftKey
import common.ext.onEditorAction
import common.ext.onKey
import common.ext.preventMultipleClick
import common.ext.setOnItemSelected
import dagger.hilt.android.AndroidEntryPoint
import entity.BadgePosition
import org.illegaller.ratabb.hishoot2i.data.pref.BadgeToolPref
import org.illegaller.ratabb.hishoot2i.databinding.FragmentToolBadgeBinding
import org.illegaller.ratabb.hishoot2i.ui.ARG_COLOR
import org.illegaller.ratabb.hishoot2i.ui.KEY_REQ_MIX_COLOR
import org.illegaller.ratabb.hishoot2i.ui.common.doOnStopTouch
import org.illegaller.ratabb.hishoot2i.ui.common.viewObserve
import org.illegaller.ratabb.hishoot2i.ui.tools.badge.BadgeToolDirections.Companion.actionToolsBadgeToColorMix
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class BadgeTool : BottomSheetDialogFragment() {

    @Inject
    lateinit var badgeToolPref: BadgeToolPref

    @Inject
    lateinit var fontAdapter: FontAdapter

    private val viewModel: BadgeToolViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentToolBadgeBinding.inflate(inflater, container, false).apply {
        viewObserve(viewModel.uiState) {
            when (it) {
                is Fail -> {
                    val message = it.cause.localizedMessage ?: "Oops"
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    Timber.e(it.cause)
                }
                is Success -> submitListAdapter(it.data)
            }
        }
        viewListener()
        setFragmentResultListener(KEY_REQ_MIX_COLOR) { _, result ->
            badgeToolPref.badgeColor = result.getInt(ARG_COLOR)
        }
    }.run { root }

    override fun onDestroyView() {
        clearFragmentResult(KEY_REQ_MIX_COLOR)
        super.onDestroyView()
    }

    private fun FragmentToolBadgeBinding.viewListener() {
        handleBadgeHide(badgeToolPref.badgeEnable)
        toolBadgeHide.isChecked = badgeToolPref.badgeEnable
        toolBadgeHide.setOnCheckedChangeListener { cb: CompoundButton, isChecked: Boolean ->
            cb.preventMultipleClick {
                if (badgeToolPref.badgeEnable != isChecked) {
                    badgeToolPref.badgeEnable = isChecked
                    handleBadgeHide(isChecked)
                }
            }
        }

        toolBadgeColorPick.setOnClickListener {
            it.preventMultipleClick {
                findNavController().navigate(
                    actionToolsBadgeToColorMix(
                        color = badgeToolPref.badgeColor,
                        withAlpha = false,
                        withHex = true
                    )
                )
            }
        }

        toolBadgeInput.setText(badgeToolPref.badgeText)
        toolBadgeInput.addInputFilter(AllCaps(), LengthFilter(16)) //
        toolBadgeInput.onEditorAction { handleBadgeInput { it == IME_ACTION_DONE } }
        toolBadgeInput.onKey { keyCode, keyEvent ->
            handleBadgeInput { keyEvent.action == ACTION_DOWN && keyCode == KEYCODE_ENTER }
        }

        toolBadgeSize.value = badgeToolPref.badgeSize
        toolBadgeSize.doOnStopTouch { slider ->
            if (badgeToolPref.badgeSize != slider.value) {
                badgeToolPref.badgeSize = slider.value
            }
        }

        toolBadgePosition.setSelection(badgeToolPref.badgePosition.ordinal, false)
        toolBadgePosition.setOnItemSelected { _, v, position, _ ->
            v?.preventMultipleClick {
                if (badgeToolPref.badgePosition.ordinal != position) {
                    badgeToolPref.badgePosition = BadgePosition.values()[position]
                }
            }
        }

        toolBadgeFont.adapter = fontAdapter
        toolBadgeFont.setOnItemSelected { _, v, position, _ ->
            v?.preventMultipleClick {
                fontAdapter.setSelection(position)
                badgeToolPref.badgeTypefacePath = fontAdapter.getItemAsString(position)
            }
        }
    }

    private fun FragmentToolBadgeBinding.submitListAdapter(list: List<String>) {
        val current = list.indexOf(badgeToolPref.badgeTypefacePath).coerceAtLeast(minimumValue = 0)
        fontAdapter.submitList(list, current)
        toolBadgeFont.apply {
            // NOTE: size == 1 -> only DEFAULT
            isEnabled = list.size > 1 && badgeToolPref.badgeEnable
            setSelection(current, false)
        }
    }

    private fun FragmentToolBadgeBinding.handleBadgeHide(enable: Boolean) { //
        root.forEach {
            when (it) {
                toolBadgeHide -> { // no-op
                }
                toolBadgeFont -> it.isEnabled = fontAdapter.count > 1 && enable
                else -> it.isEnabled = enable
            }
        }
    }

    private inline fun FragmentToolBadgeBinding.handleBadgeInput(
        crossinline condition: () -> Boolean
    ): Boolean = condition().also {
        if (it) {
            toolBadgeInput.clearFocus()
            toolBadgeInput.hideSoftKey()
            val normalizeText = toolBadgeInput.text?.takeUnless(CharSequence?::isNullOrBlank)
                ?.toString() ?: "HISHOOT"
            if (badgeToolPref.badgeText != normalizeText) badgeToolPref.badgeText = normalizeText
            toolBadgeInput.setText(normalizeText)
        }
    }
}
