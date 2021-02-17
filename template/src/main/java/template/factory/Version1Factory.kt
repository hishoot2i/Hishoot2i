package template.factory

import android.content.Context
import common.PathBuilder.stringTemplateApp
import common.ext.drawableSizes
import common.ext.openAssetsFrom
import entity.Sizes
import template.Template.Version1
import template.TemplateConstants.KETERANGAN_XML
import template.TemplateConstants.SKIN
import template.reader.ModelV1Reader

class Version1Factory(
    private val appContext: Context,
    private val packageName: String,
    private val installedDate: Long
) : Factory<Version1> {
    @Throws(Exception::class)
    override fun newTemplate(): Version1 {
        val model = appContext.openAssetsFrom(packageName, KETERANGAN_XML)
            .let { ModelV1Reader(it).use(ModelV1Reader::model) }
        val skinSizes = appContext.drawableSizes(packageName, SKIN)
            ?: throw IllegalStateException("Can't read skin Sizes, id = $packageName")
        return Version1(
            id = packageName,
            author = model.author,
            name = model.device,
            desc = "Template V1",
            frame = stringTemplateApp(packageName, SKIN),
            sizes = skinSizes, //
            coordinate = model.run {
                val (ax, ay) = skinSizes - Sizes(botx, boty)
                listOf(topx, topy, ax, topy, topx, ay, ax, ay).map(Int::toFloat)
            },
            installedDate = installedDate
        )
    }
}
