package org.illegaller.ratabb.hishoot2i.data

import android.content.Intent
import android.content.Intent.ACTION_MAIN
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_META_DATA
import android.content.pm.ResolveInfo
import entity.AppInfo
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import template.TemplateConstants.CATEGORY_TEMPLATE_APK
import template.TemplateConstants.META_DATA_TEMPLATE
import javax.inject.Inject

class PackageResolverImpl @Inject constructor(
    packageManager: PackageManager
) : PackageResolver {
    private val queryIntentActivities: (Intent, Int) -> List<ResolveInfo> =
        (packageManager::queryIntentActivities)

    private val getInstalledApplications: (Int) -> List<ApplicationInfo> =
        (packageManager::getInstalledApplications)

    private val getPackageInfo: (String) -> PackageInfo =
        { packageName: String -> packageManager.getPackageInfo(packageName, 0) }

    override fun installedTemplateLegacy(): Flowable<AppInfo> =
        queryIntentActivities(Intent(ACTION_MAIN).addCategory(CATEGORY_TEMPLATE_APK), 0)
            .toFlowable()
            .map { it.activityInfo.packageName }
            .flatMap { Flowable.fromCallable { getPackageInfo(it) } }
            .map { AppInfo(it.packageName, it.firstInstallTime) }

    override fun installedTemplate(version: Int): Flowable<AppInfo> =
        getInstalledApplications(GET_META_DATA)
            .toFlowable()
            .filter { it.matchVersion(version) }
            .map { it.packageName }
            .flatMap { Flowable.fromCallable { getPackageInfo(it) } }
            .map { AppInfo(it.packageName, it.firstInstallTime) }

    private fun ApplicationInfo.matchVersion(version: Int): Boolean = metaData?.run {
        containsKey(META_DATA_TEMPLATE) && getInt(META_DATA_TEMPLATE) == version
    } == true
}
