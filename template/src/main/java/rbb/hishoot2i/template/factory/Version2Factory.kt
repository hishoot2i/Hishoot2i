package rbb.hishoot2i.template.factory

import android.content.Context
import rbb.hishoot2i.common.PathBuilder.stringTemplateApp
import rbb.hishoot2i.common.entity.Glare
import rbb.hishoot2i.common.entity.Sizes
import rbb.hishoot2i.common.ext.openAssetsFrom
import rbb.hishoot2i.template.Template
import rbb.hishoot2i.template.TemplateConstants.FRAME
import rbb.hishoot2i.template.TemplateConstants.GLARE
import rbb.hishoot2i.template.TemplateConstants.PREVIEW
import rbb.hishoot2i.template.TemplateConstants.SHADOW
import rbb.hishoot2i.template.TemplateConstants.TEMPLATE_CFG
import rbb.hishoot2i.template.model.ModelV2
import rbb.hishoot2i.template.reader.ModelV2Reader

class Version2Factory(
    private val appContext: Context,
    private val packageName: String,
    private val installedDate: Long
) : Factory<Template.Version2> {
    @Throws(Exception::class)
    override fun newTemplate(): Template.Version2 {
        val model: ModelV2 = appContext.openAssetsFrom(packageName, TEMPLATE_CFG)
            .let { ModelV2Reader(it).use { it.model() } }
        val coordinate = with(model) {
            listOf(
                left_top_x, left_top_y,
                right_top_x, right_top_y,
                left_bottom_x, left_bottom_y,
                right_bottom_x, right_bottom_y
            ).map { it.toFloat() }
        }
        val templateSize = with(model) { Sizes(template_width, template_height) }
        // NOTE: TemplateV2: Glare size == template size.
        val glare = Glare(stringTemplateApp(packageName, GLARE), templateSize, Sizes.ZERO)

        return with(model) {
            Template.Version2(
                packageName,
                author,
                name,
                "Template V2",
                stringTemplateApp(packageName, FRAME),
                stringTemplateApp(packageName, PREVIEW),
                templateSize,
                coordinate,
                installedDate,
                stringTemplateApp(packageName, SHADOW),
                glare
            )
        }
    }
}