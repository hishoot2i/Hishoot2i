package template

import android.content.Context
import androidx.annotation.CheckResult
import common.FileConstants
import template.Template.Default
import template.Template.Version1
import template.Template.Version2
import template.Template.Version3
import template.Template.VersionHtz
import template.factory.DefaultFactory
import template.factory.Version1Factory
import template.factory.Version2Factory
import template.factory.Version3Factory
import template.factory.VersionHtzFactory
import java.io.File

class TemplateFactoryManagerImpl constructor(
    context: Context,
    fileConstants: FileConstants
) : TemplateFactoryManager {

    private val appContext by lazy { context.applicationContext }
    private val defaultID = TemplateConstants.DEFAULT_TEMPLATE_ID
    private val cache = mutableMapOf<String, Template>()
    private val htzDir: () -> File = (fileConstants::htzDir)

    @Suppress("UNCHECKED_CAST")
    private fun <T : Template> getFromCache(key: String): T? =
        if (cache.containsKey(key)) cache[key] as? T else null

    private fun Template.putToCache(key: String) {
        if (!cache.containsValue(this) &&
            !cache.containsKey(key)
        ) {
            cache[key] = this
        }
    }

    @CheckResult
    @Throws(Exception::class)
    override fun default(): Default = getFromCache(defaultID)
        ?: DefaultFactory(appContext).newTemplate()
            .also { it.putToCache(defaultID) }

    @CheckResult
    @Throws(Exception::class)
    override fun version1(
        packageName: String,
        installedDate: Long
    ): Version1 = getFromCache(packageName)
        ?: Version1Factory(
            appContext,
            packageName,
            installedDate
        ).newTemplate()
            .also { it.putToCache(packageName) }

    @CheckResult
    @Throws(Exception::class)
    override fun version2(
        packageName: String,
        installedDate: Long
    ): Version2 = getFromCache(packageName)
        ?: Version2Factory(
            appContext,
            packageName,
            installedDate
        ).newTemplate()
            .also { it.putToCache(packageName) }

    @CheckResult
    @Throws(Exception::class)
    override fun version3(
        packageName: String,
        installedDate: Long
    ): Version3 = getFromCache(packageName)
        ?: Version3Factory(
            appContext,
            packageName,
            installedDate
        ).newTemplate()
            .also { it.putToCache(packageName) }

    @CheckResult
    @Throws(Exception::class)
    override fun versionHtz(
        htzPath: String,
        installedDate: Long
    ): VersionHtz = getFromCache(htzPath)
        ?: VersionHtzFactory(
            htzDir(),
            htzPath,
            installedDate
        ).newTemplate()
            .also { it.putToCache(htzPath) }
}
