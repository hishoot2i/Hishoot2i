package org.illegaller.ratabb.hishoot2i.data.resolver

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import entity.AppInfo
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

    private val getPackageInfo: (String, Int) -> PackageInfo =
        (packageManager::getPackageInfo)

    override fun installedTemplateLegacy(): List<AppInfo> =
        queryIntentActivities(Intent(Intent.ACTION_MAIN).addCategory(CATEGORY_TEMPLATE_APK), 0)
            .map { getPackageInfo(it.activityInfo.packageName, 0) }
            .map { AppInfo(it.packageName, it.firstInstallTime) }

    override fun installedTemplate(version: Int): List<AppInfo> =
        getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { it.matchVersion(version) }
            .map { getPackageInfo(it.packageName, 0) }
            .map { AppInfo(it.packageName, it.firstInstallTime) }

    private fun ApplicationInfo.matchVersion(version: Int): Boolean =
        metaData?.run { getInt(META_DATA_TEMPLATE) == version } == true
}
