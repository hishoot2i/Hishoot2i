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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import common.ext.activityPendingIntent
import common.ext.graphics.sizes
import common.ext.preventMultipleClick
import dagger.hilt.android.AndroidEntryPoint
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
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main), MainView {
    @Inject
    lateinit var presenter: MainPresenter

    @Inject
    lateinit var saveNotification: SaveNotification

    private var mainBinding: FragmentMainBinding? = null

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
        val binding = FragmentMainBinding.bind(view)
        mainBinding = binding
        binding.apply {
            mainFab.setOnClickListener {
                it.preventMultipleClick { if (!isOnPipette) presenter.save() else stopPipette() }
            }
            mainBottomAppBar.apply {
                setOnMenuItemClickListener {
                    it.preventMultipleClick {
                        bottomMenuClick(it)
                    }
                }
                setNavigationOnClickListener {
                    it.preventMultipleClick {
                        if (isOnPipette) stopPipette(true)
                        (activity as? HiShootActivity)?.openDrawer()
                    }
                }
            }
        }
        presenter.attachView(this)
        handleSaveState(savedInstanceState)
        if (!handleReceiver()) presenter.render()
        setFragmentResultListeners(*requestKeys) { requestKey, result ->
            when (requestKey) {
                KEY_REQ_CROP ->
                    result.getString(ARG_CROP_PATH)
                        ?.let { presenter.changeBackground(it) }
                KEY_REQ_BACKGROUND ->
                    result.getString(ARG_BACKGROUND_PATH)
                        ?.let { presenter.changeBackground(it) }
                KEY_REQ_SCREEN_1 ->
                    result.getString(ARG_SCREEN1_PATH)
                        ?.let { presenter.changeScreen1(it) }
                KEY_REQ_SCREEN_2 ->
                    result.getString(ARG_SCREEN2_PATH)
                        ?.let { presenter.changeScreen2(it) }
                KEY_REQ_PIPETTE -> startingPipette(result.getInt(ARG_PIPETTE_COLOR))
            }
        }
    }

    override fun onDestroyView() {
        mainBinding = null
        clearFragmentResultListeners(*requestKeys)
        presenter.detachView()
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        presenter.resume()
    }

    private fun handleSaveState(state: Bundle?) {
        presenter.sourcePath.apply {
            state?.getString(ARG_BACKGROUND_PATH)?.let { background = it }
            state?.getString(ARG_SCREEN1_PATH)?.let { screen1 = it }
            state?.getString(ARG_SCREEN2_PATH)?.let { screen2 = it }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        with(outState) {
            presenter.sourcePath.background?.let { putString(ARG_BACKGROUND_PATH, it) }
            presenter.sourcePath.screen1?.let { putString(ARG_SCREEN1_PATH, it) }
            presenter.sourcePath.screen2?.let { putString(ARG_SCREEN2_PATH, it) }
        }
    }

    override fun preview(bitmap: Bitmap) {
        ratioCrop = bitmap.sizes.run { Point(x, y) }
        mainBinding?.mainImage?.setImageBitmap(bitmap)
    }

    override fun save(bitmap: Bitmap, uri: Uri, name: String) {
        saveNotification.complete(
            bitmap,
            name,
            requireContext().activityPendingIntent {
                ShareCompat.IntentBuilder.from(requireActivity())
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

    override fun startSave() {
        saveNotification.start()
    }

    override fun errorSave(e: Throwable) {
        saveNotification.error(e)
    }

    override fun showProgress() {
        isOnProgress = true
        mainBinding?.apply {
            mainFab.setImageResource(R.drawable.ic_dot)
            mainFab.isEnabled = false
            mainProgress.show()
        }
    }

    override fun hideProgress() {
        isOnProgress = false
        mainBinding?.apply {
            mainFab.setImageResource(R.drawable.ic_save)
            mainFab.isEnabled = true
            mainProgress.hide()
        }
    }

    override fun onError(e: Throwable) {
        Toast.makeText(requireContext(), e.localizedMessage ?: "Oops", Toast.LENGTH_SHORT).show()
        Timber.e(e)
    }

    private fun startingPipette(srcColor: Int) {
        isOnPipette = true
        mainBinding?.apply {
            mainImage.startPipette(srcColor)
            mainFab.setImageResource(R.drawable.ic_pipette_done)
        }
    }

    private fun stopPipette(isCancel: Boolean = false) {
        mainBinding?.apply {
            if (isCancel) {
                mainImage.stopPipette()
                mainFab.setImageResource(R.drawable.ic_save)
            } else {
                // NOTE: presenter#setBackgroundColorFromPipette -> hide/show progress
                mainImage.stopPipette(presenter::backgroundColorPipette)
            }
            isOnPipette = false
        }
    }

    private fun bottomMenuClick(item: MenuItem): Boolean = when {
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
                action = { setAction(R.string.cancel) { stopPipette(isCancel = true) } }
            )
        }
        else -> {
            val direction = when (item.itemId) {
                R.id.action_background ->
                    MainFragmentDirections.actionMainToToolsBackground(ratioCrop)
                R.id.action_badge -> MainFragmentDirections.actionMainToToolsBadge()
                R.id.action_screen -> MainFragmentDirections.actionMainToToolsScreen()
                R.id.action_template -> MainFragmentDirections.actionMainToToolsTemplate()
                else -> null
            }?.let { findNavController().navigate(it) }
            direction != null
        }
    }

    private fun handleReceiver(): Boolean = with(args) {
        val rawUri = path ?: return false
        return when (kind) {
            R.string.background -> true.also { presenter.changeBackground(rawUri) }
            R.string.screen -> true.also { presenter.changeScreen1(rawUri) }
            else -> false
        }
    }
}
