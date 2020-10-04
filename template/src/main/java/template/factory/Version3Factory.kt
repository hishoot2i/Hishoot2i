package template.factory

import android.content.Context
import common.PathBuilder.stringTemplateApp
import common.ext.openAssetsFrom
import entity.Glare
import template.Template.Version3
import template.TemplateConstants
import template.model.ModelV3
import template.reader.ModelV3Reader

class Version3Factory(
    private val appContext: Context,
    private val packageName: String,
    private val installedDate: Long
) : Factory<Version3> {
    @Throws(Exception::class) override fun newTemplate(): Version3 {
        val model = readModel()
        val frame = stringTemplateApp(packageName, model.frame)
        val preview = model.previewNormalize() ?: frame
        val shadow = model.shadowNormalize()
        val glares = model.glaresNormalize()
        return Version3(
            packageName,
            model.author,
            model.name,
            model.desc ?: "Template V3",
            frame,
            preview,
            model.size,
            model.coordinate,
            installedDate,
            shadow,
            glares
        )
    }

    private fun ModelV3.glaresNormalize(): List<Glare>? =
        glares?.map { it.copy(name = stringTemplateApp(packageName, it.name)) }

    private fun ModelV3.previewNormalize(): String? =
        preview?.let { stringTemplateApp(packageName, it) }

    private fun ModelV3.shadowNormalize(): String? =
        shadow?.let { stringTemplateApp(packageName, it) }

    @Throws(Exception::class) private fun readModel(): ModelV3 =
        appContext.openAssetsFrom(packageName, TemplateConstants.TEMPLATE_CFG)
            .let { stream -> ModelV3Reader(stream).use { it.model() } }
}
