package org.illegaller.ratabb.hishoot2i

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import org.illegaller.ratabb.hishoot2i.data.pref.AppPref
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
open class App : Application() {
    @Inject
    lateinit var appPref: AppPref
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(appPref.dayNightMode)
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}
