package org.illegaller.ratabb.hishoot2i.data.source

import androidx.annotation.IntRange
import common.FileConstants
import common.ext.isDirAndCanRead
import common.ext.listFilesOrEmpty
import entity.AppInfo
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.mergeDelayError
import io.reactivex.rxkotlin.toFlowable
import org.illegaller.ratabb.hishoot2i.data.PackageResolver
import template.Template
import template.TemplateFactoryManager
import java.io.File
import javax.inject.Inject

class TemplateDataSourceImpl @Inject constructor(
    packageResolver: PackageResolver,
    templateFactoryManager: TemplateFactoryManager,
    fileConstants: FileConstants
) : TemplateDataSource, TemplateFactoryManager by templateFactoryManager {

    override fun allTemplate(): Flowable<Template> = listOf(
        Flowable.fromCallable(::default),
        provideTemplateLegacy(::version1),
        provideTemplateVersion(version = 2, factory = ::version2),
        provideTemplateVersion(version = 3, factory = ::version3),
        provideTemplateHtz(::versionHtz)
    )
        .mergeDelayError()

    override fun findById(id: String): Single<Template> = allTemplate()
        .filter { it.id == id }
        .first(default())

    private val installedTemplateLegacy: () -> Flowable<AppInfo> =
        (packageResolver::installedTemplateLegacy)

    private val installedTemplate: (Int) -> Flowable<AppInfo> =
        (packageResolver::installedTemplate)

    private val htzDir: () -> File = (fileConstants::htzDir)

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
    ): Flowable<Template> = Flowable.fromCallable { htzDir() }
        .map { it.listFilesOrEmpty() }
        .flatMap { it.toFlowable() }
        .filter { it.isDirAndCanRead() }
        .map { factory(it.name, it.lastModified()) }
}
