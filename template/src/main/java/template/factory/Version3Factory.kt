package template.factory

import android.content.Context
import common.PathBuilder.stringTemplateApp
import common.ext.openAssetsFrom
import template.Template.Version3
import template.TemplateConstants.TEMPLATE_CFG
import template.reader.ModelV3Reader

class Version3Factory(
    private val appContext: Context,
    private val packageName: String,
    private val installedDate: Long
) : Factory<Version3> {
    @Throws(Exception::class)
    override fun newTemplate(): Version3 {
        val model = appContext.openAssetsFrom(packageName, TEMPLATE_CFG)
            .let { ModelV3Reader(it).use(ModelV3Reader::model) }
        val frame = stringTemplateApp(packageName, model.frame)
        return Version3(
            id = packageName,
            author = model.author,
            name = model.name,
            desc = model.desc ?: "Template V3",
            frame = frame,
            preview = model.preview?.let { stringTemplateApp(packageName, it) } ?: frame,
            sizes = model.size,
            coordinate = model.coordinate,
            installedDate = installedDate,
            shadow = model.shadow?.let { stringTemplateApp(packageName, it) },
            glares = model.glares?.map { it.copy(name = stringTemplateApp(packageName, it.name)) }
        )
    }
}
