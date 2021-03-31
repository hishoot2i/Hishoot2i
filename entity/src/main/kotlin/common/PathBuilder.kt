@file:Suppress("MemberVisibilityCanBePrivate")

package common

import java.io.File

object PathBuilder {
    const val FILES = "file://"
    const val TEMPLATE_APP = "template_app://"
    const val DRAWABLES = "drawable://"
    const val SEPARATOR = "/"

    /**
     * Image path from template app.
     *
     * @param templateId packageName app template.
     * @param drawableName drawable name from app template resources.
     * @return [String] ** template_app://`templateId/drawableName` **
     */
    @JvmStatic
    fun stringTemplateApp(templateId: String, drawableName: String): String =
        "$TEMPLATE_APP$templateId$SEPARATOR$drawableName"

    /**
     * Image path from default template.
     *
     * @param drawableRes drawable resources identity.
     * @return [String] ** drawable://`drawableRes` **
     */
    @JvmStatic
    fun stringDrawables(drawableRes: Int): String = "$DRAWABLES$drawableRes"

    /**
     * Image path from storage
     *
     * @param file image file
     * @return [String] ** file://`absolute-path-of-file` **
     */
    @JvmStatic
    fun stringFiles(file: File): String = "$FILES${file.absolutePath}"
}
