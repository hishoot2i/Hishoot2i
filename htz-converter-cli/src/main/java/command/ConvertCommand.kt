package command

import apk.binaryXmlManifest
import entity.Sizes
import template.compressHtz
import template.converter.HtzConverter
import template.converter.HtzConverterImpl
import template.entryInputStream
import template.factory.Version1Factory
import template.factory.Version2Factory
import template.factory.Version3Factory
import template.serialize.ModelSerialize
import template.serialize.ModelSerializeImpl
import java.io.File
import java.io.InputStream
import java.util.zip.ZipFile
import javax.imageio.ImageIO

class ConvertCommand @JvmOverloads constructor(
    apkFilePath: String,
    private val tempDir: String = System.getProperty("java.io.tmpdir"),
    private val outputDir: String? = null
) {
    init {
        if (outputDir != null) {
            File(outputDir).run {
                require(isDirectory && canWrite()) { "$outputDir can't write." }
            }
        }
        File(tempDir).run {
            require(isDirectory && canWrite()) { "$tempDir can't write." }
        }
    }

    private val apkFile = File(apkFilePath).run {
        require(isFile && canRead()) { " File= $apkFilePath cant read!" }
        ZipFile(this)
    }

    private val serialize: ModelSerialize = ModelSerializeImpl

    private val htzConverter: HtzConverter = HtzConverterImpl(
        { File(tempDir) },
        serialize::encodeModelHtz,
        { _, drawableName -> openResDrawable(drawableName) }
    )

    fun run() {
        println("=====================================")
        println(">>          Htz Converter          <<")
        println("=====================================")
        val (packageName, templateVersion) =
            apkFile.entryInputStream(apk.ANDROID_MANIFEST).binaryXmlManifest()
        println("  packageName    : $packageName")
        println("  templateVersion: $templateVersion")
        val unused = -1L // unused installed date.
        val factory = when (templateVersion) {
            1 -> Version1Factory(
                packageName, unused,
                { asset ->
                    openAssets(asset).use(serialize::decodeModelV1)
                },
                { drawableName ->
                    ImageIO.read(openResDrawable(drawableName)).run { Sizes(width, height) }
                }
            )
            2 -> Version2Factory(packageName, unused) { asset ->
                openAssets(asset).use(serialize::decodeModelV2)
            }
            3 -> Version3Factory(packageName, unused) { asset ->
                openAssets(asset).use(serialize::decodeModelV3)
            }
            else -> throw IllegalStateException("Not apk template= $packageName, $templateVersion")
        }
        val newHtzId = htzConverter.convert(factory.newTemplate())
        println("  newHtzId       : $newHtzId")

        val result = if (outputDir == null) File("$newHtzId.htz")
        else File(outputDir, "$newHtzId.htz")
        compressHtz(File(tempDir, newHtzId), result)
        println("=====================================")
        println("Done!!\n  Htz: ${result.absolutePath}")
    }

    private fun openResDrawable(name: String): InputStream {
        val resDrawable = "res/drawable/$name"
        var entry = apkFile.getEntry("$resDrawable.png") //
        if (entry == null) entry = apkFile.getEntry("$resDrawable.jpg") //
        return apkFile.getInputStream(entry)
    }

    private fun openAssets(assetName: String) = apkFile.entryInputStream("assets/$assetName")
}
