package template.factory

import common.PathBuilder.stringFiles
import entity.Glare
import entity.Sizes
import template.Template.VersionHtz
import template.TemplateConstants.TEMPLATE_CFG
import template.model.ModelHtz
import java.io.File

class VersionHtzFactory(
    private val htzBaseDir: File,
    private val htzPath: String,
    private val installedDate: Long,
    private val decodeModel: (String) -> ModelHtz,
    private val calculateSizes: (File) -> Sizes?
) : Factory<VersionHtz> {
    override fun newTemplate(): VersionHtz {
        val currentPath = File(htzBaseDir, htzPath)
        check(currentPath.canRead()) { "Can't read: $currentPath" }
        val model = decodeModel(TEMPLATE_CFG)
        val coordinate = model.coordinate ?: model.run {
            check(screen_x > 0 && screen_y > 0 && screen_width > 0 && screen_height > 0) {
                "Invalid screen_.. field, name=$name; id=$htzPath"
            }
            val (width, height) = screen_x + screen_width to screen_y + screen_height
            listOf(screen_x, screen_y, width, screen_y, screen_x, height, width, height)
                .map(Int::toFloat)
        }
        val frame = stringFiles(File(currentPath, model.template_file))
        val preview = model.preview?.let { stringFiles(File(currentPath, it)) } ?: frame
        return VersionHtz(
            id = htzPath,
            author = model.author,
            name = model.name,
            desc = "Template Htz",
            frame = frame,
            preview = preview,
            sizes = Sizes(model.template_width, model.template_height),
            coordinate = coordinate,
            installedDate = installedDate,
            glare = model.run {
                if (overlay_file.isNullOrEmpty()) return@run null
                val glareFile = File(currentPath, overlay_file)
                if (glareFile.canRead()) {
                    calculateSizes(glareFile)?.let { sizes ->
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
