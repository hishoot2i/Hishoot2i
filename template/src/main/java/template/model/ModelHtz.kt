@file:Suppress("SpellCheckingInspection")

package template.model

import java.util.Locale

/**
 * // loc: template.cfg
 *```
 * {
 *   "name"           :"Sample Htz",
 *   "author"         :"fb.com/ratabb",
 *   "template_file"  :"frame_sample.png",
 *   "preview"        :"preview_sample.jpg",
 *   "overlay_file"   :"overlay_sample.png",
 *   "overlay_x"      :148,
 *   "overlay_y"      :206,
 *   "screen_width"   :720,
 *   "screen_height"  :1280,
 *   "screen_x"       :200,
 *   "screen_y"       :300,
 *   "template_width" :1120,
 *   "template_height":2080
 * }
 * ```
 **/
data class ModelHtz(
    var name: String = "",
    var author: String = "",
    var template_file: String = "",
    var preview: String = "",
    var overlay_file: String = "",
    var overlay_x: Int = -1,
    var overlay_y: Int = -1,
    var screen_width: Int = -1,
    var screen_height: Int = -1,
    var screen_x: Int = -1,
    var screen_y: Int = -1,
    var template_width: Int = -1,
    var template_height: Int = -1
) {
    fun isNotValid(): Boolean = name == "" || author == "" || template_file == "" ||
        preview == "" || overlay_file == "" || overlay_x == -1 || overlay_y == -1 ||
        screen_width == -1 || screen_height == -1 || screen_x == -1 || screen_y == -1 ||
        template_width == -1 || template_height == -1

    fun generateTemplateId(): String {
        val ret = "${author.hashCode()}_${name.toLowerCase(Locale.ROOT)}"
            .replace("[^\\w]".toRegex(), replacement = "") // removing non word char
            .trim()
        val maxLengthID = 32
        return ret.takeIf { it.length <= maxLengthID }
            ?: ret.substring(range = 0..maxLengthID) // limit
    }
}
