package template.factory

import common.PathBuilder.stringFiles
import common.ext.graphics.bitmapSize
import entity.Glare
import entity.Sizes
import template.Template.VersionHtz
import template.TemplateConstants
import template.model.ModelHtz
import template.reader.ModelHtzReader
import java.io.File

class VersionHtzFactory(
    private val htzBaseDir: File,
    private val htzPath: String,
    private val installedDate: Long
) : Factory<VersionHtz> {
    @Throws(Exception::class) override fun newTemplate(): VersionHtz {
        val currentPath = File(htzBaseDir, htzPath)
        if (!currentPath.canRead()) throw IllegalStateException("Can't read: $htzPath")
        val model = currentPath.readModel()
        return VersionHtz(
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

    private fun ModelHtz.templateSize(): Sizes =
        Sizes(template_width, template_height)

    private fun ModelHtz.frameNormalize(currentPath: File): String =
        stringFiles(File(currentPath, template_file))

    private fun ModelHtz.previewNormalize(currentPath: File): String =
        stringFiles(File(currentPath, preview))

    private fun ModelHtz.glareNormalize(currentPath: File): Glare? {
        val glareFile = File(currentPath, overlay_file)
        return if (glareFile.canRead()) {
            glareFile.bitmapSize()?.let { sizes ->
                Glare(stringFiles(glareFile), sizes, Sizes(overlay_x, overlay_y).toSizeF())
            }
        } else null
    }

    private fun ModelHtz.getCoordinate(): List<Float> = listOf(
        screen_x, screen_y,
        screen_x + screen_width, screen_y,
        screen_x, screen_y + screen_height,
        screen_x + screen_width, screen_y + screen_height
    ).map { it.toFloat() }

    @Throws(Exception::class)
    private fun File.readModel(): ModelHtz =
        File(this, TemplateConstants.TEMPLATE_CFG).let { file ->
            ModelHtzReader(file.inputStream()).use { it.model() }
        }
}
