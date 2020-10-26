package org.illegaller.ratabb.hishoot2i

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.GET_META_DATA
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import common.ext.activityPendingIntent
import common.ext.preventMultipleClick
import dagger.hilt.android.AndroidEntryPoint
import org.illegaller.ratabb.hishoot2i.BuildConfig.IMAGE_RECEIVER
import org.illegaller.ratabb.hishoot2i.BuildConfig.VERSION_NAME
import org.illegaller.ratabb.hishoot2i.databinding.ActivityHishootBinding
import org.illegaller.ratabb.hishoot2i.databinding.HeaderNavLayoutBinding
import org.illegaller.ratabb.hishoot2i.ui.common.registerRequestPermissionLazy
import org.illegaller.ratabb.hishoot2i.ui.common.setSystemUiFlagEdgeToEdge
import timber.log.Timber

@AndroidEntryPoint
class HiShootActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private lateinit var binding: ActivityHishootBinding

    private val getPermission by registerRequestPermissionLazy { granted ->
        // TODO: handle result from request permission
        Timber.d("Permission is granted: $granted")
    }

    private val versionName by lazy { getString(R.string.version_format, VERSION_NAME) }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_DayNight)
        super.onCreate(savedInstanceState)
        binding = ActivityHishootBinding.inflate(layoutInflater)
        binding.apply {
            setContentView(root)
            setSystemUiFlagEdgeToEdge(root, true) //
            navController = findNavController(R.id.navHostContainer)
            navController.setGraph(R.navigation.navigation, parseImageReceiver())

            navigationView.apply {
                setupWithNavController(navController)
                HeaderNavLayoutBinding.bind(getHeaderView(0)).apply {
                    navAppVersion.text = versionName
                    navWeb.setOnClickListener {
                        it.preventMultipleClick {
                            openUrl("https://hishoot2i.github.io")
                        }
                    }
                    navSources.setOnClickListener {
                        it.preventMultipleClick {
                            openUrl("https://www.github.com/hishoot2i/hishoot2i")
                        }
                    }
                }
            }
        }
        getPermission.launch(WRITE_EXTERNAL_STORAGE)
        AppCenter.start(Analytics::class.java, Crashes::class.java)
    }

    override fun onSupportNavigateUp(): Boolean = navController.navigateUp(binding.drawerLayout)

    override fun onBackPressed() = if (binding.drawerLayout.isOpen) binding.drawerLayout.close()
    else super.onBackPressed()

    internal fun openDrawer() {
        binding.drawerLayout.open()
    }

    private fun openUrl(url: String) {
        CustomTabsIntent.Builder()
            .build()
            .launchUrl(this, url.toUri())
    }

    /**
     * AndroidManifest.xml
     * `<activity-alias>` .ScreenReceiver and .BackgroundReceiver
     * NOTE: `<meta-data  android:resource/>`
     * returned Resources ID (Int), *not* a Resources value (String)
     **/
    private fun parseImageReceiver(): Bundle? {
        if (intent.action == Intent.ACTION_SEND) {
            // raw Uri
            val path: String? = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)?.toString()
            val kind: Int = intent.component?.let {
                packageManager.getActivityInfo(it, GET_META_DATA)
            }?.metaData?.getInt(IMAGE_RECEIVER, -1) ?: -1
            if (path != null && kind != -1) {
                return bundleOf("path" to path, "kind" to kind)
            }
        }
        return null
    }

    companion object {

        @JvmStatic
        fun contentIntent(context: Context): PendingIntent =
            context.activityPendingIntent { Intent(context, HiShootActivity::class.java) }
    }
}
