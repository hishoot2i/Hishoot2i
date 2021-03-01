package org.illegaller.ratabb.hishoot2i.ui.template

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.clearFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSnapHelper
import common.ext.hideSoftKey
import common.ext.isKeyboardOpen
import common.ext.isVisible
import common.ext.preventMultipleClick
import common.ext.toFile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.databinding.FragmentTemplateBinding
import org.illegaller.ratabb.hishoot2i.ui.ARG_SORT
import org.illegaller.ratabb.hishoot2i.ui.KEY_REQ_SORT
import org.illegaller.ratabb.hishoot2i.ui.common.SideListDivider
import org.illegaller.ratabb.hishoot2i.ui.common.broadcastReceiver
import org.illegaller.ratabb.hishoot2i.ui.common.queryTextChange
import org.illegaller.ratabb.hishoot2i.ui.common.registerGetContent
import org.illegaller.ratabb.hishoot2i.ui.common.showSnackBar
import template.Template
import template.TemplateComparator
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TemplateFragment : Fragment(R.layout.fragment_template), TemplateView {
    @Inject
    lateinit var adapter: TemplateAdapter

    @Inject
    lateinit var presenter: TemplatePresenter

    private var templateBinding: FragmentTemplateBinding? = null

    private val requestHtz = registerGetContent { uri ->
        DocumentFile.fromSingleUri(requireContext(), uri)?.uri?.toFile(requireContext())?.let {
            presenter.importHtz(it)
        }
    }

    companion object {
        @JvmStatic
        @Suppress("DEPRECATION")
        private val PKG_UPDATE_INTENT = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_INSTALL)
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }
    }

    private val receiver: BroadcastReceiver by broadcastReceiver { _, _ -> presenter.render() }

    override fun onResume() {
        super.onResume()
        requireActivity().registerReceiver(receiver, PKG_UPDATE_INTENT)
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(receiver)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTemplateBinding.bind(view)
        templateBinding = binding
        adapter.clickItem = ::adapterItemClick
        binding.apply {
            templateRecyclerView.apply {
                adapter = this@TemplateFragment.adapter
                SideListDivider.addItemDecorToRecyclerView(this)
                LinearSnapHelper().attachToRecyclerView(this)
                setHasFixedSize(true)
            }
            templateHtzFab.setOnClickListener { requestHtz.launch("*/*") }
            templateBottomAppBar.apply {
                setOnMenuItemClickListener { menuItemClick(it) }
                setNavigationOnClickListener { popBack(it) }
            }
        }
        presenter.attachView(this)
        presenter.search(binding.templateSearchView.queryTextChange())
        presenter.render()
        setFragmentResultListener(KEY_REQ_SORT) { _, result ->
            presenter.templateComparator = TemplateComparator.values()[result.getInt(ARG_SORT)]
        }
    }

    override fun onDestroyView() {
        templateBinding = null
        clearFragmentResult(KEY_REQ_SORT)
        presenter.detachView()
        super.onDestroyView()
    }

    override fun setData(templates: List<Template>) {
        templateBinding?.apply {
            val haveData = templates.isNotEmpty()
            if (haveData) {
                val state = templateRecyclerView.layoutManager?.onSaveInstanceState()
                adapter.submitList(templates) //
                templateRecyclerView.layoutManager?.onRestoreInstanceState(state)
            }
            templateRecyclerView.isVisible = haveData
            noContent.isVisible = !haveData
        }
    }

    override fun showProgress() {
        templateBinding?.apply {
            templateProgress.show()
            templateRecyclerView.isVisible = false
        }
    }

    override fun hideProgress() {
        templateBinding?.templateProgress?.hide()
    }

    override fun htzImported(templateHtz: Template.VersionHtz) {
        showSnackBar(
            view = requireView(),
            text = getString(R.string.template_htz_add_format, templateHtz.name),
            anchorViewId = R.id.templateHtzFab
        )
    }

    override fun onError(e: Throwable) {
        Toast.makeText(requireContext(), e.localizedMessage ?: "Oops", Toast.LENGTH_SHORT).show()
        Timber.e(e) //
    }

    private fun popBack(view: View) {
        // FIXME: check if is Keyboard Open?
        val isKeyboardOpen = requireActivity().isKeyboardOpen()
        Timber.d("isKeyboardOpen= $isKeyboardOpen")
        if (isKeyboardOpen) {
            view.hideSoftKey()
            findNavController().popBackStack()
        } else {
            findNavController().popBackStack()
        }
    }

    private fun menuItemClick(menuItem: MenuItem): Boolean =
        menuItem.preventMultipleClick {
            return when (menuItem.itemId) {
                R.id.action_sort_template -> {
                    findNavController().navigate(
                        TemplateFragmentDirections
                            .actionTemplateToSortTemplate(presenter.templateComparator)
                    )
                    true
                }
                else -> false
            }
        }

    private fun adapterItemClick(template: Template) {
        if (presenter.setCurrentTemplate(template)) {
            showSnackBar(
                view = requireView(),
                text = getString(R.string.apply_template_format, template.name),
                anchorViewId = R.id.templateHtzFab
            )
        }
    }
}
