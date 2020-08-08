package template.factory

import common.PathBuilder.stringFiles
import common.ext.graphics.bitmapSize
import java.io.File

class VersionHtzFactory(
    private val htzBaseDir: File,
    private val htzPath: String,
    private val installedDate: Long
) : Factory<template.Template.VersionHtz> {
    @Throws(Exception::class) override fun newTemplate(): template.Template.VersionHtz {
        val currentPath = File(htzBaseDir, htzPath)
        if (!currentPath.canRead()) throw IllegalStateException("Can't read: $htzPath")
        val model = currentPath.readModel()
        return template.Template.VersionHtz(
            htzPath,
            model.author,
            model.name,
            "Template Htz",
            model.frameNormalize(currentPath),
            model.previewNormalize(currentPath),
            model.templateSize(),
            model.getCoordinate(),
            installedDate,
            model.glareNormalize(currentPath)
        )
    }

    private fun template.model.ModelHtz.templateSize(): entity.Sizes =
        entity.Sizes(template_width, template_height)

    private fun template.model.ModelHtz.frameNormalize(currentPath: File): String =
        stringFiles(File(currentPath, template_file))

    private fun template.model.ModelHtz.previewNormalize(currentPath: File): String =
        stringFiles(File(currentPath, preview))

    private fun template.model.ModelHtz.glareNormalize(currentPath: File): entity.Glare? {
        val glareFile = File(currentPath, overlay_file)
        return if (glareFile.canRead()) {
            glareFile.bitmapSize()?.let { sizes ->
                entity.Glare(stringFiles(glareFile), sizes, entity.Sizes(overlay_x, overlay_y))
            }
        } else null
    }

    private fun template.model.ModelHtz.getCoordinate(): List<Float> = listOf(
        screen_x, screen_y,
        screen_x + screen_width, screen_y,
        screen_x, screen_y + screen_height,
        screen_x + screen_width, screen_y + screen_height
    ).map { it.toFloat() }

    @Throws(Exception::class)
    private fun File.readModel(): template.model.ModelHtz =
        File(this, template.TemplateConstants.TEMPLATE_CFG).let { file ->
            template.reader.ModelHtzReader(file.inputStream()).use { it.model() }
        }
}
