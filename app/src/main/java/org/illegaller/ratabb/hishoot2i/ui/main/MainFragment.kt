package org.illegaller.ratabb.hishoot2i.ui.main

import android.graphics.Bitmap
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import common.graphics.sizes
import common.view.preventMultipleClick
import core.Preview
import core.Save
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.illegaller.ratabb.hishoot2i.HiShootActivity
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.data.pref.SettingPref
import org.illegaller.ratabb.hishoot2i.databinding.FragmentMainBinding
import org.illegaller.ratabb.hishoot2i.ui.ARG_BACKGROUND_PATH
import org.illegaller.ratabb.hishoot2i.ui.ARG_CROP_PATH
import org.illegaller.ratabb.hishoot2i.ui.ARG_PIPETTE_COLOR
import org.illegaller.ratabb.hishoot2i.ui.ARG_SCREEN1_PATH
import org.illegaller.ratabb.hishoot2i.ui.ARG_SCREEN2_PATH
import org.illegaller.ratabb.hishoot2i.ui.KEY_REQ_BACKGROUND
import org.illegaller.ratabb.hishoot2i.ui.KEY_REQ_CROP
import org.illegaller.ratabb.hishoot2i.ui.KEY_REQ_PIPETTE
import org.illegaller.ratabb.hishoot2i.ui.KEY_REQ_SAVE
import org.illegaller.ratabb.hishoot2i.ui.KEY_REQ_SCREEN_1
import org.illegaller.ratabb.hishoot2i.ui.KEY_REQ_SCREEN_2
import org.illegaller.ratabb.hishoot2i.ui.common.clearFragmentResultListeners
import org.illegaller.ratabb.hishoot2i.ui.common.setFragmentResultListeners
import org.illegaller.ratabb.hishoot2i.ui.common.showSnackBar
import org.illegaller.ratabb.hishoot2i.ui.common.viewObserve
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {
    @Inject
    lateinit var saveNotification: SaveNotification

    @Inject
    lateinit var settingPref: SettingPref

    private val viewModel: MainViewModel by viewModels()

    private val requestKeys = arrayOf(
        KEY_REQ_CROP, KEY_REQ_BACKGROUND,
        KEY_REQ_SCREEN_1, KEY_REQ_SCREEN_2,
        KEY_REQ_PIPETTE, KEY_REQ_SAVE
    )
    private val args: MainFragmentArgs by navArgs()
    private var ratioCrop = Point()
    private var isOnPipette: Boolean = false
    private var isOnProgress: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentMainBinding.bind(view).apply {
            setViewListener()
            viewObserve(viewModel.uiState) { observer(it) }
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

    private fun FragmentMainBinding.handleResult(
        requestKey: String,
        result: Bundle
    ): Unit = when (requestKey) {
        KEY_REQ_CROP -> viewModel.changeBackground(result.getString(ARG_CROP_PATH))
        KEY_REQ_BACKGROUND -> viewModel.changeBackground(result.getString(ARG_BACKGROUND_PATH))
        KEY_REQ_SCREEN_1 -> viewModel.changeScreen1(result.getString(ARG_SCREEN1_PATH))
        KEY_REQ_SCREEN_2 -> viewModel.changeScreen2(result.getString(ARG_SCREEN2_PATH))
        KEY_REQ_PIPETTE -> startingPipette(result.getInt(ARG_PIPETTE_COLOR))
        KEY_REQ_SAVE -> viewModel.save()
        else -> {
        }
    }

    private fun FragmentMainBinding.observer(view: MainView): Unit = when (view) {
        is Loading -> {
            if (view.isFromSave) saveNotification.start()
            showProgress()
        }
        is Fail -> {
            hideProgress()
            if (view.isFromSave) saveNotification.error(view.cause)
            val msg = view.cause.localizedMessage ?: "Oops"
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            Timber.e(view.cause)
        }
        is Success -> {
            hideProgress()
            when (view.result) {
                is Preview -> {
                    ratioCrop = view.result.bitmap.sizes.run { Point(x, y) }
                    mainImage.setImageBitmap(view.result.bitmap)
                }
                is Save -> {
                    val (bitmap: Bitmap, uri: Uri, name: String) = view.result
                    saveNotification.complete(bitmap, name, uri)
                }
            }
        }
    }

    private fun FragmentMainBinding.setViewListener() {
        mainFab.setOnClickListener {
            it.preventMultipleClick {
                if (isOnPipette) stopPipette()
                else {
                    if (settingPref.saveConfirmEnable) {
                        findNavController().navigate(R.id.action_main_to_saveConfirm)
                    } else viewModel.save()
                }
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

    private fun FragmentMainBinding.showProgress() {
        isOnProgress = true
        val avd = AnimatedVectorDrawableCompat.create(
            requireContext(),
            R.drawable.avd_hourglass_24
        )
        mainFab.setImageDrawable(avd)
        avd?.start()
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
                view = root,
                resId = R.string.on_progress,
                anchorViewId = R.id.mainFab
            )
        }
        isOnPipette -> false.also {
            showSnackBar(
                view = root,
                resId = R.string.on_pipette,
                anchorViewId = R.id.mainFab,
                action = {
                    setAction(R.string.cancel) { stopPipette(isCancel = true) }
                }
            )
        }
        else -> {
            when (item.itemId) {
                R.id.action_background -> {
                    MainFragmentDirections.actionMainToToolsBackground(ratioCrop)
                }
                R.id.action_badge -> MainFragmentDirections.actionMainToToolsBadge()
                R.id.action_screen -> MainFragmentDirections.actionMainToToolsScreen()
                R.id.action_template -> MainFragmentDirections.actionMainToToolsTemplate()
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
