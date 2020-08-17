package org.illegaller.ratabb.hishoot2i.ui.main

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import common.ext.activityPendingIntent
import common.ext.addToGallery
import common.ext.preventMultipleClick
import common.ext.toActionViewImage
import dagger.hilt.android.AndroidEntryPoint
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.SingleActivity
import org.illegaller.ratabb.hishoot2i.ui.common.widget.CoreImagePreview
import org.illegaller.ratabb.hishoot2i.ui.tools.BaseTools
import org.illegaller.ratabb.hishoot2i.ui.tools.background.BackgroundTool
import org.illegaller.ratabb.hishoot2i.ui.tools.badge.BadgeTool
import org.illegaller.ratabb.hishoot2i.ui.tools.screen.ScreenTool
import org.illegaller.ratabb.hishoot2i.ui.tools.template.TemplateTool
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main), MainView, BaseTools.ChangeImageSourcePath {

    @Inject
    lateinit var presenter: MainPresenter

    @Inject
    lateinit var saveNotification: SaveNotification

    //
    private lateinit var mainFab: FloatingActionButton
    private lateinit var mainBottomAppBar: BottomAppBar
    private lateinit var mainImage: CoreImagePreview

    //
    private var ratioCropX: Int = 0
    private var ratioCropY: Int = 0
    private var isOnPipette: Boolean = false
    private var isOnProgress: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(view) {
            mainFab = findViewById(R.id.mainFab)
            mainBottomAppBar = findViewById(R.id.mainBottomAppBar)
            mainImage = findViewById(R.id.mainImage)
        }
        presenter.attachView(this)
        setViewListener()

        handleSaveState(savedInstanceState)
        if (!handleReceiver()) presenter.onPreview()
        setUpResultListener()
    }

    override fun onDestroyView() {
        presenter.detachView()
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        presenter.resume()
    }

    private fun handleSaveState(state: Bundle?) {
        // NOTE: Do not using changePath... here!
        // set path if not null
        presenter.sourcePath.apply {
            state?.getString(KEY_BACKGROUND_PATH)?.let { background = it }
            state?.getString(KEY_SCREEN1_PATH)?.let { screen1 = it }
            state?.getString(KEY_SCREEN2_PATH)?.let { screen2 = it }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        with(outState) {
            // save path if not null
            presenter.sourcePath.background?.let { putString(KEY_BACKGROUND_PATH, it) }
            presenter.sourcePath.screen1?.let { putString(KEY_SCREEN1_PATH, it) }
            presenter.sourcePath.screen2?.let { putString(KEY_SCREEN2_PATH, it) }
        }
    }

    override fun preview(bitmap: Bitmap) {
        ratioCropX = bitmap.width
        ratioCropY = bitmap.height
        mainImage.setImageBitmap(bitmap)
    }

    override fun save(bitmap: Bitmap, uri: Uri, name: String) {
        with(requireActivity()) {
            saveNotification.complete(
                bitmap,
                name,
                activityPendingIntent {
                    ShareCompat.IntentBuilder.from(this)
                        .setStream(uri)
                        .setType("image/*")
                        .setChooserTitle(R.string.share)
                        .createChooserIntent()
                },
                activityPendingIntent { uri.toActionViewImage() }
            )
            addToGallery(uri)
        }
    }

    override fun startSave() {
        saveNotification.start()
    }

    override fun errorSave(e: Throwable) {
        saveNotification.error(e)
    }

    override fun showProgress() {
        // TODO: UI progress
        isOnProgress = true
        mainFab.setImageResource(R.drawable.ic_dot_white_24dp)
        mainFab.isEnabled = false
    }

    override fun hideProgress() {
        // TODO: UI progress
        isOnProgress = false
        mainFab.setImageResource(R.drawable.ic_save_black_24dp)
        mainFab.isEnabled = true
    }

    override fun startingPipette(srcColor: Int) {
        isOnPipette = true
        mainImage.startPipette(srcColor)
        mainFab.setImageResource(R.drawable.ic_pipette_done_black_24dp)
    }

    override fun changePathScreen1(path: String) {
        presenter.changeScreen1(path)
    }

    override fun changePathScreen2(path: String) {
        presenter.changeScreen2(path)
    }

    override fun changePathBackground(path: String) {
        presenter.changeBackground(path)
    }

    override fun onError(e: Throwable) {
        Timber.e(e)
        // TODO: ??
    }

    //////
    private fun setUpResultListener() {
        setFragmentResultListener(KEY_REQ_CROP) { requestKey, result ->
            val path = result.getString(KEY_CROP_PATH)
            if (requestKey == KEY_REQ_CROP && path != null) changePathBackground(path)
        }
    }

    private fun setViewListener() {
        mainFab.setOnClickListener {
            it.preventMultipleClick {
                if (!isOnPipette) presenter.onSave() else stopPipette()
            }
        }
        mainBottomAppBar.apply {
            setOnMenuItemClickListener(::mainBottomAppBarMenuClick)
            setNavigationOnClickListener {
                it.preventMultipleClick {
                    (activity as? SingleActivity)?.openDrawer()
                }
            }
        }
    }

    private fun stopPipette(isCancel: Boolean = false) {
        if (isCancel) {
            mainImage.stopPipette()
            mainFab.setImageResource(R.drawable.ic_save_black_24dp)
        } else {
            // NOTE: presenter#setBackgroundColorFromPipette -> hide/show progress
            mainImage.stopPipette(presenter::setBackgroundColorFromPipette)
        }
        isOnPipette = false
    }

    private fun mainBottomAppBarMenuClick(item: MenuItem): Boolean = item.preventMultipleClick {
        return when {
            isOnProgress -> false.also { makeSnackBar("Progress").show() }
            isOnPipette -> false.also {
                makeSnackBar("Pipette")
                    .setAction(R.string.cancel) { stopPipette(isCancel = true) }
                    .show()
            }
            else -> {
                when (item.itemId) {
                    R.id.action_template -> TemplateTool()
                    R.id.action_screen -> ScreenTool()
                    R.id.action_background -> BackgroundTool.newInstance(ratioCropX, ratioCropY)
                    /* NOTE: [BadgeTool] permission READ_EXTERNAL_STORAGE Font file directory. */
                    R.id.action_badge -> BadgeTool()
                    /* R.id.action_filter -> null.also { makeSnackBar("Coming soon 7o7").show() }*/
                    else -> null
                }?.let {
                    it.callback = this
                    it.show(childFragmentManager)
                    true
                } ?: false
            }
        }
    }

    private fun makeSnackBar(message: String) =
        Snackbar.make(mainBottomAppBar, message, Snackbar.LENGTH_SHORT)
            .setAnchorView(mainFab)

    private val args: MainFragmentArgs by navArgs()
    private fun handleReceiver(): Boolean {
        // Timber.d("args= $args")
        val (path, kind) = args
        return when (kind) {
            R.string.background -> path?.let {
                changePathBackground(it)
                true
            }
            R.string.screen -> path?.let {
                changePathScreen1(it)
                true
            }
            else -> false
        } ?: false
    }

    companion object {
        private const val KEY_BACKGROUND_PATH = "_background_path"
        private const val KEY_SCREEN1_PATH = "_screen1_path"
        private const val KEY_SCREEN2_PATH = "_screen2_path"

        const val KEY_REQ_CROP = "_crop_request"
        const val KEY_CROP_PATH = "_crop_path"
    }
}
