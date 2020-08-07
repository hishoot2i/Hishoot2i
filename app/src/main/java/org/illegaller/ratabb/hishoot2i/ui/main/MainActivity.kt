package org.illegaller.ratabb.hishoot2i.ui.main

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.GET_META_DATA
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.ColorInt
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ShareCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import common.ext.activityPendingIntent
import common.ext.addToGallery
import common.ext.graphics.createVectorDrawableTint
import common.ext.graphics.sizes
import common.ext.isVisible
import common.ext.preventMultipleClick
import common.ext.toActionViewImage
import common.ext.toFile
import dagger.hilt.android.AndroidEntryPoint
import org.illegaller.ratabb.hishoot2i.BuildConfig.IMAGE_RECEIVER
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.ui.common.BaseActivity
import org.illegaller.ratabb.hishoot2i.ui.common.widget.CoreImagePreview
import org.illegaller.ratabb.hishoot2i.ui.main.tools.AbsTools
import org.illegaller.ratabb.hishoot2i.ui.main.tools.background.BackgroundTool
import org.illegaller.ratabb.hishoot2i.ui.main.tools.badge.BadgeTool
import org.illegaller.ratabb.hishoot2i.ui.main.tools.screen.ScreenTool
import org.illegaller.ratabb.hishoot2i.ui.main.tools.template.TemplateTool
import org.illegaller.ratabb.hishoot2i.ui.setting.SettingActivity
import timber.log.Timber
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

@AndroidEntryPoint
class MainActivity : BaseActivity(), MainView, AbsTools.ChangeImageSourcePath {
    @Inject
    lateinit var presenter: MainPresenter

    @Inject
    lateinit var saveNotification: SaveNotification

    //
    //private val permissionReq by lazy(NONE) { permissionsBuilder(WRITE_EXTERNAL_STORAGE).build() }
    private val saveDrawable by lazy(NONE) {
        createVectorDrawableTint(R.drawable.ic_save_black_24dp, R.color.white)
    }
    private val pipetteDrawable: Drawable? by lazy(NONE) {
        createVectorDrawableTint(R.drawable.ic_pipette_done_black_24dp, R.color.white)
    }

    //
    private lateinit var toolbar: Toolbar
    private lateinit var mainFab: FloatingActionButton
    private lateinit var mainBottomNav: BottomNavigationView
    private lateinit var mainImage: CoreImagePreview
    private lateinit var loading: View

    //
    private var ratioCrop: Point? = null
    private var isOnPipette: Boolean = false
    private var isOnProgress: Boolean = false

    /**/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.mainToolbar)
        mainFab = findViewById(R.id.mainFab)

        mainBottomNav = findViewById(R.id.mainBottomNav)
        mainImage = findViewById(R.id.mainImage)
        loading = findViewById(R.id.loading)
        setSupportActionBar(toolbar)
        presenter.attachView(this)
        /* TODO: handle this! */
//        permissionReq
//            .onAccepted { Timber.d("Permission accepted") }
//            .onDenied { Timber.d("Permission denied") }
//            .onPermanentlyDenied { Timber.d("Permission permanently denied") }
//            .onShouldShowRationale { _, _ ->
//                Timber.d("Permission should show rationale")
//            }

        setViewListener()
        // NOTE: Not use changePath...
        savedInstanceState?.let {
            if (it.containsKey(KEY_BACKGROUND_PATH)) it.getString(KEY_BACKGROUND_PATH)?.let {
                presenter.sourcePath.background = it
            }
            if (it.containsKey(KEY_SCREEN1_PATH)) it.getString(KEY_SCREEN1_PATH)?.let {
                presenter.sourcePath.screen1 = it
            }
            if (it.containsKey(KEY_SCREEN2_PATH)) it.getString(KEY_SCREEN2_PATH)?.let {
                presenter.sourcePath.screen2 = it
            }
        }
        //
        if (!handleImageReceiver()) presenter.onPreview()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        with(outState) {
            putString(KEY_BACKGROUND_PATH, presenter.sourcePath.background)
            putString(KEY_SCREEN1_PATH, presenter.sourcePath.screen1)
            putString(KEY_SCREEN2_PATH, presenter.sourcePath.screen2)
        }
    }

    override fun onResume() {
        super.onResume()
        //permissionReq.send() //
        presenter.resume()
    }

    override fun onDestroy() {
        //permissionReq.detachAllListeners()
        presenter.detachView()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_setting -> true.also { SettingActivity.start(this) }
        else -> super.onOptionsItemSelected(item)
    }

    override fun preview(bitmap: Bitmap) {
        ratioCrop = bitmap.sizes.let { Point(it.x, it.y) }
        mainImage.setImageBitmap(bitmap)
    }

    override fun save(bitmap: Bitmap, uri: Uri) {
        saveNotification.complete(
            bitmap,
            uri.toFile(this)?.nameWithoutExtension ?: "unknown",
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

    override fun startSave() {
        saveNotification.start()
    }

    override fun errorSave(e: Throwable) {
        saveNotification.error(e)
        onError(e)
    }

    override fun showProgress() {
        loading.isVisible = true
        isOnProgress = true
        mainFab.setImageResource(R.drawable.ic_dot_white_24dp)
        mainFab.isEnabled = false
    }

    override fun hideProgress() {
        isOnProgress = false
        loading.isVisible = false
        mainFab.setImageDrawable(saveDrawable)
        mainFab.isEnabled = true
    }

    override fun startingPipette(@ColorInt srcColor: Int) {
        isOnPipette = true
        mainImage.startPipette(srcColor)
        mainFab.setImageDrawable(pipetteDrawable)
    }

    override fun onError(e: Throwable) {
        Timber.e(e)
        Snackbar.make(mainImage, e.localizedMessage ?: "", Snackbar.LENGTH_SHORT)
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

    private fun setViewListener() {
        mainFab.setOnClickListener {
            it.preventMultipleClick {
                if (!isOnPipette) {
                    presenter.onSave()
                } else stopPipette()
            }
        }
        mainBottomNav.setOnNavigationItemSelectedListener { navigationShowTool(it) }
    }

    private fun stopPipette(isCancel: Boolean = false) {
        if (isCancel) {
            mainImage.stopPipette()
            mainFab.setImageDrawable(saveDrawable)
        } else {
            // NOTE: presenter#setBackgroundColorFromPipette -> hide/show progress
            mainImage.stopPipette(presenter::setBackgroundColorFromPipette)
        }
        isOnPipette = false
    }

    private fun navigationShowTool(item: MenuItem): Boolean = item.preventMultipleClick {
        return when {
            isOnProgress -> false.also {
                Snackbar.make(mainBottomNav, "PROGRESS", /*TODO:text!*/ Snackbar.LENGTH_SHORT)
                    .show()
            }
            isOnPipette -> false.also { _ ->
                Snackbar.make(mainBottomNav, "PIPETTE", /*TODO: text!*/ Snackbar.LENGTH_SHORT)
                    .setAction(R.string.cancel) { stopPipette(isCancel = true) }
                    .show()
            }
            else -> {
                when (item.itemId) {
                    R.id.action_template -> TemplateTool()
                    R.id.action_screen -> ScreenTool()
                    R.id.action_background -> BackgroundTool.newInstance(ratioCrop)
                    /* NOTE: [BadgeTool] permission READ_EXTERNAL_STORAGE Font file directory. */
                    R.id.action_badge -> BadgeTool()
                    else -> null
                }?.let {
                    it.callback = this
                    it.show(supportFragmentManager)
                    true
                } ?: false
            }
        }
    }

    /**
     * @return "true" when we have image path (screen1 | background) from intent receiver,
     * also run changePath...
     */
    private fun handleImageReceiver(): Boolean {
        if (intent?.action != Intent.ACTION_SEND) return false
        val uri: Uri = intent.getParcelableExtra(Intent.EXTRA_STREAM) ?: return false
        Timber.d("uri:$uri author:${uri.authority}")
        val path = uri.toString() // FIXME: `uri.toString()` -> Let ImageLoader handle it?
        val info = intent.component?.let { packageManager.getActivityInfo(it, GET_META_DATA) }
        // NOTE: @see AndroidManifest.xml
        // NOTE: <meta-data ...  android:resource />
        // returned Resources ID (Int), *not* a Resources value (String)
        return when (info?.metaData?.getInt(IMAGE_RECEIVER)) {
            R.string.screen -> true.also { changePathScreen1(path) }
            R.string.background -> true.also { changePathBackground(path) }
            else -> false
        }
    }

    companion object {
        private const val KEY_BACKGROUND_PATH = "_background_path"
        private const val KEY_SCREEN1_PATH = "_screen1_path"
        private const val KEY_SCREEN2_PATH = "_screen2_path"

        @JvmStatic
        fun contentIntent(context: Context): PendingIntent =
            context.activityPendingIntent { Intent(context, MainActivity::class.java) }
    }
}