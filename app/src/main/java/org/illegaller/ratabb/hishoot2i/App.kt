package org.illegaller.ratabb.hishoot2i

import android.annotation.SuppressLint
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

@SuppressLint("Registered") //
open class App : DaggerApplication() {
    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) return
        installLeakCanary()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder()
            .application(application = this)
            .build()
    }

    protected open fun installLeakCanary() { // no-op
    }
}