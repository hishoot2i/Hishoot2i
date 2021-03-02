package org.illegaller.ratabb.hishoot2i.ui.main

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import common.ext.activityPendingIntent
import common.ext.graphics.sizes
import common.ext.preventMultipleClick
import core.Preview
import core.Save
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.illegaller.ratabb.hishoot2i.HiShootActivity
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.databinding.FragmentMainBinding
import org.illegaller.ratabb.hishoot2i.ui.ARG_BACKGROUND_PATH
import org.illegaller.ratabb.hishoot2i.ui.ARG_CROP_PATH
import org.illegaller.ratabb.hishoot2i.ui.ARG_PIPETTE_COLOR
import org.illegaller.ratabb.hishoot2i.ui.ARG_SCREEN1_PATH
import org.illegaller.ratabb.hishoot2i.ui.ARG_SCREEN2_PATH
import org.illegaller.ratabb.hishoot2i.ui.KEY_REQ_BACKGROUND
import org.illegaller.ratabb.hishoot2i.ui.KEY_REQ_CROP
import org.illegaller.ratabb.hishoot2i.ui.KEY_REQ_PIPETTE
import org.illegaller.ratabb.hishoot2i.ui.KEY_REQ_SCREEN_1
import org.illegaller.ratabb.hishoot2i.ui.KEY_REQ_SCREEN_2
import org.illegaller.ratabb.hishoot2i.ui.common.clearFragmentResultListeners
import org.illegaller.ratabb.hishoot2i.ui.common.setFragmentResultListeners
import org.illegaller.ratabb.hishoot2i.ui.common.showSnackBar
import org.illegaller.ratabb.hishoot2i.ui.main.MainFragmentDirections.Companion.actionMainToToolsBackground
import org.illegaller.ratabb.hishoot2i.ui.main.MainFragmentDirections.Companion.actionMainToToolsBadge
import org.illegaller.ratabb.hishoot2i.ui.main.MainFragmentDirections.Companion.actionMainToToolsScreen
import org.illegaller.ratabb.hishoot2i.ui.main.MainFragmentDirections.Companion.actionMainToToolsTemplate
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {
    @Inject
    lateinit var saveNotification: SaveNotification

    private val viewModel: MainViewModel by viewModels()

    private val requestKeys = arrayOf(
        KEY_REQ_CROP, KEY_REQ_BACKGROUND,
        KEY_REQ_SCREEN_1, KEY_REQ_SCREEN_2,
        KEY_REQ_PIPETTE
    )
    private val args: MainFragmentArgs by navArgs()
    private var ratioCrop = Point()
    private var isOnPipette: Boolean = false
    private var isOnProgress: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentMainBinding.bind(view).apply {
            setViewListener()
            viewModel.uiState.observe(viewLifecycleOwner) { observer(it) }
            setFragmentResultListeners(*requestKeys) { requestKey, result ->
                handleResult(requestKey, result)
            }
        }
        if (!handleReceiver()) viewModel.render()
    }

    override fun onDestroyView() {
        clearFragmentResultListeners(*requestKeys)
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        viewModel.resume()
    }

    private fun FragmentMainBinding.handleResult(requestKey: String, result: Bundle) {
        when (requestKey) {
            KEY_REQ_CROP -> result.getString(ARG_CROP_PATH)?.let {
                viewModel.changeBackground(it)
            }
            KEY_REQ_BACKGROUND -> result.getString(ARG_BACKGROUND_PATH)?.let {
                viewModel.changeBackground(it)
            }
            KEY_REQ_SCREEN_1 -> result.getString(ARG_SCREEN1_PATH)?.let {
                viewModel.changeScreen1(it)
            }
            KEY_REQ_SCREEN_2 -> result.getString(ARG_SCREEN2_PATH)?.let {
                viewModel.changeScreen2(it)
            }
            KEY_REQ_PIPETTE -> startingPipette(result.getInt(ARG_PIPETTE_COLOR))
        }
    }

    private fun FragmentMainBinding.observer(view: MainView) {
        when (view) {
            is Loading -> {
                showProgress()
                if (view.isFromSave) saveNotification.start()
            }
            is Fail -> {
                hideProgress()
                val msg = view.cause.localizedMessage ?: "Oops"
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                Timber.e(view.cause)
                if (view.isFromSave) saveNotification.error(view.cause)
            }
            is Success -> {
                hideProgress()
                when (view.result) {
                    is Preview -> preview(view.result.bitmap)
                    is Save -> saveComplete(view.result)
                }
            }
        }
    }

    private fun FragmentMainBinding.setViewListener() {
        mainFab.setOnClickListener {
            it.preventMultipleClick {
                if (!isOnPipette) viewModel.save() else stopPipette()
            }
        }
        mainBottomAppBar.setOnMenuItemClickListener {
            it.preventMultipleClick { bottomMenuClick(it) }
        }
        mainBottomAppBar.setNavigationOnClickListener {
            it.preventMultipleClick {
                if (isOnPipette) stopPipette(isCancel = true)
                (activity as? HiShootActivity)?.openDrawer()
            }
        }
    }

    private fun FragmentMainBinding.preview(bitmap: Bitmap) {
        ratioCrop = bitmap.sizes.run { Point(x, y) }
        mainImage.setImageBitmap(bitmap)
    }

    private fun saveComplete(save: Save) {
        val (bitmap: Bitmap, uri: Uri, name: String) = save
        saveNotification.complete(
            bitmap,
            name,
            requireContext().activityPendingIntent {
                ShareCompat.IntentBuilder(requireContext())
                    .setStream(uri)
                    .setType("image/*")
                    .setChooserTitle(R.string.share)
                    .createChooserIntent()
            },
            requireContext().activityPendingIntent {
                Intent(Intent.ACTION_VIEW)
                    .setDataAndTypeAndNormalize(uri, "image/*")
                    .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        )
    }

    private fun FragmentMainBinding.showProgress() {
        isOnProgress = true
        mainFab.setImageResource(R.drawable.ic_dot) // TODO: use AVD here!
        mainFab.isEnabled = false
        mainProgress.show()
    }

    private fun FragmentMainBinding.hideProgress() {
        isOnProgress = false
        mainFab.setImageResource(R.drawable.ic_save)
        mainFab.isEnabled = true
        mainProgress.hide()
    }

    private fun FragmentMainBinding.startingPipette(srcColor: Int) {
        isOnPipette = true
        mainImage.startPipette(srcColor)
        mainFab.setImageResource(R.drawable.ic_pipette_done)
    }

    private fun FragmentMainBinding.stopPipette(isCancel: Boolean = false) {
        if (isCancel) {
            mainImage.stopPipette()
            mainFab.setImageResource(R.drawable.ic_save)
        } else {
            mainImage.stopPipette(viewModel::backgroundColorPipette)
        }
        isOnPipette = false
    }

    private fun FragmentMainBinding.bottomMenuClick(item: MenuItem): Boolean = when {
        isOnProgress -> false.also {
            showSnackBar(
                view = requireView(),
                resId = R.string.on_progress,
                anchorViewId = R.id.mainFab
            )
        }
        isOnPipette -> false.also {
            showSnackBar(
                view = requireView(),
                resId = R.string.on_pipette,
                anchorViewId = R.id.mainFab,
                action = {
                    setAction(R.string.cancel) { stopPipette(isCancel = true) }
                }
            )
        }
        else -> {
            when (item.itemId) {
                R.id.action_background -> actionMainToToolsBackground(ratioCrop)
                R.id.action_badge -> actionMainToToolsBadge()
                R.id.action_screen -> actionMainToToolsScreen()
                R.id.action_template -> actionMainToToolsTemplate()
                else -> null
            }?.let { findNavController().navigate(it) } != null
        }
    }

    private fun handleReceiver(): Boolean = with(args) {
        val rawUri = path ?: return false
        return when (kind) {
            R.string.background -> true.also { viewModel.changeBackground(rawUri) }
            R.string.screen -> true.also { viewModel.changeScreen1(rawUri) }
            else -> false
        }
    }
}
