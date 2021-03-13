package org.illegaller.ratabb.hishoot2i.ui.template

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.clearFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSnapHelper
import common.ext.hideSoftKey
import common.ext.isKeyboardOpen
import common.ext.preventMultipleClick
import common.ext.toFile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.data.pref.TemplatePref
import org.illegaller.ratabb.hishoot2i.data.pref.TemplateToolPref
import org.illegaller.ratabb.hishoot2i.databinding.FragmentTemplateBinding
import org.illegaller.ratabb.hishoot2i.ui.ARG_SORT
import org.illegaller.ratabb.hishoot2i.ui.KEY_REQ_SORT
import org.illegaller.ratabb.hishoot2i.ui.common.SideListDivider
import org.illegaller.ratabb.hishoot2i.ui.common.broadcastReceiver
import org.illegaller.ratabb.hishoot2i.ui.common.queryTextChange
import org.illegaller.ratabb.hishoot2i.ui.common.registerGetContent
import org.illegaller.ratabb.hishoot2i.ui.common.showSnackBar
import org.illegaller.ratabb.hishoot2i.ui.common.viewObserve
import org.illegaller.ratabb.hishoot2i.ui.template.TemplateFragmentDirections.Companion.actionTemplateToSortTemplate
import template.Template
import template.TemplateComparator
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TemplateFragment : Fragment(R.layout.fragment_template) {
    @Inject
    lateinit var adapter: TemplateAdapter

    @Inject
    lateinit var templatePref: TemplatePref

    @Inject
    lateinit var templateToolPref: TemplateToolPref

    private val viewModel: TemplateViewModel by viewModels()

    private val requestHtz = registerGetContent { uri ->
        DocumentFile.fromSingleUri(requireContext(), uri)?.uri?.toFile(requireContext())?.let {
            viewModel.importHtz(it)
        }
    }

    private val receiver: BroadcastReceiver by broadcastReceiver { _, _ -> viewModel.perform() }

    override fun onResume() {
        super.onResume()
        requireActivity().registerReceiver(
            receiver,
            IntentFilter().apply {
                // addAction(Intent.ACTION_PACKAGE_INSTALL) // <-- TODO: ?
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
            viewObserve(viewModel.uiState) { observer(it) }
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

    private fun FragmentTemplateBinding.observer(view: TemplateView) {
        when (view) {
            Loading -> {
                templateProgress.show()
                templateRecyclerView.isVisible = false
            }
            is HtzImported -> {
                templateProgress.hide()
                htzImported(view.htz)
            }
            is Fail -> {
                templateProgress.hide()
                onError(view.cause)
            }
            is Success -> {
                templateProgress.hide()
                setData(view.data)
            }
        }
    }

    private fun FragmentTemplateBinding.setViewListener() {
        adapter.clickItem = ::adapterItemClick
        templateRecyclerView.apply {
            adapter = this@TemplateFragment.adapter
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
        if (haveData) { // TODO:
            val state = templateRecyclerView.layoutManager?.onSaveInstanceState()
            adapter.submitList(templates) //
            templateRecyclerView.layoutManager?.onRestoreInstanceState(state)
        }
        templateRecyclerView.isVisible = haveData
        noContent.isVisible = !haveData
    }

    private fun htzImported(templateHtz: Template.VersionHtz) {
        showSnackBar(
            view = requireView(),
            text = getString(R.string.template_htz_add_format, templateHtz.name),
            anchorViewId = R.id.templateHtzFab
        )
    }

    private fun onError(e: Throwable) {
        Toast.makeText(requireContext(), e.localizedMessage ?: "Oops", Toast.LENGTH_SHORT).show()
        Timber.e(e) //
    }

    private fun popBack(view: View) {
        val isKeyboardOpen = requireActivity().isKeyboardOpen()
        Timber.d("isKeyboardOpen= $isKeyboardOpen")
        if (isKeyboardOpen) {
            view.hideSoftKey()
            findNavController().popBackStack()
        } else {
            findNavController().popBackStack()
        }
    }

    private fun menuItemClick(menuItem: MenuItem): Boolean = menuItem.preventMultipleClick {
        return when (menuItem.itemId) {
            R.id.action_sort_template -> {
                findNavController().navigate(
                    actionTemplateToSortTemplate(templatePref.templateComparator)
                )
                true
            }
            else -> false
        }
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
