package org.illegaller.ratabb.hishoot2i.ui.main.tools.template

import android.os.Bundle
import android.support.v7.widget.SwitchCompat
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.data.pref.AppPref
import org.illegaller.ratabb.hishoot2i.ui.main.tools.AbsTools
import org.illegaller.ratabb.hishoot2i.ui.template.TemplateManagerActivity
import rbb.hishoot2i.common.ext.preventMultipleClick
import rbb.hishoot2i.common.ext.toDateTimeFormat
import rbb.hishoot2i.common.imageloader.ImageLoader
import rbb.hishoot2i.template.Template
import timber.log.Timber
import javax.inject.Inject

class TemplateTool : AbsTools(), TemplateToolView {
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
        }
        presenter.attachView(this)
        toolTemplateManager.setOnClickListener {
            it.preventMultipleClick { TemplateManagerActivity.start(it.context) }
            dismiss()
        }
    }

    override fun onDestroyView() {
        presenter.detachView()
        super.onDestroyView()
    }

    override fun currentTemplate(template: Template, appPref: AppPref) {
        with(template) {
            when (this) {
                is Template.Version2,
                is Template.Version3 -> enableOptions(appPref)
                else -> disableOptions()
            }

            imageLoader.display(toolTemplatePreview, preview)
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
        // context?.toast(e.localizedMessage)
    }

    private fun enableOptions(appPref: AppPref) {
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
        toolTemplateSwitchFrame.isEnabled = false
        toolTemplateSwitchShadow.isEnabled = false
        toolTemplateSwitchGlare.isEnabled = false
    }
}