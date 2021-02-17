package template.factory

import android.content.Context
import common.PathBuilder.stringTemplateApp
import common.ext.openAssetsFrom
import entity.Glare
import entity.Sizes
import entity.SizesF
import template.Template.Version2
import template.TemplateConstants.FRAME
import template.TemplateConstants.GLARE
import template.TemplateConstants.PREVIEW
import template.TemplateConstants.SHADOW
import template.TemplateConstants.TEMPLATE_CFG
import template.reader.ModelV2Reader

class Version2Factory(
    private val appContext: Context,
    private val packageName: String,
    private val installedDate: Long
) : Factory<Version2> {
    @Throws(Exception::class)
    override fun newTemplate(): Version2 {
        val model = appContext.openAssetsFrom(packageName, TEMPLATE_CFG)
            .let { ModelV2Reader(it).use(ModelV2Reader::model) }
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
