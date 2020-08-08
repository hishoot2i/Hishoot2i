package template.factory

import android.content.Context
import common.PathBuilder.stringTemplateApp
import common.ext.openAssetsFrom

class Version3Factory(
    private val appContext: Context,
    private val packageName: String,
    private val installedDate: Long
) : Factory<template.Template.Version3> {
    @Throws(Exception::class) override fun newTemplate(): template.Template.Version3 {
        val model = readModel()
        val frame = stringTemplateApp(packageName, model.frame)
        val preview = model.previewNormalize() ?: frame
        val shadow = model.shadowNormalize()
        val glares = model.glaresNormalize()
        return template.Template.Version3(
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

    private fun template.model.ModelV3.glaresNormalize(): List<entity.Glare>? =
        glares?.map { it.copy(name = stringTemplateApp(packageName, it.name)) }

    private fun template.model.ModelV3.previewNormalize(): String? =
        preview?.let { stringTemplateApp(packageName, it) }

    private fun template.model.ModelV3.shadowNormalize(): String? =
        shadow?.let { stringTemplateApp(packageName, it) }

    @Throws(Exception::class) private fun readModel(): template.model.ModelV3 =
        appContext.openAssetsFrom(packageName, template.TemplateConstants.TEMPLATE_CFG)
            .let { stream -> template.reader.ModelV3Reader(stream).use { it.model() } }
}
