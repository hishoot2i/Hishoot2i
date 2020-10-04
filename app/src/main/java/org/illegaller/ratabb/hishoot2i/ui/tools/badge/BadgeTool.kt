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
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.forEach
import androidx.fragment.app.clearFragmentResult
import androidx.fragment.app.setFragmentResultListener
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
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class BadgeTool : BottomSheetDialogFragment(), BadgeView {
    @Inject
    lateinit var presenter: BadgeToolPresenter

    @Inject
    lateinit var fontAdapter: FontAdapter

    private var badgeBinding: FragmentToolBadgeBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentToolBadgeBinding.inflate(inflater, container, false).apply {
        badgeBinding = this
        presenter.attachView(this@BadgeTool)
        setFragmentResultListener(KEY_REQ_MIX_COLOR) { _, result ->
            presenter.setBadgeColor(result.getInt(ARG_COLOR))
        }
    }.run { root }

    override fun onDestroyView() {
        badgeBinding = null
        clearFragmentResult(KEY_REQ_MIX_COLOR)
        presenter.detachView()
        super.onDestroyView()
    }

    override fun onEmit(badgeToolPref: BadgeToolPref) {
        handleBadgeHide(badgeToolPref.badgeEnable)
        badgeBinding?.apply {
            with(toolBadgeHide) {
                isChecked = badgeToolPref.badgeEnable
                setOnCheckedChangeListener { cb: CompoundButton, isChecked: Boolean ->
                    cb.preventMultipleClick {
                        if (badgeToolPref.badgeEnable != isChecked) {
                            badgeToolPref.badgeEnable = isChecked
                            handleBadgeHide(isChecked)
                        }
                    }
                }
            }
            toolBadgeColorPick.setOnClickListener {
                it.preventMultipleClick {
                    findNavController().navigate(
                        BadgeToolDirections.actionToolsBadgeToColorMix(
                            color = badgeToolPref.badgeColor,
                            withAlpha = false,
                            withHex = true
                        )
                    )
                }
            }

            toolBadgeInput.apply {
                setText(badgeToolPref.badgeText)
                addInputFilter(AllCaps(), LengthFilter(16)) //
                onEditorAction { actionId ->
                    handleBadgeInput(badgeToolPref) { actionId == IME_ACTION_DONE }
                }
                onKey { keyCode, keyEvent ->
                    handleBadgeInput(badgeToolPref) {
                        keyEvent.action == ACTION_DOWN && keyCode == KEYCODE_ENTER
                    }
                }
            }

            toolBadgeSize.apply {
                value = badgeToolPref.badgeSize
                doOnStopTouch { slider ->
                    if (badgeToolPref.badgeSize != slider.value) {
                        badgeToolPref.badgeSize = slider.value
                    }
                }
            }

            toolBadgePosition.apply {
                setSelection(badgeToolPref.badgePosition.ordinal, false)
                setOnItemSelected { _, v, position, _ ->
                    v?.preventMultipleClick {
                        if (badgeToolPref.badgePosition.ordinal != position) {
                            badgeToolPref.badgePosition = BadgePosition.values()[position]
                        }
                    }
                }
            }

            toolBadgeFont.apply {
                adapter = fontAdapter
                setOnItemSelected { _, v, position, _ ->
                    v?.preventMultipleClick {
                        fontAdapter.setSelection(position)
                        presenter.setBadgeFont(position)
                    }
                }
            }
        }
    }

    override fun submitListAdapter(list: List<String>, current: Int, enable: Boolean) {
        fontAdapter.submitList(list, current)
        badgeBinding?.toolBadgeFont?.apply {
            isEnabled = list.size > 1 && enable // NOTE: size == 1 -> only DEFAULT
            setSelection(current, false)
        }
    }

    override fun onError(e: Throwable) {
        Toast.makeText(requireContext(), e.localizedMessage ?: "Oops", Toast.LENGTH_SHORT).show()
        Timber.e(e)
    }

    private fun handleBadgeHide(enable: Boolean) { //
        badgeBinding?.apply {
            toolBadgeLayout.forEach { view: View ->
                view.isEnabled = enable
                (view as? ViewGroup)?.forEach {
                    if (it == toolBadgeFont) {
                        it.isEnabled = fontAdapter.count > 1 && enable
                    } else {
                        it.isEnabled = enable
                    }
                }
            }
        }
    }

    private inline fun TextView.handleBadgeInput(
        pref: BadgeToolPref, crossinline condition: () -> Boolean
    ): Boolean = condition().also {
        if (it) {
            clearFocus()
            hideSoftKey()
            val normalizeText = text?.takeUnless(CharSequence?::isNullOrBlank)
                ?.toString() ?: "HISHOOT"
            if (pref.badgeText != normalizeText) pref.badgeText = normalizeText
            text = normalizeText
        }
    }
}
