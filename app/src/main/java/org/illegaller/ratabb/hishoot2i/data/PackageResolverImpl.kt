package org.illegaller.ratabb.hishoot2i.data

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import common.ext.actionMainWith
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import template.TemplateConstants.CATEGORY_TEMPLATE_APK
import template.TemplateConstants.META_DATA_TEMPLATE
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

class PackageResolverImpl @Inject constructor(context: Context) : PackageResolver {
    private val packageManager by lazy(NONE) { context.applicationContext.packageManager }
    override fun installedTemplateLegacy(): Flowable<entity.AppInfo> =
        packageManager.queryIntentActivities(actionMainWith(CATEGORY_TEMPLATE_APK), 0)
            .toFlowable()
            .map { it.activityInfo.packageName }
            .flatMap(::mapTo)

    override fun installedTemplate(version: Int): Flowable<entity.AppInfo> =
        packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .toFlowable()
            .filter { it.metaData.machVersion(version) }
            .map { it.packageName }
            .flatMap(::mapTo)

    private fun mapTo(packageName: String): Flowable<entity.AppInfo> =
        Flowable.fromCallable { packageManager.getPackageInfo(packageName, 0) }
            .map { entity.AppInfo(packageName, it.firstInstallTime) }

    companion object {
        @JvmStatic private fun Bundle?.machVersion(version: Int): Boolean = this?.let {
            containsKey(META_DATA_TEMPLATE) && getInt(META_DATA_TEMPLATE) == version
        } == true
    }
}