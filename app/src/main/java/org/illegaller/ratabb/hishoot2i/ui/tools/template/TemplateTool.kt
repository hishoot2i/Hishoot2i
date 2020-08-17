package org.illegaller.ratabb.hishoot2i.ui.tools.template

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.navigation.fragment.findNavController
import common.ext.isVisible
import common.ext.preventMultipleClick
import common.ext.toDateTimeFormat
import dagger.hilt.android.AndroidEntryPoint
import imageloader.ImageLoader
import org.illegaller.ratabb.hishoot2i.GlobalDirections
import org.illegaller.ratabb.hishoot2i.NavigationDirections
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.data.pref.AppPref
import org.illegaller.ratabb.hishoot2i.ui.tools.BaseTools
import template.Template
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TemplateTool : BaseTools(), TemplateToolView {
    @Inject
    lateinit var presenter: TemplateToolPresenter

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun tagName(): String = "TemplateTool"
    override fun layoutRes(): Int = R.layout.fragment_tool_template

    /**/
    private lateinit var toolTemplateManager: View
    private lateinit var toolTemplatePreview: ImageView
    private lateinit var toolTemplateName: TextView
    private lateinit var toolTemplateId: TextView
    private lateinit var toolTemplateInfo: TextView
    private lateinit var toolTemplateSwitchFrame: SwitchCompat
    private lateinit var toolTemplateSwitchGlare: SwitchCompat
    private lateinit var toolTemplateSwitchShadow: SwitchCompat
    private lateinit var toolTemplateOption: View

    /**/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(view) {
            toolTemplateManager = findViewById(R.id.toolTemplateManager)
            toolTemplatePreview = findViewById(R.id.toolTemplatePreview)
            toolTemplateName = findViewById(R.id.toolTemplateName)
            toolTemplateId = findViewById(R.id.toolTemplateId)
            toolTemplateInfo = findViewById(R.id.toolTemplateInfo)
            toolTemplateSwitchFrame = findViewById(R.id.toolTemplateSwitchFrame)
            toolTemplateSwitchGlare = findViewById(R.id.toolTemplateSwitchGlare)
            toolTemplateSwitchShadow = findViewById(R.id.toolTemplateSwitchShadow)
            toolTemplateOption = findViewById(R.id.toolTemplateOption)
        }
        presenter.attachView(this)
        toolTemplateManager.setOnClickListener {
            it.preventMultipleClick {
                findNavController().navigate(GlobalDirections.template())
            }
            dismissAllowingStateLoss()
        }
    }

    override fun onDestroyView() {
        presenter.detachView()
        super.onDestroyView()
    }

    override fun currentTemplate(template: Template, appPref: AppPref) {
        val imSize = with(toolTemplatePreview) {
            return@with if (width != 0 || height != 0) entity.Sizes(width, height)
            else null
        }
        with(template) {
            if (this is Template.Version2 || this is Template.Version3) enableOptions(appPref)
            else disableOptions()

            imageLoader.display(toolTemplatePreview, preview, imSize)
            toolTemplateName.text = name
            toolTemplateId.text = id
            context?.getString(
                R.string.template_info_format,
                author,
                desc,
                installedDate.toDateTimeFormat()
            )?.let {
                toolTemplateInfo.text = it
            }
        }
    }

    override fun onError(e: Throwable) {
        Timber.e(e)
        // TODO ??
    }

    private fun enableOptions(appPref: AppPref) {
        toolTemplateOption.isVisible = true
        with(toolTemplateSwitchFrame) {
            isEnabled = true
            isChecked = appPref.templateFrameEnable
            setOnCheckedChangeListener { cb: CompoundButton, isChecked: Boolean ->
                cb.preventMultipleClick { appPref.templateFrameEnable = isChecked }
            }
        }
        with(toolTemplateSwitchShadow) {
            isEnabled = true
            isChecked = appPref.templateShadowEnable
            setOnCheckedChangeListener { cb: CompoundButton, isChecked: Boolean ->
                cb.preventMultipleClick { appPref.templateShadowEnable = isChecked }
            }
        }
        with(toolTemplateSwitchGlare) {
            isEnabled = true
            isChecked = appPref.templateGlareEnable
            setOnCheckedChangeListener { cb: CompoundButton, isChecked: Boolean ->
                cb.preventMultipleClick { appPref.templateGlareEnable = isChecked }
            }
        }
    }

    private fun disableOptions() {
        toolTemplateOption.isVisible = false
        toolTemplateSwitchFrame.isEnabled = false
        toolTemplateSwitchShadow.isEnabled = false
        toolTemplateSwitchGlare.isEnabled = false
    }
}
