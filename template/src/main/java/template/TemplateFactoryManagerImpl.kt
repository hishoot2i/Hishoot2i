package template

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.core.util.lruCache
import common.FileConstants
import common.ext.drawableSizes
import common.ext.graphics.bitmapSize
import common.ext.openAssetsFrom
import common.ext.openRawResource
import common.ext.resourcesFrom
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
import template.serialize.ModelSerialize
import template.serialize.ModelSerializeImpl
import java.io.File
import java.util.zip.ZipFile

@WorkerThread
class TemplateFactoryManagerImpl constructor(
    private val appContext: Context,
    fileConstants: FileConstants
) : TemplateFactoryManager {

    private val cache = lruCache<String, Template>(maxSize = 64)
    private val htzDir: () -> File = (fileConstants::htzDir)
    private val savedDir: () -> File = (fileConstants::savedDir)

    private val serialize: ModelSerialize = ModelSerializeImpl

    private val htzConverter: HtzConverter = HtzConverterImpl(
        htzDir = htzDir,
        encodeModelHtz = serialize::encodeModelHtz,
        assetTemplate = { packageName, drawableName ->
            appContext.resourcesFrom(packageName)
                .openRawResource(drawableName, "drawable", packageName)
                ?: throw IllegalStateException("Failed open $drawableName from $packageName")
        }
    )

    override fun default(): Default = DEFAULT_TEMPLATE_ID cacheOrNewBy DefaultFactory(appContext)

    override fun version1(packageName: String, installedDate: Long): Version1 =
        packageName cacheOrNewBy Version1Factory(
            packageName = packageName,
            installedDate = installedDate,
            decodeModel = { asset ->
                appContext.openAssetsFrom(packageName, asset).use(serialize::decodeModelV1)
            },
            calculateSizes = { drawableName ->
                appContext.drawableSizes(packageName, drawableName)
                    ?: throw IllegalStateException("Can't read skin Sizes, id = $packageName")
            }
        )

    override fun version2(packageName: String, installedDate: Long): Version2 =
        packageName cacheOrNewBy Version2Factory(
            packageName = packageName,
            installedDate = installedDate,
            decodeModel = { asset ->
                appContext.openAssetsFrom(packageName, asset).use(serialize::decodeModelV2)
            }
        )

    override fun version3(packageName: String, installedDate: Long): Version3 =
        packageName cacheOrNewBy Version3Factory(
            packageName = packageName,
            installedDate = installedDate,
            decodeModel = { asset ->
                appContext.openAssetsFrom(packageName, asset).use(serialize::decodeModelV3)
            }
        )

    override fun versionHtz(path: String, installedDate: Long): VersionHtz =
        path cacheOrNewBy VersionHtzFactory(
            htzBaseDir = htzDir(),
            htzPath = path,
            installedDate = installedDate,
            decodeModel = { cfg ->
                val currentPath = File(htzDir(), path)
                File(currentPath, cfg).inputStream().use(serialize::decodeModelHtz)
            },
            calculateSizes = File::bitmapSize
        )

    override fun importHtz(file: File): VersionHtz {
        val id = ZipFile(file).entryInputStream(TEMPLATE_CFG)
            .use(serialize::decodeModelHtz).newHtzId
        decompressHtz(file, File(htzDir(), id))
        return versionHtz(id, System.currentTimeMillis())
    }

    override fun convertHtz(template: Template): VersionHtz =
        versionHtz(htzConverter.convert(template), System.currentTimeMillis())

    override fun exportHtz(versionHtz: VersionHtz): File {
        val src = File(htzDir(), versionHtz.id)
        check(src.exists() && src.isDirectory) { "src must exist and its dir, ${src.absolutePath}" }
        val dst = File(savedDir(), "${versionHtz.name}.htz")
        compressHtz(src, dst)
        return dst
    }

    override fun removeHtz(versionHtz: VersionHtz): String {
        if (File(htzDir(), versionHtz.id).deleteRecursively()) {
            return versionHtz.name
        } else throw IllegalStateException("Something wrong when removing htz= ${versionHtz.name}")
    }

    private inline infix fun <reified T : Template> String.cacheOrNewBy(factory: Factory<T>): T =
        cache.get(this) as? T? ?: factory.newTemplate().also { cache.put(this, it) }
}
