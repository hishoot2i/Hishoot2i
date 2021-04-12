package org.illegaller.ratabb.hishoot2i.ui.template

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.clearFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSnapHelper
import common.content.lazyBroadcastReceiver
import common.net.toFile
import common.view.hideSoftKey
import common.view.preventMultipleClick
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.illegaller.ratabb.hishoot2i.HiShootActivity
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.data.pref.TemplatePref
import org.illegaller.ratabb.hishoot2i.data.pref.TemplateToolPref
import org.illegaller.ratabb.hishoot2i.databinding.FragmentTemplateBinding
import org.illegaller.ratabb.hishoot2i.ui.ARG_SORT
import org.illegaller.ratabb.hishoot2i.ui.KEY_REQ_SORT
import org.illegaller.ratabb.hishoot2i.ui.common.SideListDivider
import org.illegaller.ratabb.hishoot2i.ui.common.queryTextChange
import org.illegaller.ratabb.hishoot2i.ui.common.registerGetContent
import org.illegaller.ratabb.hishoot2i.ui.common.showSnackBar
import org.illegaller.ratabb.hishoot2i.ui.common.viewObserve
import template.Template
import template.TemplateComparator
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TemplateFragment : Fragment(R.layout.fragment_template) {
    @Inject
    lateinit var templateAdapter: TemplateAdapter

    @Inject
    lateinit var templatePref: TemplatePref

    @Inject
    lateinit var templateToolPref: TemplateToolPref

    private val viewModel: TemplateViewModel by viewModels()

    private val requestHtz = registerGetContent { uri ->
        DocumentFile.fromSingleUri(requireContext(), uri)?.uri?.toFile(requireContext())?.let {
            viewModel.importFileHtz(it)
        }
    }

    private val receiver: BroadcastReceiver by lazyBroadcastReceiver { _, _ -> viewModel.perform() }

    override fun onResume() {
        super.onResume()
        requireActivity().registerReceiver(
            receiver,
            IntentFilter().apply {
                // addAction(Intent.ACTION_PACKAGE_INSTALL)
                addAction(Intent.ACTION_PACKAGE_ADDED)
                addAction(Intent.ACTION_PACKAGE_CHANGED)
                addAction(Intent.ACTION_PACKAGE_REMOVED)
                addAction(Intent.ACTION_PACKAGE_REPLACED)
                addDataScheme("package")
            }
        )
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(receiver)
    }

    @FlowPreview
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentTemplateBinding.bind(view).apply {
            setViewListener()
            viewObserve(viewModel.uiState) { observerView(it) }
            viewObserve(viewModel.htzState) { observerHtzView(it) }
            viewModel.search(templateSearchView.queryTextChange())
        }
        viewModel.perform()
        setFragmentResultListener(KEY_REQ_SORT) { _, result ->
            templatePref.templateComparator = TemplateComparator.values()[result.getInt(ARG_SORT)]
            viewModel.perform() // TODO: handle view on Search ?
        }
    }

    override fun onDestroyView() {
        clearFragmentResult(KEY_REQ_SORT)
        super.onDestroyView()
    }

    private fun FragmentTemplateBinding.observerHtzView(state: HtzEventView) {
        when (state) {
            LoadingHtzEvent -> templateProgress.show()
            is FailHtzEvent -> {
                templateProgress.hide()
                onError(state.cause)
            }
            is SuccessHtzEvent -> {
                templateProgress.hide()
                htzNotify(state.event, state.message)
            }
        }
    }

    private fun FragmentTemplateBinding.observerView(state: TemplateView) {
        when (state) {
            Loading -> templateProgress.show()
            is Fail -> {
                templateProgress.hide()
                onError(state.cause)
            }
            is Success -> {
                templateProgress.hide()
                setData(state.data)
            }
        }
    }

    private fun FragmentTemplateBinding.setViewListener() {
        templateAdapter.clickItem = ::adapterItemClick
        templateAdapter.longClickItem = ::adapterItemLongClick
        templateRecyclerView.apply {
            adapter = templateAdapter
            SideListDivider.addItemDecorToRecyclerView(this)
            LinearSnapHelper().attachToRecyclerView(this)
            setHasFixedSize(true)
        }
        templateHtzFab.setOnClickListener { requestHtz.launch("*/*") }
        templateBottomAppBar.apply {
            setOnMenuItemClickListener(::menuItemClick)
            setNavigationOnClickListener(::popBack)
        }
    }

    private fun FragmentTemplateBinding.setData(templates: List<Template>) {
        val haveData = templates.isNotEmpty()
        if (haveData) {
            val state = templateRecyclerView.layoutManager?.onSaveInstanceState()
            templateAdapter.submitList(templates) //
            templateRecyclerView.layoutManager?.onRestoreInstanceState(state)
        }
        templateRecyclerView.isVisible = haveData
        noContent.isVisible = !haveData
    }

    private fun htzNotify(htzEvent: HtzEvent, message: String) {
        val format = when (htzEvent) {
            HtzEvent.IMPORT -> R.string.template_htz_imported_format
            HtzEvent.CONVERT -> R.string.template_htz_converted_format
            HtzEvent.EXPORT -> R.string.template_htz_exported_format
            HtzEvent.REMOVE -> R.string.template_htz_removed_format
        }
        showSnackBar(
            view = requireView(),
            text = getString(format, message),
            anchorViewId = R.id.templateHtzFab
        )
    }

    private fun onError(e: Throwable) {
        Toast.makeText(requireContext(), e.localizedMessage ?: "Oops", Toast.LENGTH_SHORT).show()
        Timber.e(e) //
    }

    private fun popBack(view: View) {
        if ((requireActivity() as HiShootActivity).isKeyboardShow) view.hideSoftKey()
        else findNavController().popBackStack()
    }

    private fun menuItemClick(menuItem: MenuItem): Boolean = menuItem.preventMultipleClick {
        return when (menuItem.itemId) {
            R.id.action_sort_template -> {
                findNavController().navigate(
                    TemplateFragmentDirections.actionTemplateToSortTemplate(templatePref.templateComparator)
                )
                true
            }
            else -> false
        }
    }

    @SuppressLint("RestrictedApi") // <- MenuPopupHelper || TODO: replace with something else?
    private inline fun popupMenu(
        view: View,
        isHtz: Boolean,
        crossinline menuClick: (MenuItem) -> Boolean
    ) = PopupMenu(view.context, view).apply {
        inflate(R.menu.template_popup)
        if (isHtz) menu.findItem(R.id.action_convert_to_htz).isEnabled = false
        else {
            menu.findItem(R.id.action_export_htz).isEnabled = false
            menu.findItem(R.id.action_remove_htz).isEnabled = false
        }
        setOnMenuItemClickListener { menuClick(it) }
    }.run {
        MenuPopupHelper(view.context, menu as MenuBuilder, view)
            .apply { setForceShowIcon(true) }
            .show()
    }

    private fun adapterItemLongClick(view: View, template: Template): Boolean = when (template) {
        is Template.Default -> false
        is Template.VersionHtz -> {
            popupMenu(view, true) {
                return@popupMenu when (it.itemId) {
                    R.id.action_export_htz -> {
                        viewModel.exportTemplateHtz(template)
                        true
                    }
                    R.id.action_remove_htz -> {
                        viewModel.removeTemplateHtz(template)
                        true
                    }
                    else -> false
                }
            }
            true
        }
        is Template.Version1, is Template.Version2, is Template.Version3 -> {
            popupMenu(view, false) {
                return@popupMenu when (it.itemId) {
                    R.id.action_convert_to_htz -> {
                        viewModel.convertTemplateHtz(template)
                        true
                    }

                    else -> false
                }
            }
            true
        }
        else -> false //
    }

    private fun adapterItemClick(template: Template) {
        if (templateToolPref.templateCurrentId != template.id) {
            templateToolPref.templateCurrentId = template.id
            showSnackBar(
                view = requireView(),
                text = getString(R.string.apply_template_format, template.name),
                anchorViewId = R.id.templateHtzFab
            )
        }
    }
}
