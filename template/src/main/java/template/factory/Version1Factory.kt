package template.factory

import android.content.Context
import common.PathBuilder.stringTemplateApp
import common.ext.drawableSizes
import common.ext.openAssetsFrom

class Version1Factory(
    private val appContext: Context,
    private val packageName: String,
    private val installedDate: Long
) : Factory<template.Template.Version1> {
    @Throws(Exception::class) override fun newTemplate(): template.Template.Version1 {
        val model = readModel()
        val skinSizes = getSkinSizes()
        val skin = stringTemplateApp(packageName, template.TemplateConstants.SKIN)
        val coordinate = model.getCoordinate(skinSizes)
        return template.Template.Version1(
            packageName,
            model.author,
            model.device,
            "Template V1",
            skin,
            skinSizes, //
            coordinate,
            installedDate
        )
    }

    private fun template.model.ModelV1.getCoordinate(skinSizes: entity.Sizes): List<Float> {
        val (ax, ay) = skinSizes - entity.Sizes(botx, boty)
        return listOf(topx, topy, ax, topy, topx, ay, ax, ay).map { it.toFloat() }
    }

    private fun getSkinSizes(): entity.Sizes =
        appContext.drawableSizes(packageName, template.TemplateConstants.SKIN)
            ?: throw IllegalStateException("$packageName: can't read skin Sizes")

    @Throws(Exception::class) private fun readModel(): template.model.ModelV1 =
        appContext.openAssetsFrom(packageName, template.TemplateConstants.KETERANGAN_XML)
            .let { stream -> template.reader.ModelV1Reader(stream).use { it.model() } }
}
