package org.illegaller.ratabb.hishoot2i

import android.os.Build.VERSION.SDK_INT
import android.os.StrictMode

internal fun enableStrictMode() {
    val threadPolicy = StrictMode.ThreadPolicy.Builder()
        .detectAll()
        .permitDiskReads() // Pref ...

    val vmPolicy = StrictMode.VmPolicy.Builder()
        .detectAll()
        .setClassInstanceLimit(HiShootActivity::class.java, 1) //

    // RecyclerView.onLayout(RecyclerView.java:4577)
    if (SDK_INT >= 28) vmPolicy.permitNonSdkApiUsage()

    if (SDK_INT >= 29) vmPolicy.detectImplicitDirectBoot()

    // threadPolicy.detectExplicitGc()

    StrictMode.setThreadPolicy(threadPolicy.penaltyLog().build())
    StrictMode.setVmPolicy(vmPolicy.penaltyLog().build())
}
