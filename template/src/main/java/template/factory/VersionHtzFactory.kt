package template.factory

import common.PathBuilder.stringFiles
import common.ext.graphics.bitmapSize
import entity.Glare
import entity.Sizes
import template.Template.VersionHtz
import template.TemplateConstants.TEMPLATE_CFG
import template.reader.ModelHtzReader
import java.io.File

class VersionHtzFactory(
    private val htzBaseDir: File,
    private val htzPath: String,
    private val installedDate: Long
) : Factory<VersionHtz> {
    @Throws(Exception::class)
    override fun newTemplate(): VersionHtz {
        val currentPath = File(htzBaseDir, htzPath)
        check(currentPath.canRead()) { "Can't read: $currentPath" }
        val model = File(currentPath, TEMPLATE_CFG)
            .let { ModelHtzReader(it.inputStream()).use(ModelHtzReader::model) }
        return VersionHtz(
            id = htzPath,
            author = model.author,
            name = model.name,
            desc = "Template Htz",
            frame = stringFiles(File(currentPath, model.template_file)),
            preview = stringFiles(File(currentPath, model.preview)),
            sizes = Sizes(model.template_width, model.template_height),
            coordinate = model.run {
                val (width, height) = screen_x + screen_width to screen_y + screen_height
                listOf(screen_x, screen_y, width, screen_y, screen_x, height, width, height)
                    .map(Int::toFloat)
            },
            installedDate = installedDate,
            glare = model.run {
                val glareFile = File(currentPath, overlay_file)
                if (glareFile.canRead()) {
                    glareFile.bitmapSize()?.let { sizes ->
                        Glare(
                            stringFiles(glareFile),
                            sizes,
                            Sizes(overlay_x, overlay_y).toSizeF()
                        )
                    }
                } else null
            }
        )
    }
}
