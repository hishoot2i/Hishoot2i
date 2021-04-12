package org.illegaller.ratabb.hishoot2i.ui.tools.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import common.view.preventMultipleClick
import dagger.hilt.android.AndroidEntryPoint
import org.illegaller.ratabb.hishoot2i.data.pref.ScreenToolPref
import org.illegaller.ratabb.hishoot2i.databinding.FragmentToolScreenBinding
import org.illegaller.ratabb.hishoot2i.ui.ARG_SCREEN1_PATH
import org.illegaller.ratabb.hishoot2i.ui.ARG_SCREEN2_PATH
import org.illegaller.ratabb.hishoot2i.ui.KEY_REQ_SCREEN_1
import org.illegaller.ratabb.hishoot2i.ui.KEY_REQ_SCREEN_2
import org.illegaller.ratabb.hishoot2i.ui.common.registerGetContent
import javax.inject.Inject

@AndroidEntryPoint
class ScreenTool : BottomSheetDialogFragment() {
    @Inject
    lateinit var screenToolPref: ScreenToolPref

    private val screenShoot1 = registerGetContent { uri ->
        setFragmentResult(
            KEY_REQ_SCREEN_1,
            bundleOf(ARG_SCREEN1_PATH to uri.toString())
        )
    }
    private val screenShoot2 = registerGetContent { uri ->
        setFragmentResult(
            KEY_REQ_SCREEN_2,
            bundleOf(ARG_SCREEN2_PATH to uri.toString())
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentToolScreenBinding.inflate(inflater, container, false).apply {
        toolScreen2.isEnabled = screenToolPref.doubleScreenEnable

        toolScreenDouble.isChecked = screenToolPref.doubleScreenEnable
        toolScreenDouble.setOnCheckedChangeListener { cb, isChecked ->
            cb.preventMultipleClick {
                if (screenToolPref.doubleScreenEnable != isChecked) {
                    screenToolPref.doubleScreenEnable = isChecked
                    toolScreen2.isEnabled = isChecked
                }
            }
        }

        toolScreen1.setOnClickListener {
            it.preventMultipleClick { screenShoot1.launch("image/*") }
        }
        toolScreen2.setOnClickListener {
            it.preventMultipleClick { screenShoot2.launch("image/*") }
        }
    }.run { root }
}
