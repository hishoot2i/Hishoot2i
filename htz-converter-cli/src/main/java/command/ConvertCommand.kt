package command

import apk.ApkTemplate.Companion.toApkTemplate
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
import kotlin.system.exitProcess

class ConvertCommand(apkFilePath: String) {
    private val tempDir: String = "build/tmp/" // TODO: System.getProperty("java.io.tmpdir") ?

    private val apkZip: ZipFile by lazy {
        val apkFile = File(apkFilePath)
        require(apkFile.isFile && apkFile.canRead()) { " File= $apkFilePath cant read!" }
        ZipFile(apkFile)
    }

    private val serialize: ModelSerialize = ModelSerializeImpl

    private val htzConverter: HtzConverter = HtzConverterImpl(
        { File(tempDir) },
        serialize::encodeModelHtz,
        { _, drawableName -> openResDrawable(drawableName) }
    )

    fun run() {
        echo("=====================================")
        echo(">>          Htz Converter          <<")
        echo("=====================================")

        val apkTemplate = try {
            apkZip.entryInputStream("AndroidManifest.xml").toApkTemplate()
        } catch (e: Exception) {
            throw IllegalStateException("parsing AndroidManifest", e)
        }
        val (packageName, templateVersion) = apkTemplate.run { packageName to templateVersion }
        echo("  packageName    : $packageName")
        echo("  templateVersion: $templateVersion")
        val today = System.currentTimeMillis()
        val factory = when (templateVersion) {
            1 -> Version1Factory(
                packageName, today,
                { asset ->
                    openAssets(asset).use { serialize.decodeModelV1(it) }
                },
                { drawableName ->
                    ImageIO.read(openResDrawable(drawableName)).run { Sizes(width, height) }
                }
            )
            2 -> Version2Factory(packageName, today) { asset ->
                openAssets(asset).use { serialize.decodeModelV2(it) }
            }
            3 -> Version3Factory(packageName, today) { asset ->
                openAssets(asset).use { serialize.decodeModelV3(it) }
            }
            else -> throw IllegalStateException("Not apk template= $packageName, $templateVersion")
        }
        val newHtzId = htzConverter.convert(factory.newTemplate())
        echo("  newHtzId= \t$newHtzId")

        val result = File("$newHtzId.htz")
        compressHtz(File(tempDir, newHtzId), result) // TODO: validation file result?
        echo("=====================================")
        echo("Done!!\n  Htz= \t${result.absolutePath}")
        exitProcess(0) //
    }

    private fun openResDrawable(name: String): InputStream {
        val resDrawable = "res/drawable/$name"
        var entry = apkZip.getEntry("$resDrawable.png") //
        if (entry == null) entry = apkZip.getEntry("$resDrawable.jpg") //
        return apkZip.getInputStream(entry)
    }

    private fun openAssets(assetName: String) = apkZip.entryInputStream("assets/$assetName")

    //
    private fun echo(message: String): Unit = println(message)
}
