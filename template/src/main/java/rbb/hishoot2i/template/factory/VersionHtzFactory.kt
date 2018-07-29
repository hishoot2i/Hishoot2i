package rbb.hishoot2i.template.factory

import rbb.hishoot2i.common.PathBuilder.stringFiles
import rbb.hishoot2i.common.entity.Glare
import rbb.hishoot2i.common.entity.Sizes
import rbb.hishoot2i.common.ext.graphics.bitmapSize
import rbb.hishoot2i.template.Template
import rbb.hishoot2i.template.TemplateConstants.TEMPLATE_CFG
import rbb.hishoot2i.template.TemplateException
import rbb.hishoot2i.template.model.ModelHtz
import rbb.hishoot2i.template.reader.ModelHtzReader
import java.io.File

class VersionHtzFactory(
    private val htzBaseDir: File,
    private val htzPath: String,
    private val installedDate: Long
) : Factory<Template.VersionHtz> {
    @Throws(Exception::class)
    override fun newTemplate(): Template.VersionHtz {
        val currentPath = File(htzBaseDir, htzPath)
        if (!currentPath.canRead()) throw TemplateException("currentPath can't read: $htzPath")
        val model: ModelHtz = File(currentPath, TEMPLATE_CFG)
            .let { ModelHtzReader(it.inputStream()).use { it.model() } }
        val glareFile = File(currentPath, model.overlay_file)
        val glareSize: Sizes? = glareFile.bitmapSize()
        val glare: Glare? = glareSize?.let {
            Glare(stringFiles(glareFile), it, Sizes(model.overlay_x, model.overlay_y))
        }
        val coordinate = with(model) {
            listOf(
                screen_x, screen_y,
                screen_x + screen_width, screen_y,
                screen_x, screen_y + screen_height,
                screen_x + screen_width, screen_y + screen_height
            ).map { it.toFloat() }
        }
        return with(model) {
            Template.VersionHtz(
                htzPath,
                author,
                name,
                "Template Htz",
                stringFiles(File(currentPath, template_file)),
                stringFiles(File(currentPath, preview)),
                Sizes(model.template_width, template_height),
                coordinate,
                installedDate,
                glare
            )
        }
    }
}