package template.converter

import android.content.Context
import common.ext.openRawResource
import common.ext.resourcesFrom
import template.Template
import template.Template.Version1
import template.Template.Version2
import template.Template.Version3
import template.TemplateConstants.TEMPLATE_CFG
import template.model.ModelHtz
import java.io.File
import java.io.InputStream

internal class HtzConverterImpl(
    context: Context,
    private val htzDir: () -> File,
    private val encodeModelHtz: (ModelHtz) -> String,
) : HtzConverter {

    private val assetTemplate: (String, String) -> InputStream = { id, asset ->
        context.resourcesFrom(id).openRawResource(asset, "drawable", id)
            ?: throw IllegalStateException("Failed open $asset from $id")
    }

    override fun convert(template: Template, generatorHtzId: ModelHtz.() -> String): String {
        check(template is Version1 || template is Version2 || template is Version3) {
            "Can not convert: ${template.id}"
        }
        val model = template.toModelHtz()
        val newHtzId = generatorHtzId(model)
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

    private fun Template.toModelHtz(): ModelHtz {
        val frameName = frame.substringAfterLast("/", "")
        val previewName: String? =
            if (this is Version1) null else preview.substringAfterLast("/", "")
        var overlayName: String? = null // Version1 not have this.
        var overlayX = -1
        var overlayY = -1
        (this as? Version2)?.glare?.let {
            overlayName = it.name.substringAfterLast("/", "")
            overlayX = it.position.x.toInt()
            overlayY = it.position.y.toInt()
        }
        (this as? Version3)?.glares?.get(0)?.let { // NOTE: only take 1 glare and ignore shadow?
            overlayName = it.name.substringAfterLast("/", "")
            overlayX = it.position.x.toInt()
            overlayY = it.position.y.toInt()
        }
        return ModelHtz(
            name = "[Htz] $name", // added prefix.
            author = author,
            template_file = frameName,
            preview = previewName,
            overlay_file = overlayName,
            overlay_x = overlayX,
            overlay_y = overlayY,
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
