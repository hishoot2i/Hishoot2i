package org.illegaller.ratabb.hishoot2i

import android.os.StrictMode
import com.squareup.leakcanary.LeakCanary
import org.illegaller.ratabb.hishoot2i.ui.main.MainActivity
import timber.log.Timber
import java.util.concurrent.TimeUnit

@Suppress("unused")
class DebugApp : App() {
    override fun onCreate() {
        enableStrictMode()
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

    override fun installLeakCanary() {
        LeakCanary.refWatcher(this)
            .watchDelay(10L, TimeUnit.SECONDS)
            .buildAndInstall()
    }

    /* Having fun with StrictMode :lol: */
    private fun enableStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                // .permitDiskReads() // FIXME: SharedPreference [Editor] get/set Value?
                .penaltyLog()
                .build()
        )
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectAll()
                .setClassInstanceLimit(MainActivity::class.java, 1) //
                .penaltyLog()
                .build()
        )
    }
}