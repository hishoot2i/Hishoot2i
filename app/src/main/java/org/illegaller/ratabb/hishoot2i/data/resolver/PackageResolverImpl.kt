package org.illegaller.ratabb.hishoot2i.data.resolver

import android.content.Intent
import android.content.Intent.ACTION_MAIN
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_META_DATA
import template.TemplateConstants.CATEGORY_TEMPLATE_APK
import template.TemplateConstants.META_DATA_TEMPLATE
import javax.inject.Inject

class PackageResolverImpl @Inject constructor(
    private val manager: PackageManager
) : PackageResolver {
    private companion object {
        val TEMPLATE_V1 = Intent(ACTION_MAIN).addCategory(CATEGORY_TEMPLATE_APK)
    }

    override fun installedTemplateLegacy() = manager.queryIntentActivities(TEMPLATE_V1, 0)
        .map { manager.getPackageInfo(it.activityInfo.packageName, 0) }
        .map { it.packageName to it.firstInstallTime }

    override fun installedTemplate(version: Int) = manager.getInstalledApplications(GET_META_DATA)
        .filter { it.metaData?.run { getInt(META_DATA_TEMPLATE) == version } == true }
        .map { manager.getPackageInfo(it.packageName, 0) }
        .map { it.packageName to it.firstInstallTime }
}
