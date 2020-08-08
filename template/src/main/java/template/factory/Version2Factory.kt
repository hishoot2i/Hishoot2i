package template.factory

import android.content.Context
import common.PathBuilder.stringTemplateApp
import common.ext.openAssetsFrom

class Version2Factory(
    private val appContext: Context,
    private val packageName: String,
    private val installedDate: Long
) : Factory<template.Template.Version2> {
    @Throws(Exception::class) override fun newTemplate(): template.Template.Version2 {
        val model = readModel()
        val coordinate = model.getCoordinate()
        val templateSize = model.templateSize()
        // NOTE: TemplateV2: Glare size == template size.
        val glare = entity.Glare(
            stringTemplateApp(packageName, template.TemplateConstants.GLARE),
            templateSize,
            entity.Sizes.ZERO
        )
        val frame = stringTemplateApp(packageName, template.TemplateConstants.FRAME)
        val preview = stringTemplateApp(packageName, template.TemplateConstants.PREVIEW)
        val shadow = stringTemplateApp(packageName, template.TemplateConstants.SHADOW)
        return template.Template.Version2(
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

    private fun template.model.ModelV2.templateSize(): entity.Sizes =
        entity.Sizes(template_width, template_height)

    private fun template.model.ModelV2.getCoordinate(): List<Float> = listOf(
        left_top_x, left_top_y,
        right_top_x, right_top_y,
        left_bottom_x, left_bottom_y,
        right_bottom_x, right_bottom_y
    ).map { it.toFloat() }

    @Throws(Exception::class) private fun readModel(): template.model.ModelV2 =
        appContext.openAssetsFrom(packageName, template.TemplateConstants.TEMPLATE_CFG)
            .let { stream -> template.reader.ModelV2Reader(stream).use { it.model() } }
}
