/*
package org.illegaller.ratabb.hishoot2i

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager

internal class WatcherFragmentOnDestroy(
    private val watcher: (Fragment?) -> Unit
) : ActivityLifecycleCallbacksAdapter() {
    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        (activity as? FragmentActivity)?.supportFragmentManager
            ?.registerFragmentLifecycleCallbacks(
                object : FragmentManager.FragmentLifecycleCallbacks() {
                    override fun onFragmentDestroyed(fm: FragmentManager?, f: Fragment?) {
                        watcher(f)
                        super.onFragmentDestroyed(fm, f)
                    }
                },
                true
            )
    }
}*/
