package org.illegaller.ratabb.hishoot2i

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.GET_META_DATA
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import common.ext.activityPendingIntent
import dagger.hilt.android.AndroidEntryPoint
import org.illegaller.ratabb.hishoot2i.BuildConfig.IMAGE_RECEIVER
import timber.log.Timber

@AndroidEntryPoint
class SingleActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        navController = findNavController(R.id.navHostContainer)
        navController.setGraph(R.navigation.navigation, parseImageReceiver())
        navigationView.setupWithNavController(navController)
        setupAppVersion()
        // TODO: permission
        getPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun onSupportNavigateUp(): Boolean = navController.navigateUp(drawerLayout)

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else
            super.onBackPressed()
    }

    // TODO: permission
    private val getPermission = registerForActivityResult(RequestPermission()) { granted ->
        Timber.d("permission is granted: $granted")
    }

    internal fun openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun setupAppVersion() {
        navigationView.getHeaderView(0).findViewById<TextView>(R.id.navAppVersion)?.apply {
            text = getString(
                R.string.version_format,
                packageManager.getPackageInfo(packageName, 0).versionName //
            )
        }
    }

    /**
     * @see  `AndroidManifest.xml`
     * <activity-alias> .ScreenReceiver and .BackgroundReceiver
     * NOTE: <meta-data ...  android:resource />
     * returned Resources ID (Int), *not* a Resources value (String)
     **/
    private fun parseImageReceiver(): Bundle? {
        if (intent.action != Intent.ACTION_SEND) return null //
        val uri: Uri = intent.getParcelableExtra(Intent.EXTRA_STREAM) ?: return null //
        val kind = intent.component?.let {
            packageManager.getActivityInfo(it, GET_META_DATA)
        }?.metaData?.getInt(IMAGE_RECEIVER) ?: return null //
        return bundleOf("path" to uri.toString(), "kind" to kind)
    }

    companion object {

        @JvmStatic
        fun contentIntent(context: Context): PendingIntent =
            context.activityPendingIntent { Intent(context, SingleActivity::class.java) }
    }
}
