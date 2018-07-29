package org.illegaller.ratabb.hishoot2i.data

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.ResolveInfo
import io.reactivex.Flowable

interface PackageResolver {
    fun queryIntentActivities(category: String): Flowable<ResolveInfo>
    fun installedApplications(): Flowable<ApplicationInfo>
    fun getPackageInfo(packageName: String): PackageInfo
}
