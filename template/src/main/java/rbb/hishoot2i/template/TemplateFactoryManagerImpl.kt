package rbb.hishoot2i.template

import android.content.Context
import android.support.annotation.CheckResult
import rbb.hishoot2i.common.FileConstants
import rbb.hishoot2i.template.factory.DefaultFactory
import rbb.hishoot2i.template.factory.Version1Factory
import rbb.hishoot2i.template.factory.Version2Factory
import rbb.hishoot2i.template.factory.Version3Factory
import rbb.hishoot2i.template.factory.VersionHtzFactory
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

class TemplateFactoryManagerImpl @Inject constructor(
    context: Context,
    fileConstants: FileConstants
) : TemplateFactoryManager,
    FileConstants by fileConstants {
    private val appContext by lazy(NONE) { context.applicationContext }
    private val cache = mutableMapOf<String, Template>()
    @CheckResult
    @Throws(Exception::class)
    override fun default(): Template.Default =
        cache[TemplateConstants.DEFAULT_TEMPLATE_ID] as? Template.Default
                ?: DefaultFactory(appContext).newTemplate()
                    .also { cache[TemplateConstants.DEFAULT_TEMPLATE_ID] = it }

    @CheckResult
    @Throws(Exception::class)
    override fun version1(packageName: String, installedDate: Long): Template.Version1 =
        cache[packageName] as? Template.Version1
                ?: Version1Factory(appContext, packageName, installedDate).newTemplate()
                    .also { cache[packageName] = it }

    @CheckResult
    @Throws(Exception::class)
    override fun version2(packageName: String, installedDate: Long): Template.Version2 =
        cache[packageName] as? Template.Version2
                ?: Version2Factory(appContext, packageName, installedDate).newTemplate()
                    .also { cache[packageName] = it }

    @CheckResult
    @Throws(Exception::class)
    override fun version3(packageName: String, installedDate: Long): Template.Version3 =
        cache[packageName] as? Template.Version3
                ?: Version3Factory(appContext, packageName, installedDate).newTemplate()
                    .also { cache[packageName] = it }

    @CheckResult
    @Throws(Exception::class)
    override fun versionHtz(htzPath: String, installedDate: Long): Template.VersionHtz =
        cache[htzPath] as? Template.VersionHtz
                ?: VersionHtzFactory(htzDir(), htzPath, installedDate).newTemplate()
                    .also { cache[htzPath] = it }
}