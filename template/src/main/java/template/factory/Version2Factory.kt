package template.factory

import android.content.Context
import common.PathBuilder.stringTemplateApp
import common.ext.openAssetsFrom
import entity.Glare
import entity.SizesF
import template.Template.Version2
import template.TemplateConstants
import template.model.ModelV2
import template.reader.ModelV2Reader

class Version2Factory(
    private val appContext: Context,
    private val packageName: String,
    private val installedDate: Long
) : Factory<Version2> {
    @Throws(Exception::class) override fun newTemplate(): Version2 {
        val model = readModel()
        val coordinate = model.getCoordinate()
        val templateSize = model.templateSize()
        // NOTE: Template.Version2: Glare size == template size.
        val glare = Glare(
            stringTemplateApp(packageName, TemplateConstants.GLARE),
            templateSize,
            SizesF.ZERO
        )
        val frame = stringTemplateApp(packageName, TemplateConstants.FRAME)
        val preview = stringTemplateApp(packageName, TemplateConstants.PREVIEW)
        val shadow = stringTemplateApp(packageName, TemplateConstants.SHADOW)
        return Version2(
            packageName,
            model.author,
            model.name,
            "Template V2",
            frame,
            preview,
            templateSize,
            coordinate,
            installedDate,
            shadow,
            glare
        )
    }

    private fun ModelV2.templateSize(): entity.Sizes =
        entity.Sizes(template_width, template_height)

    private fun ModelV2.getCoordinate(): List<Float> = listOf(
        left_top_x, left_top_y,
        right_top_x, right_top_y,
        left_bottom_x, left_bottom_y,
        right_bottom_x, right_bottom_y
    ).map { it.toFloat() }

    @Throws(Exception::class) private fun readModel(): ModelV2 =
        appContext.openAssetsFrom(packageName, TemplateConstants.TEMPLATE_CFG)
            .let { stream -> ModelV2Reader(stream).use { it.model() } }
}
