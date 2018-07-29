package org.illegaller.ratabb.hishoot2i.data

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import rbb.hishoot2i.common.ext.actionMainWith
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

class PackageResolverImpl @Inject constructor(context: Context) : PackageResolver {
    private val packageManager by lazy(NONE) { context.packageManager }
    override fun queryIntentActivities(category: String): Flowable<ResolveInfo> =
        packageManager.queryIntentActivities(actionMainWith(category), 0)
            .toFlowable()

    override fun installedApplications(): Flowable<ApplicationInfo> =
        packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .toFlowable()

    override fun getPackageInfo(packageName: String): PackageInfo =
        packageManager.getPackageInfo(packageName, 0)
}