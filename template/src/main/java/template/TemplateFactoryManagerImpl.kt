package template

import android.content.Context
import androidx.annotation.CheckResult

import common.FileConstants
import kotlin.LazyThreadSafetyMode.NONE

class TemplateFactoryManagerImpl constructor(
    context: Context,
    fileConstants: FileConstants
) : TemplateFactoryManager,
    FileConstants by fileConstants {
    private val appContext by lazy(NONE) { context.applicationContext }
    private val defaultID = TemplateConstants.DEFAULT_TEMPLATE_ID
    private val cache = mutableMapOf<String, Template>()

    @Suppress("UNCHECKED_CAST")
    private fun <T : Template> getFromCache(key: String): T? =
        if (cache.containsKey(key)) cache[key] as? T else null

    private fun Template.putToCache(key: String) {
        if (cache.containsValue(this).not() &&
            cache.containsKey(key).not() &&
            this !is Template.Empty
        ) {
            cache[key] = this
        }
    }

    @CheckResult
    @Throws(Exception::class)
    override fun default(): Template.Default = getFromCache(defaultID)
        ?: template.factory.DefaultFactory(appContext).newTemplate()
            .also { it.putToCache(defaultID) }

    @CheckResult
    @Throws(Exception::class)
    override fun version1(packageName: String, installedDate: Long): Template.Version1 =
        getFromCache(packageName) ?: template.factory.Version1Factory(
            appContext,
            packageName,
            installedDate
        ).newTemplate().also { it.putToCache(packageName) }

    @CheckResult
    @Throws(Exception::class)
    override fun version2(packageName: String, installedDate: Long): Template.Version2 =
        getFromCache(packageName) ?: template.factory.Version2Factory(
            appContext,
            packageName,
            installedDate
        ).newTemplate().also { it.putToCache(packageName) }

    @CheckResult
    @Throws(Exception::class)
    override fun version3(packageName: String, installedDate: Long): Template.Version3 =
        getFromCache(packageName) ?: template.factory.Version3Factory(
            appContext,
            packageName,
            installedDate
        ).newTemplate().also { it.putToCache(packageName) }

    @CheckResult
    @Throws(Exception::class)
    override fun versionHtz(htzPath: String, installedDate: Long): Template.VersionHtz =
        getFromCache(htzPath) ?: template.factory.VersionHtzFactory(
            htzDir() /* FileConstants */,
            htzPath,
            installedDate
        ).newTemplate().also { it.putToCache(htzPath) }
}
