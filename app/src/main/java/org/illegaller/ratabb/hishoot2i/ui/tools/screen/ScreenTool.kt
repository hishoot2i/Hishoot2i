package org.illegaller.ratabb.hishoot2i.ui.tools.screen

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import common.ext.compoundVectorDrawables
import common.ext.isVisible
import common.ext.preventMultipleClick
import dagger.hilt.android.AndroidEntryPoint
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.data.pref.AppPref
import org.illegaller.ratabb.hishoot2i.ui.tools.BaseTools
import javax.inject.Inject

@AndroidEntryPoint
class ScreenTool : BaseTools() {
    @Inject
    lateinit var appPref: AppPref

    override fun tagName(): String = "ScreenTool"
    override fun layoutRes(): Int = R.layout.fragment_tool_screen
    private lateinit var toolScreenSwitchDoubleSS: SwitchCompat
    private lateinit var toolScreen1: AppCompatTextView
    private lateinit var toolScreen2: AppCompatTextView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(view) {
            toolScreenSwitchDoubleSS = findViewById(R.id.toolScreenSwitchDoubleSS)
            toolScreen1 = findViewById(R.id.toolScreen1)
            toolScreen2 = findViewById(R.id.toolScreen2)
        }

        /*toolScreen1.compoundVectorDrawables(
            top = R.drawable.ic_image_black_24dp,
            tint = R.color.accent
        )
        toolScreen2.compoundVectorDrawables(
            top = R.drawable.ic_image_black_24dp,
            tint = R.color.accent
        )*/

        toolScreen2.isVisible = appPref.doubleScreenEnable
        with(toolScreenSwitchDoubleSS) {
            isChecked = appPref.doubleScreenEnable
            setOnCheckedChangeListener { cb: CompoundButton, isChecked: Boolean ->
                cb.preventMultipleClick {
                    if (appPref.doubleScreenEnable != isChecked) {
                        appPref.doubleScreenEnable = isChecked
                        toolScreen2.isVisible = isChecked
                    }
                }
            }
        }
        toolScreen1.setOnClickListener {
            it.preventMultipleClick {
//                startActivityForResult(
//                    chooserGetContentWith(type = "image/*", title = getString(R.string.screen_1)),
//                    REQ_IMAGE_PICK_1
//                )
                getContentImage1.launch("image/*")
            }
        }
        toolScreen2.setOnClickListener {
            it.preventMultipleClick {
//                startActivityForResult(
//                    chooserGetContentWith(type = "image/*", title = getString(R.string.screen_2)),
//                    REQ_IMAGE_PICK_2
//                )
                getContentImage2.launch("image/*")
            }
        }
    }

    private val getContentImage1 = registerForActivityResult(GetContent()) { uri ->
        callback?.changePathScreen1(uri.toString())
    }
    private val getContentImage2 = registerForActivityResult(GetContent()) { uri ->
        callback?.changePathScreen2(uri.toString())
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
       // super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        val dataString = data?.dataString ?: return
        when (requestCode) {
            REQ_IMAGE_PICK_1 -> callback?.changePathScreen1(dataString)
            REQ_IMAGE_PICK_2 -> callback?.changePathScreen2(dataString)
        }
    }*/

    companion object {
//        private const val REQ_IMAGE_PICK_1 = 0x01
//        private const val REQ_IMAGE_PICK_2 = 0x02
    }
}
