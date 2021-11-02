package template.factory

import common.PathBuilder.stringTemplateApp
import entity.Glare
import entity.Sizes
import entity.SizesF
import template.Template.Version2
import template.TemplateConstants.FRAME
import template.TemplateConstants.GLARE
import template.TemplateConstants.PREVIEW
import template.TemplateConstants.SHADOW
import template.TemplateConstants.TEMPLATE_CFG
import template.model.ModelV2

class Version2Factory(
    private val packageName: String,
    private val installedDate: Long,
    private val decodeModel: (String) -> ModelV2
) : Factory<Version2> {
    override fun newTemplate(): Version2 {
        val model = decodeModel(TEMPLATE_CFG)
        val templateSize = model.run { Sizes(template_width, template_height) }
        return Version2(
            id = packageName,
            author = model.author,
            name = model.name,
            desc = "Template V2",
            frame = stringTemplateApp(packageName, FRAME),
            preview = stringTemplateApp(packageName, PREVIEW),
            sizes = templateSize,
            coordinate = model.run {
                listOf(
                    left_top_x, left_top_y, right_top_x, right_top_y,
                    left_bottom_x, left_bottom_y, right_bottom_x, right_bottom_y
                ).map(Int::toFloat)
            },
            installedDate = installedDate,
            shadow = stringTemplateApp(packageName, SHADOW),
            glare = Glare(
                stringTemplateApp(packageName, GLARE),
                templateSize, /* NOTE: Template.Version2: Glare size == template size. */
                SizesF.ZERO
            )
        )
    }
}
