package org.illegaller.ratabb.hishoot2i.ui.tools.template

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
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
import org.illegaller.ratabb.hishoot2i.ui.tools.template.TemplateToolDirections.Companion.actionGlobalTemplate
import template.Template
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TemplateTool : BottomSheetDialogFragment() {

    @Inject
    lateinit var imageLoader: ImageLoader

    private val viewModel: TemplateToolViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentToolTemplateBinding.inflate(inflater, container, false).apply {
        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                is Fail -> {
                    val message = it.cause.localizedMessage ?: "Oops"
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    Timber.e(it.cause)
                }
                is Success -> currentTemplate(it)
            }
        }
        toolTemplateManager.setOnClickListener {
            it.preventMultipleClick {
                findNavController().navigate(actionGlobalTemplate())
            }
            dismiss()
        }
    }.run { root }

    private fun FragmentToolTemplateBinding.currentTemplate(success: Success) {
        val template = success.template
        val pref = success.pref
        when (template) {
            is Template.Version2, is Template.Version3 -> enableOptions(pref)
            else -> toolTemplateOption.isVisible = false
        }
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

    private fun FragmentToolTemplateBinding.enableOptions(pref: TemplateToolPref) {
        toolTemplateOption.isVisible = true
        getCheckedIds(pref).takeIf { it.isNotEmpty() }?.onEach {
            toolTemplateToggleGroup.check(it)
        }
        toolTemplateToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when (checkedId) {
                R.id.toolTemplateSwitchFrame -> pref.templateFrameEnable = isChecked
                R.id.toolTemplateSwitchGlare -> pref.templateGlareEnable = isChecked
                R.id.toolTemplateSwitchShadow -> pref.templateShadowEnable = isChecked
            }
        }
    }

    private val getCheckedIds: TemplateToolPref.() -> List<Int> = {
        val result = mutableListOf<Int>()
        if (templateFrameEnable) result += R.id.toolTemplateSwitchFrame
        if (templateGlareEnable) result += R.id.toolTemplateSwitchGlare
        if (templateShadowEnable) result += R.id.toolTemplateSwitchShadow
        result
    }
}
