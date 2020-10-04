package org.illegaller.ratabb.hishoot2i.ui.tools.template

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import common.ext.dpSize
import common.ext.isVisible
import common.ext.preventMultipleClick
import common.ext.toDateTimeFormat
import dagger.hilt.android.AndroidEntryPoint
import entity.Sizes
import imageloader.ImageLoader
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.data.pref.TemplateToolPref
import org.illegaller.ratabb.hishoot2i.databinding.FragmentToolTemplateBinding
import template.Template
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TemplateTool : BottomSheetDialogFragment(), TemplateToolView {
    @Inject
    lateinit var presenter: TemplateToolPresenter

    @Inject
    lateinit var imageLoader: ImageLoader

    private var templateBinding: FragmentToolTemplateBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        FragmentToolTemplateBinding.inflate(inflater, container, false).apply {
            templateBinding = this
            presenter.attachView(this@TemplateTool)
            toolTemplateManager.setOnClickListener {
                it.preventMultipleClick {
                    findNavController().navigate(TemplateToolDirections.actionGlobalTemplate())
                }
                dismiss()
            }
        }.run { return@onCreateView root }
    }

    override fun onDestroyView() {
        templateBinding?.toolTemplateToggleGroup?.clearOnButtonCheckedListeners()
        templateBinding = null
        presenter.detachView()
        super.onDestroyView()
    }

    override fun currentTemplate(template: Template, templateToolPref: TemplateToolPref) {
        when (template) {
            is Template.Version2, is Template.Version3 -> enableOptions(templateToolPref)
            else -> disableOptions()
        }
        templateBinding?.apply {
            toolTemplatePreview.apply {
                val (w, h) = context.run {
                    dpSize(R.dimen.toolPreviewWidth) to dpSize(R.dimen.toolPreviewHeight)
                }
                imageLoader.display(this, template.preview, Sizes(w, h))
            }
            toolTemplateName.text = template.name
            toolTemplateId.text = template.id
            toolTemplateInfo.apply {
                text = context.getString(
                    R.string.template_info_format,
                    template.author,
                    template.desc,
                    template.installedDate.toDateTimeFormat()
                )
            }
        }
    }

    override fun onError(e: Throwable) {
        Toast.makeText(requireContext(), e.localizedMessage ?: "Oops", Toast.LENGTH_SHORT).show()
        Timber.e(e)
    }

    private fun enableOptions(templateToolPref: TemplateToolPref) {
        templateBinding?.apply {
            toolTemplateOption.isVisible = true
            val checkedIds = getCheckedIds(templateToolPref)
            toolTemplateToggleGroup.apply {
                checkedIds.takeIf { it.isNotEmpty() }?.onEach { check(it) }
                addOnButtonCheckedListener { _, checkedId, isChecked ->
                    when (checkedId) {
                        R.id.toolTemplateSwitchFrame -> templateToolPref.templateFrameEnable =
                            isChecked
                        R.id.toolTemplateSwitchGlare -> templateToolPref.templateGlareEnable =
                            isChecked
                        R.id.toolTemplateSwitchShadow -> templateToolPref.templateShadowEnable =
                            isChecked
                    }
                }
            }
        }
    }

    private fun disableOptions() {
        templateBinding?.toolTemplateOption?.isVisible = false
    }

    private val getCheckedIds: (TemplateToolPref) -> List<Int> = { templateToolPref ->
        val result = mutableListOf<Int>()
        if (templateToolPref.templateFrameEnable) result += R.id.toolTemplateSwitchFrame
        if (templateToolPref.templateGlareEnable) result += R.id.toolTemplateSwitchGlare
        if (templateToolPref.templateShadowEnable) result += R.id.toolTemplateSwitchShadow
        result
    }
}
