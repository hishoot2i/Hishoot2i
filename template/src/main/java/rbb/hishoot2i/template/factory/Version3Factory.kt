package rbb.hishoot2i.template.factory

import android.content.Context
import rbb.hishoot2i.common.PathBuilder.stringTemplateApp
import rbb.hishoot2i.common.entity.Glare
import rbb.hishoot2i.common.ext.openAssetsFrom
import rbb.hishoot2i.template.Template
import rbb.hishoot2i.template.TemplateConstants.TEMPLATE_CFG
import rbb.hishoot2i.template.model.ModelV3
import rbb.hishoot2i.template.reader.ModelV3Reader

class Version3Factory(
    private val appContext: Context,
    private val packageName: String,
    private val installedDate: Long
) : Factory<Template.Version3> {
    @Throws(Exception::class)
    override fun newTemplate(): Template.Version3 {
        val model: ModelV3 = appContext.openAssetsFrom(packageName, TEMPLATE_CFG)
            .let { ModelV3Reader(it).use { it.model() } }
        val frame: String = stringTemplateApp(packageName, model.frame)
        val preview: String = model.preview?.let { stringTemplateApp(packageName, it) } ?: frame
        val shadow: String? = model.shadow?.let { stringTemplateApp(packageName, it) }
        val glares: List<Glare>? = model.glares?.let { list: List<Glare> ->
            list.map { glare: Glare ->
                glare.copy(name = stringTemplateApp(packageName, glare.name))
            }
        }
        return with(model) {
            Template.Version3(
                packageName,
                author,
                name,
                desc ?: "Template V3",
                frame,
                preview,
                size,
                coordinate,
                installedDate,
                shadow,
                glares
            )
        }
    }
}