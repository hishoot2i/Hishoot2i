package org.illegaller.ratabb.hishoot2i.ui.template

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.clearFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import common.ext.hideSoftKey
import common.ext.isKeyboardOpen
import common.ext.isVisible
import common.ext.preventMultipleClick
import common.ext.toFile
import dagger.hilt.android.AndroidEntryPoint
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.databinding.FragmentTemplateBinding
import org.illegaller.ratabb.hishoot2i.ui.ARG_SORT
import org.illegaller.ratabb.hishoot2i.ui.KEY_REQ_SORT
import org.illegaller.ratabb.hishoot2i.ui.common.registerGetContent
import org.illegaller.ratabb.hishoot2i.ui.common.rx.RxSearchView
import org.illegaller.ratabb.hishoot2i.ui.common.showSnackBar
import template.Template
import template.TemplateComparator
import timber.log.Timber
import javax.inject.Inject

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTemplateBinding.bind(view)
        templateBinding = binding
        adapter.clickItem = ::adapterItemClick
        binding.apply {
            templateRecyclerView.apply {
                adapter = this@TemplateFragment.adapter
                addItemDecoration(TemplateListDivider(requireContext()))
            }
            templateHtzFab.setOnClickListener { requestHtz.launch("*/*") }
            templateBottomAppBar.apply {
                setOnMenuItemClickListener { menuItemClick(it) }
                setNavigationOnClickListener { popBack(it) }
            }
        }
        presenter.attachView(this)
        presenter.search(RxSearchView.queryTextChange(binding.templateSearchView))
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
            noContent.noContent.isVisible = !haveData
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
