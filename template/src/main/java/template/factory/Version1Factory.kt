package template.factory

import android.content.Context
import common.PathBuilder.stringTemplateApp
import common.ext.drawableSizes
import common.ext.openAssetsFrom
import entity.Sizes
import template.Template.Version1
import template.TemplateConstants
import template.model.ModelV1
import template.reader.ModelV1Reader

class Version1Factory(
    private val appContext: Context,
    private val packageName: String,
    private val installedDate: Long
) : Factory<Version1> {
    @Throws(Exception::class) override fun newTemplate(): Version1 {
        val model = readModel()
        val skinSizes = getSkinSizes()
        val skin = stringTemplateApp(packageName, TemplateConstants.SKIN)
        val coordinate = model.getCoordinate(skinSizes)
        return Version1(
            packageName,
            model.author,
            model.device,
            "Template V1",
            skin,
            skinSizes, //
            coordinate,
            installedDate
        )
    }

    private fun ModelV1.getCoordinate(skinSizes: Sizes): List<Float> {
        val (ax, ay) = skinSizes - Sizes(botx, boty)
        return listOf(topx, topy, ax, topy, topx, ay, ax, ay).map { it.toFloat() }
    }

    private fun getSkinSizes(): Sizes =
        appContext.drawableSizes(packageName, TemplateConstants.SKIN)
            ?: throw IllegalStateException("$packageName: can't read skin Sizes")

    @Throws(Exception::class) private fun readModel(): ModelV1 =
        appContext.openAssetsFrom(packageName, TemplateConstants.KETERANGAN_XML)
            .let { stream -> ModelV1Reader(stream).use { it.model() } }
}
