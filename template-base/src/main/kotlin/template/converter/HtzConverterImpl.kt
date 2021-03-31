package template.converter

import entity.Glare
import template.Template
import template.Template.Version1
import template.Template.Version2
import template.Template.Version3
import template.TemplateConstants.TEMPLATE_CFG
import template.model.ModelHtz
import java.io.File
import java.io.InputStream

class HtzConverterImpl(
    private val htzDir: () -> File,
    private val encodeModelHtz: (ModelHtz) -> String,
    private val assetTemplate: (String, String) -> InputStream
) : HtzConverter {

    override fun convert(template: Template): String {
        check(template is Version1 || template is Version2 || template is Version3) {
            "Can not convert: ${template.id}"
        }
        val model = template.toModelHtz()
        val newHtzId = model.newHtzId
        val pathTemplate = File(htzDir(), newHtzId).apply {
            require(!exists()) { "Already converted: ${template.id}?" }
            mkdirs()
        }
        try {
            File(pathTemplate, TEMPLATE_CFG).writeText(encodeModelHtz(model))

            copyAsset(template.id, pathTemplate, model.template_file) //
            // Version1 not have preview file, not need to copy it.
            if (template !is Version1) copyAsset(template.id, pathTemplate, model.preview)
            copyAsset(template.id, pathTemplate, model.overlay_file) //
        } catch (reThrow: Exception) {
            pathTemplate.deleteRecursively() // revert
            throw reThrow
        }
        return newHtzId
    }

    private fun copyAsset(templateId: String, pathTemplate: File, assetName: String?) {
        if (assetName == null) return
        assetTemplate(templateId, assetName).use { input ->
            File(pathTemplate, assetName).outputStream().use { input.copyTo(it) }
        }
    }

    /**
     * When changing this method,
     * make sure change logic [template.factory.VersionHtzFactory]
     **/
    private fun Template.toModelHtz(): ModelHtz {
        val frameName = frame.substringAfterLast("/")
        // Version1 not have preview, use frame?
        val previewName = if (this is Version1) null else preview.substringAfterLast("/")

        fun overlayOrElse(glare: Glare?, fallback: Triple<String?, Float, Float>) = when (glare) {
            null -> fallback
            else -> Triple(glare.name.substringAfterLast("/"), glare.position.x, glare.position.y)
        }
        // Version1 not have glare
        // Version2 glare = optional
        // Version3 glares = optional, only take 1st glare if exist and ignore shadow?
        val fallback = Triple(null, -1F, -1F)
        val (overlayName, overlayX, overlayY) = when (this) {
            is Version2 -> overlayOrElse(glare, fallback)
            is Version3 -> overlayOrElse(glares?.firstOrNull(), fallback)
            else -> fallback
        }
        return ModelHtz(
            name = "[Htz] $name", // added prefix.
            author = author,
            template_file = frameName,
            preview = previewName,
            overlay_file = overlayName,
            overlay_x = overlayX.toInt(),
            overlay_y = overlayY.toInt(),
            screen_width = -1, //
            screen_height = -1, //
            screen_x = -1, //
            screen_y = -1, //
            template_width = sizes.x,
            template_height = sizes.y,
            // NOTE: Converted VersionHtz must be ignore screen_... fields,
            // copy coordinate from previous Template directly.
            coordinate = coordinate
        )
    }
}
