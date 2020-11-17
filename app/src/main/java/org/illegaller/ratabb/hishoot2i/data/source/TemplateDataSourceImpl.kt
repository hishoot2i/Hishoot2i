package org.illegaller.ratabb.hishoot2i.data.source

import androidx.annotation.IntRange
import entity.AppInfo
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.mergeDelayError
import org.illegaller.ratabb.hishoot2i.data.HtzResolver
import org.illegaller.ratabb.hishoot2i.data.PackageResolver
import template.Template
import template.TemplateFactoryManager
import java.io.File
import javax.inject.Inject

class TemplateDataSourceImpl @Inject constructor(
    packageResolver: PackageResolver,
    htzResolver: HtzResolver,
    templateFactoryManager: TemplateFactoryManager
) : TemplateDataSource, TemplateFactoryManager by templateFactoryManager {

    override fun allTemplate(): Flowable<Template> = listOf(
        Flowable.fromCallable(::default),
        provideTemplateLegacy(::version1),
        provideTemplateVersion(version = 2, factory = ::version2),
        provideTemplateVersion(version = 3, factory = ::version3),
        provideTemplateHtz(::versionHtz)
    )
        .mergeDelayError()
        // ignore error
        .onErrorResumeNext { Flowable.empty() }

    override fun findById(id: String): Single<Template> = allTemplate()
        .filter { it.id == id }
        .first(default())

    private val installedTemplateLegacy: () -> Flowable<AppInfo> =
        (packageResolver::installedTemplateLegacy)

    private val installedTemplate: (Int) -> Flowable<AppInfo> =
        (packageResolver::installedTemplate)

    private val installedHtz: () -> Flowable<File> =
        (htzResolver::installedHtz)

    private inline fun provideTemplateLegacy(
        crossinline factory: (String, Long) -> Template
    ): Flowable<Template> = installedTemplateLegacy().map {
        factory(it.packageName, it.firstInstallTime)
    }

    private inline fun provideTemplateVersion(
        @IntRange(from = 2, to = 3) version: Int,
        crossinline factory: (String, Long) -> Template
    ): Flowable<Template> = installedTemplate(version).map {
        factory(it.packageName, it.firstInstallTime)
    }

    private inline fun provideTemplateHtz(
        crossinline factory: (String, Long) -> Template
    ): Flowable<Template> = installedHtz().map {
        factory(it.name, it.lastModified())
    }
}
