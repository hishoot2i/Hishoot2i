package org.illegaller.ratabb.hishoot2i.data

import android.os.Bundle
import android.support.annotation.IntRange
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.mergeDelayError
import io.reactivex.rxkotlin.toFlowable
import rbb.hishoot2i.common.FileConstants
import rbb.hishoot2i.template.Template
import rbb.hishoot2i.template.TemplateConstants.CATEGORY_TEMPLATE_APK
import rbb.hishoot2i.template.TemplateConstants.META_DATA_TEMPLATE
import rbb.hishoot2i.template.TemplateFactoryManager
import javax.inject.Inject

class TemplateDataSourceImpl @Inject constructor(
    packageResolver: PackageResolver,
    templateFactoryManager: TemplateFactoryManager,
    fileConstants: FileConstants
) : TemplateDataSource,
    TemplateFactoryManager by templateFactoryManager,
    FileConstants by fileConstants,
    PackageResolver by packageResolver {
    /**/
    override fun allTemplate(): Flowable<Template> = arrayOf(
        Flowable.fromCallable(::default),
        provideTemplateLegacy(::version1),
        provideTemplateVersion(version = 2, factory = ::version2),
        provideTemplateVersion(version = 3, factory = ::version3),
        provideTemplateHtz(::versionHtz)
    )
        .asIterable()
        .mergeDelayError()
        .filter { it.isNotEmpty }

    override fun findById(id: String): Single<Template> = allTemplate()
        .filter { it.id == id }
        .first(default())

    private fun provideTemplateLegacy(factory: (String, Long) -> Template): Flowable<Template> =
        queryIntentActivities(CATEGORY_TEMPLATE_APK)
            .map {
                getPackageInfo(it.activityInfo.packageName).let {
                    factory(it.packageName, it.firstInstallTime)
                }
            }
            .onErrorReturnItem(Template.Empty) //

    private fun provideTemplateVersion(
        @IntRange(from = 2, to = 3) version: Int,
        factory: (String, Long) -> Template
    ): Flowable<Template> = installedApplications()
        .filter { it.metaData.machVersion(version) }
        .map {
            getPackageInfo(it.packageName).let {
                factory(it.packageName, it.firstInstallTime)
            }
        }
        .onErrorReturnItem(Template.Empty) //

    private fun provideTemplateHtz(factory: (String, Long) -> Template): Flowable<Template> =
        Flowable.fromCallable { htzDir() }
            .flatMap { it.listFiles().toFlowable() }
            .filter { it.canRead() && it.isDirectory }
            .map { factory(it.name, it.lastModified()) }
            .onErrorReturnItem(Template.Empty) //

    private fun Bundle?.machVersion(version: Int): Boolean = this?.let {
        return@let containsKey(META_DATA_TEMPLATE) && getInt(META_DATA_TEMPLATE) == version
    } ?: false
}