package rbb.hishoot2i.template.factory

import android.content.Context
import rbb.hishoot2i.common.PathBuilder.stringTemplateApp
import rbb.hishoot2i.common.ext.drawableSizes
import rbb.hishoot2i.common.ext.openAssetsFrom
import rbb.hishoot2i.template.Template
import rbb.hishoot2i.template.TemplateConstants.KETERANGAN_XML
import rbb.hishoot2i.template.TemplateConstants.SKIN
import rbb.hishoot2i.template.TemplateException
import rbb.hishoot2i.template.model.ModelV1
import rbb.hishoot2i.template.reader.ModelV1Reader

class Version1Factory(
    private val appContext: Context,
    private val packageName: String,
    private val installedDate: Long
) : Factory<Template.Version1> {
    @Throws(Exception::class)
    override fun newTemplate(): Template.Version1 {
        val model: ModelV1 = appContext.openAssetsFrom(packageName, KETERANGAN_XML)
            .let { ModelV1Reader(it).use { it.model() } }
        val skinSizes = appContext.drawableSizes(packageName, SKIN)
                ?: throw TemplateException("$packageName: can't read skin Sizes")
        val coordinate = with(model) {
            listOf(
                topx, topy,
                skinSizes.x - botx, topy,
                topx, skinSizes.y - boty,
                skinSizes.x - botx, skinSizes.y - boty
            ).map { it.toFloat() }
        }
        return Template.Version1(
            packageName,
            model.author,
            model.device,
            "Template V1",
            stringTemplateApp(packageName, SKIN),
            skinSizes, //
            coordinate,
            installedDate
        )
    }
}