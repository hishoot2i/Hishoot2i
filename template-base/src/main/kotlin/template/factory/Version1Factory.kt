package template.factory

import common.PathBuilder.stringTemplateApp
import entity.Sizes
import template.Template.Version1
import template.TemplateConstants.KETERANGAN_XML
import template.TemplateConstants.SKIN
import template.model.ModelV1

class Version1Factory(
    private val packageName: String,
    private val installedDate: Long,
    private val decodeModel: (String) -> ModelV1,
    private val calculateSizes: (String) -> Sizes
) : Factory<Version1> {
    override fun newTemplate(): Version1 {
        val model = decodeModel(KETERANGAN_XML)
        val skinSizes = calculateSizes(SKIN)
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
