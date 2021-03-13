package org.illegaller.ratabb.hishoot2i.data.source

import entity.AppInfo
import org.illegaller.ratabb.hishoot2i.data.resolver.HtzResolver
import org.illegaller.ratabb.hishoot2i.data.resolver.PackageResolver
import template.Template
import template.TemplateFactoryManager
import java.io.File
import javax.inject.Inject

class TemplateSourceImpl @Inject constructor(
    packageResolver: PackageResolver,
    htzResolver: HtzResolver,
    templateFactoryManager: TemplateFactoryManager
) : TemplateSource, TemplateFactoryManager by templateFactoryManager {

    override fun allTemplate(): List<Template> = listOf(
        listOfNotNull(createOrNull { default() }),
        resolveTemplateLegacy().mapNotNull { (packageName, firstInstallTime) ->
            createOrNull { version1(packageName, firstInstallTime) }
        },
        resolveTemplateApk(2).mapNotNull { (packageName, firstInstallTime) ->
            createOrNull { version2(packageName, firstInstallTime) }
        },
        resolveTemplateApk(3).mapNotNull { (packageName, firstInstallTime) ->
            createOrNull { version3(packageName, firstInstallTime) }
        },
        resolveTemplateHtz().mapNotNull { file ->
            createOrNull { versionHtz(file.name, file.lastModified()) }
        }
    ).flatten()

    override fun findByIdOrDefault(id: String): Template = allTemplate()
        .firstOrNull { it.id == id } ?: default()

    override fun searchByNameOrAuthor(query: String): List<Template> =
        if (query.isNotBlank()) allTemplate().filterNameOrAuthor(query)
        else allTemplate()

    private val resolveTemplateLegacy: () -> List<AppInfo> =
        (packageResolver::installedTemplateLegacy)

    private val resolveTemplateApk: (Int) -> List<AppInfo> =
        (packageResolver::installedTemplate)

    private val resolveTemplateHtz: () -> List<File> =
        (htzResolver::installedHtz)

    private fun List<Template>.filterNameOrAuthor(query: String) =
        filter { it.name.contains(query, true) || it.author.contains(query, true) }

    private inline fun <T> createOrNull(creator: () -> T): T? =
        runCatching { creator() }.getOrNull()
}
