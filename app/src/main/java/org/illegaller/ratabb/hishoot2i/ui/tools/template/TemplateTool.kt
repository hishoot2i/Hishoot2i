package org.illegaller.ratabb.hishoot2i.ui.tools.template

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import common.text.toDateTimeFormat
import common.view.preventMultipleClick
import dagger.hilt.android.AndroidEntryPoint
import imageloader.ImageLoader
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.data.pref.TemplateToolPref
import org.illegaller.ratabb.hishoot2i.databinding.FragmentToolTemplateBinding
import org.illegaller.ratabb.hishoot2i.ui.common.viewObserve
import template.Template.Version2
import template.Template.Version3
import template.Template.VersionHtz
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
        viewObserve(viewModel.uiState) { uiState ->
            when (uiState) {
                is Fail -> {
                    val message = uiState.cause.localizedMessage ?: "Oops"
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    Timber.e(uiState.cause)
                }
                is Success -> currentTemplate(uiState)
            }
        }
        toolTemplateManager.setOnClickListener {
            it.preventMultipleClick {
                findNavController().navigate(
                    TemplateToolDirections.actionGlobalTemplate()
                )
            }
            dismiss()
        }
    }.run { root }

    private fun FragmentToolTemplateBinding.currentTemplate(success: Success) {
        val template = success.template
        val pref = success.pref
        val isSupportOption = template is Version2 || template is Version3 || template is VersionHtz
        textOption.isVisible = isSupportOption
        textOptionInfo.isVisible = isSupportOption
        toolTemplateToggleGroup.isVisible = isSupportOption
        if (isSupportOption) {
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
        imageLoader.display(toolTemplatePreview, viewLifecycleOwner, template.preview)
        toolTemplateName.text = template.name
        toolTemplateId.text = template.id
        toolTemplateInfo.apply {
            text = context.getString(
                R.string.template_info_format,
                template.author,
                template.desc,
                template.installedDate toDateTimeFormat "yyyy-MMM-dd HH:mm:ss"
            )
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
