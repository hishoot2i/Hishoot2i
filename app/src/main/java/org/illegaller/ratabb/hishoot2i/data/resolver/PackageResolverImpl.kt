package org.illegaller.ratabb.hishoot2i.data.resolver

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import entity.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import template.TemplateConstants
import javax.inject.Inject

class PackageResolverImpl @Inject constructor(
    packageManager: PackageManager
) : PackageResolver {
    private val queryIntentActivities: suspend (Intent, Int) -> List<ResolveInfo> = { intent, i ->
        withContext(Dispatchers.Default) { packageManager.queryIntentActivities(intent, i) }
    }

    private val getInstalledApplications: suspend (Int) -> List<ApplicationInfo> = { flag ->
        withContext(Dispatchers.Default) { packageManager.getInstalledApplications(flag) }
    }

    private val getPackageInfo: suspend (String) -> PackageInfo = { packageName ->
        withContext(Dispatchers.Default) { packageManager.getPackageInfo(packageName, 0) }
    }

    override suspend fun installedTemplateLegacy(): List<AppInfo> =
        withContext(Dispatchers.Default) {
            queryIntentActivities(
                Intent(Intent.ACTION_MAIN).addCategory(TemplateConstants.CATEGORY_TEMPLATE_APK),
                0
            )
                .map { getPackageInfo(it.activityInfo.packageName) }
                .map { AppInfo(it.packageName, it.firstInstallTime) }
        }

    override suspend fun installedTemplate(version: Int): List<AppInfo> =
        withContext(Dispatchers.Default) {
            getInstalledApplications(PackageManager.GET_META_DATA)
                .filter { it.matchVersion(version) }
                .map { getPackageInfo(it.packageName) }
                .map { AppInfo(it.packageName, it.firstInstallTime) }
        }

    private suspend fun ApplicationInfo.matchVersion(version: Int): Boolean = withContext(
        Dispatchers.Default
    ) {
        metaData?.run { getInt(TemplateConstants.META_DATA_TEMPLATE) == version } == true
    }
}
