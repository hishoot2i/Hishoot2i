package org.illegaller.ratabb.hishoot2i.data.source

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
        resolveTemplateLegacy().map { (packageName, firstInstallTime) ->
            version1(packageName, firstInstallTime)
        },
        resolveTemplateApk(2).map { (packageName, firstInstallTime) ->
            version2(packageName, firstInstallTime)
        },
        resolveTemplateApk(3).map { (packageName, firstInstallTime) ->
            version3(packageName, firstInstallTime)
        },
        resolveTemplateHtz().map { file ->
            versionHtz(file.name, file.lastModified())
        }
    )
        .mergeDelayError()
        // ignore error
        .onErrorResumeNext { Flowable.empty() }

    override fun findByIdOrDefault(id: String): Single<Template> = allTemplate()
        .filter { it.id == id }
        .first(default())

    private val resolveTemplateLegacy: () -> Flowable<AppInfo> =
        (packageResolver::installedTemplateLegacy)

    private val resolveTemplateApk: (Int) -> Flowable<AppInfo> =
        (packageResolver::installedTemplate)

    private val resolveTemplateHtz: () -> Flowable<File> =
        (htzResolver::installedHtz)
}
