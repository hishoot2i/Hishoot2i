package org.illegaller.ratabb.hishoot2i.data.source

import org.illegaller.ratabb.hishoot2i.data.resolver.HtzResolver
import org.illegaller.ratabb.hishoot2i.data.resolver.PackageResolver
import template.Template
import template.TemplateFactoryManager
import timber.log.Timber
import javax.inject.Inject

class TemplateSourceImpl @Inject constructor(
    packageResolver: PackageResolver,
    htzResolver: HtzResolver,
    templateManager: TemplateFactoryManager
) : TemplateSource,
    TemplateFactoryManager by templateManager,
    PackageResolver by packageResolver,
    HtzResolver by htzResolver {

    override fun allTemplate(): List<Template> = listOf(
        listOfNotNull(createOrNull { default() }),
        installedTemplateLegacy().mapNotNull { (name, installed) ->
            createOrNull { version1(name, installed) }
        },
        installedTemplate(2).mapNotNull { (name, installed) ->
            createOrNull { version2(name, installed) }
        },
        installedTemplate(3).mapNotNull { (name, installed) ->
            createOrNull { version3(name, installed) }
        },
        installedHtz().mapNotNull { file ->
            createOrNull { versionHtz(file.name, file.lastModified()) }
        }
    ).flatten()

    override fun findByIdOrDefault(id: String): Template = allTemplate()
        .firstOrNull { it.id == id } ?: default()

    override fun searchByNameOrAuthor(query: String): List<Template> =
        if (query.isNotBlank()) allTemplate().filterNameOrAuthor(query)
        else allTemplate()

    private fun List<Template>.filterNameOrAuthor(query: String) =
        filter { it.name.contains(query, true) || it.author.contains(query, true) }

    private inline fun <T> createOrNull(creator: () -> T): T? = try {
        creator()
    } catch (e: Exception) {
        Timber.e(e)
        null
    }
    // runCatching { creator() }.getOrNull()
}
