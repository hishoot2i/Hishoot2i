package org.illegaller.ratabb.hishoot2i.ui.about

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import common.ext.preventMultipleClick
import dagger.hilt.android.AndroidEntryPoint
import org.illegaller.ratabb.hishoot2i.BuildConfig
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.databinding.FragmentAboutBinding
import javax.inject.Inject

@AndroidEntryPoint
class AboutFragment : Fragment(R.layout.fragment_about) {
    @Inject
    lateinit var adapter: AboutItemAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentAboutBinding.bind(view).apply {
            aboutList.adapter = adapter
            aboutBottomAppBar.setNavigationOnClickListener {
                it.preventMultipleClick { findNavController().popBackStack() }
            }
            aboutVersionText.text = versionName
        }
        generateAboutItems()
    }

    private fun generateAboutItems() {
        // TODO:
        val list = listOf(
            AboutItem("HomePage", "https://hishoot2i.github.io", R.drawable.ic_crop),
            AboutItem(
                "Fb Group",
                "https://www.facebook.com/groups/hishoot.template",
                R.drawable.ic_crop_free
            ),
            AboutItem("Dummy", "", R.drawable.ic_cancel),
            AboutItem("Dummy", "", R.drawable.ic_cancel),
            AboutItem("Dummy", "", R.drawable.ic_cancel),
            AboutItem("Dummy", "", R.drawable.ic_cancel),
            AboutItem("Dummy", "", R.drawable.ic_cancel),
            AboutItem("Dummy", "", R.drawable.ic_cancel),
            AboutItem("Dummy", "", R.drawable.ic_cancel),
            AboutItem("Dummy", "", R.drawable.ic_cancel),
            AboutItem("Dummy", "", R.drawable.ic_cancel),
            AboutItem("Dummy", "", R.drawable.ic_cancel),
            AboutItem("Dummy", "", R.drawable.ic_cancel),
            AboutItem("Dummy", "", R.drawable.ic_cancel),
            AboutItem("Dummy", "", R.drawable.ic_cancel)
        )
        adapter.submitList(list)
    }

    private val versionName by lazy {
        requireContext().getString(R.string.version_format, BuildConfig.VERSION_NAME)
    }
}