package org.illegaller.ratabb.hishoot2i.data

import androidx.annotation.IntRange
import common.FileConstants
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.mergeDelayError
import io.reactivex.rxkotlin.toFlowable
import template.Template
import template.TemplateFactoryManager
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
        .filter { it.isNotEmpty } //

    override fun findById(id: String): Single<Template> = allTemplate()
        .filter { it.id == id }
        .first(default())

    private fun provideTemplateLegacy(factory: (String, Long) -> Template): Flowable<Template> =
        installedTemplateLegacy().map { factory(it.packageName, it.firstInstallTime) }
            .onErrorReturnItem(Template.Empty) //

    private fun provideTemplateVersion(
        @IntRange(from = 2, to = 3) version: Int,
        factory: (String, Long) -> Template
    ): Flowable<Template> =
        installedTemplate(version).map { factory(it.packageName, it.firstInstallTime) }
            .onErrorReturnItem(Template.Empty) //

    private fun provideTemplateHtz(factory: (String, Long) -> Template): Flowable<Template> =
        Flowable.fromCallable { htzDir() }
            .flatMap { it.listFiles()?.toFlowable() }
            .filter { it.canRead() && it.isDirectory }
            .map { factory(it.name, it.lastModified()) }
            .onErrorReturnItem(Template.Empty) //
}