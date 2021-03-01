package template

import android.content.Context
import androidx.annotation.CheckResult
import androidx.annotation.WorkerThread
import androidx.core.util.lruCache
import common.FileConstants
import template.Template.Default
import template.Template.Version1
import template.Template.Version2
import template.Template.Version3
import template.Template.VersionHtz
import template.TemplateConstants.DEFAULT_TEMPLATE_ID
import template.factory.DefaultFactory
import template.factory.Factory
import template.factory.Version1Factory
import template.factory.Version2Factory
import template.factory.Version3Factory
import template.factory.VersionHtzFactory
import java.io.File

@WorkerThread
class TemplateFactoryManagerImpl constructor(
    context: Context,
    fileConstants: FileConstants
) : TemplateFactoryManager {

    private val appContext = context.applicationContext
    private val cache = lruCache<String, Template>(maxSize = 64)
    private val htzDir: () -> File = (fileConstants::htzDir)

    @CheckResult
    @Throws(Exception::class)
    override fun default(): Default =
        cacheOrNew(DEFAULT_TEMPLATE_ID) { DefaultFactory(appContext) }

    @CheckResult
    @Throws(Exception::class)
    override fun version1(packageName: String, installedDate: Long): Version1 =
        cacheOrNew(packageName) { Version1Factory(appContext, packageName, installedDate) }

    @CheckResult
    @Throws(Exception::class)
    override fun version2(packageName: String, installedDate: Long): Version2 =
        cacheOrNew(packageName) { Version2Factory(appContext, packageName, installedDate) }

    @CheckResult
    @Throws(Exception::class)
    override fun version3(packageName: String, installedDate: Long): Version3 =
        cacheOrNew(packageName) { Version3Factory(appContext, packageName, installedDate) }

    @CheckResult
    @Throws(Exception::class)
    override fun versionHtz(htzPath: String, installedDate: Long): VersionHtz =
        cacheOrNew(htzPath) { VersionHtzFactory(htzDir(), htzPath, installedDate) }

    private inline fun <reified T : Template> cacheOrNew(
        key: String,
        factory: () -> Factory<T>
    ): T {
        cache.get(key)?.let { return it as T }
        return factory().newTemplate().also { cache.put(key, it) }
    }
}
