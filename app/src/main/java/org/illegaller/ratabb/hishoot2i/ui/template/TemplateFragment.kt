package org.illegaller.ratabb.hishoot2i.ui.template

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import common.ext.hideSoftKey
import common.ext.isKeyboardOpen
import common.ext.isVisible
import common.ext.preventMultipleClick
import dagger.hilt.android.AndroidEntryPoint
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.ui.common.rx.RxSearchView
import template.Template
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TemplateFragment : Fragment(R.layout.fragment_template), TemplateView {
    @Inject
    lateinit var adapter: TemplateAdapter

    @Inject
    lateinit var presenter: TemplatePresenter
    private lateinit var templateRecyclerView: RecyclerView
    private lateinit var templateSearchView: SearchView
    private lateinit var templateBottomAppBar: BottomAppBar
    private lateinit var templateHtzFab: FloatingActionButton
    private lateinit var noContent: View
    private lateinit var loading: View
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(view) {
            templateRecyclerView = findViewById(R.id.templateRecyclerView)
            templateSearchView = findViewById(R.id.templateSearchView)
            templateBottomAppBar = findViewById(R.id.templateBottomAppBar)
            templateHtzFab = findViewById(R.id.templateHtzFab)
            noContent = findViewById(R.id.noContent)
            loading = findViewById(R.id.loading)
        }
        templateHtzFab.setOnClickListener(::htzFabClick)
        templateBottomAppBar.apply {
            setOnMenuItemClickListener(::menuItemClick)
            setNavigationOnClickListener(::popBack)
        }
        adapter.clickItem = ::adapterItemClick
        templateRecyclerView.adapter = adapter
        presenter.attachView(this)
        presenter.search(RxSearchView.queryTextChange(templateSearchView))
        presenter.render()
    }

    override fun onDestroyView() {
        presenter.detachView()
        super.onDestroyView()
    }

    override fun setData(data: List<Template>) {
        if (data.isNotEmpty()) {
            val state = templateRecyclerView.layoutManager?.onSaveInstanceState()
            adapter.submitList(data) //
            templateRecyclerView.layoutManager?.onRestoreInstanceState(state)
            templateRecyclerView.isVisible = true
            noContent.isVisible = false
            //
        } else {
            templateRecyclerView.isVisible = false
            noContent.isVisible = true
        }
    }

    override fun showProgress() {
        loading.isVisible = true
        templateRecyclerView.isVisible = false
    }

    override fun hideProgress() {
        loading.isVisible = false
    }

    override fun onError(e: Throwable) {
        Timber.e(e) //
    }

    ///
    private fun popBack(view: View) {
        // check if is Keyboard Open
        val isKeyboardOpen = requireActivity().isKeyboardOpen()
        Timber.d("onSoftKey= $isKeyboardOpen")
        if (isKeyboardOpen) {
            view.hideSoftKey()
        } else {
            findNavController().popBackStack()
        }
    }

    private fun menuItemClick(menuItem: MenuItem): Boolean =
        menuItem.preventMultipleClick {
            return when (menuItem.itemId) {
                R.id.action_sort_template -> {
                    SortTemplateDialog().apply {
                        callback = { presenter.render() }
                    }
                        .show(childFragmentManager)
                    true
                }
                else -> false
            }
        }

    private fun htzFabClick(view: View) {
        Snackbar.make(view, "TODO: Import htz!!", Snackbar.LENGTH_SHORT)
            .setAnchorView(view)
            .show()
    }

    private fun adapterItemClick(template: Template) {
        if (presenter.setCurrentTemplate(template)) {
            Snackbar.make(
                templateRecyclerView,
                getString(R.string.apply_template_format, template.name),
                Snackbar.LENGTH_SHORT
            )
                .setAnchorView(templateHtzFab)
                .show()
        }
    }
}

