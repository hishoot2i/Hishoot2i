package template.factory

import android.content.Context
import common.PathBuilder.stringTemplateApp
import common.ext.openAssetsFrom
import template.Template.Version3
import template.TemplateConstants.TEMPLATE_CFG
import template.model.ModelV3
import java.io.InputStream

internal class Version3Factory(
    private val appContext: Context,
    private val packageName: String,
    private val installedDate: Long,
    private val decodeModel: (InputStream) -> ModelV3
) : Factory<Version3> {
    override fun newTemplate(): Version3 {
        val model = appContext.openAssetsFrom(packageName, TEMPLATE_CFG).use { decodeModel(it) }
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
