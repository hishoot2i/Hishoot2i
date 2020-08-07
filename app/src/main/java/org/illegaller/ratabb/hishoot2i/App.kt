package org.illegaller.ratabb.hishoot2i

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
open class App : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}