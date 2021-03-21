package template

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.core.util.lruCache
import common.FileConstants
import template.Template.Default
import template.Template.Version1
import template.Template.Version2
import template.Template.Version3
import template.Template.VersionHtz
import template.TemplateConstants.DEFAULT_TEMPLATE_ID
import template.TemplateConstants.TEMPLATE_CFG
import template.converter.HtzConverter
import template.converter.HtzConverterImpl
import template.factory.DefaultFactory
import template.factory.Factory
import template.factory.Version1Factory
import template.factory.Version2Factory
import template.factory.Version3Factory
import template.factory.VersionHtzFactory
import template.model.ModelHtz
import template.serialize.ModelSerialize
import template.serialize.ModelSerializeImpl
import java.io.File
import java.util.Locale
import java.util.zip.ZipFile

@WorkerThread
class TemplateFactoryManagerImpl constructor(
    private val appContext: Context,
    fileConstants: FileConstants
) : TemplateFactoryManager, ModelSerialize by ModelSerializeImpl {

    private val cache = lruCache<String, Template>(maxSize = 64)
    private val htzDir: () -> File = (fileConstants::htzDir)
    private val savedDir: () -> File = (fileConstants::savedDir)

    private val htzConverter: HtzConverter by lazy {
        HtzConverterImpl(appContext, htzDir, ::encodeModelHtz)
    }

    override fun default(): Default = DEFAULT_TEMPLATE_ID cacheOrNewBy DefaultFactory(appContext)

    override fun version1(name: String, installed: Long): Version1 =
        name cacheOrNewBy Version1Factory(appContext, name, installed, ::decodeModelV1)

    override fun version2(name: String, installed: Long): Version2 =
        name cacheOrNewBy Version2Factory(appContext, name, installed, ::decodeModelV2)

    override fun version3(name: String, installed: Long): Version3 =
        name cacheOrNewBy Version3Factory(appContext, name, installed, ::decodeModelV3)

    override fun versionHtz(path: String, installed: Long): VersionHtz =
        path cacheOrNewBy VersionHtzFactory(htzDir(), path, installed, ::decodeModelHtz)

    override fun importHtz(file: File): VersionHtz {
        val id = ZipFile(file).entryInputStream(TEMPLATE_CFG)
            .use(::decodeModelHtz).run { generatorHtzId(this) }
        decompressHtz(file, File(htzDir(), id))
        return versionHtz(id, System.currentTimeMillis())
    }

    override fun convertHtz(template: Template): VersionHtz {
        val id = htzConverter.convert(template, ::generatorHtzId)
        return versionHtz(id, System.currentTimeMillis())
    }

    override fun exportHtz(versionHtz: VersionHtz): File {
        val src = File(htzDir(), versionHtz.id)
        check(src.exists() && src.isDirectory) { "src must exist and its dir, ${src.absolutePath}" }
        val dst = File(savedDir(), "${versionHtz.name}.htz")
        compressHtz(src, dst)
        return dst
    }

    override fun removeHtz(versionHtz: VersionHtz): String {
        File(htzDir(), versionHtz.id).deleteRecursively()
        return versionHtz.name
    }

    private fun generatorHtzId(modelHtz: ModelHtz): String = modelHtz.run {
        "${author.hashCode()}_${name.toLowerCase(Locale.ROOT)}"
            .replace("[^\\w]".toRegex(), "") // removing non word char
            .trim().take(32) // limit
    }

    private inline infix fun <reified T : Template> String.cacheOrNewBy(factory: Factory<T>): T =
        cache.get(this) as? T? ?: factory.newTemplate().also { cache.put(this, it) }
}
