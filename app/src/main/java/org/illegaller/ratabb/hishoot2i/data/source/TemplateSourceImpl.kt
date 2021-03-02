package org.illegaller.ratabb.hishoot2i.data.source

import entity.AppInfo
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
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

    override suspend fun allTemplate(): List<Template> = withContext(IO) {
        listOf(
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
    }

    override suspend fun findByIdOrDefault(id: String): Template = allTemplate()
        .firstOrNull { it.id == id } ?: default()

    override suspend fun searchByNameOrAuthor(query: String): List<Template> {
        val allTemplate = allTemplate()
        return when {
            query.isNotBlank() -> allTemplate.filter {
                it.name.contains(query, true) || it.author.contains(query, true)
            }
            else -> allTemplate
        }
    }

    private val resolveTemplateLegacy: suspend () -> List<AppInfo> =
        (packageResolver::installedTemplateLegacy)

    private val resolveTemplateApk: suspend (Int) -> List<AppInfo> =
        (packageResolver::installedTemplate)

    private val resolveTemplateHtz: suspend () -> List<File> =
        (htzResolver::installedHtz)

    /* TODO: avoid using this method! */
    private inline fun <T> createOrNull(creator: () -> T): T? =
        runCatching { creator() }.getOrNull()
}
