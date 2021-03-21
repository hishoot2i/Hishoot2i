@file:Suppress("SpellCheckingInspection")

package template.model

import kotlinx.serialization.Serializable

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
 *   "template_height":2080,
 * ## ============
 *   "coordinate"     : [ 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999 ],
 * }
 * ```
 **/
@Serializable
data class ModelHtz(
    val name: String,
    val author: String,
    val template_file: String,
    val preview: String? = null,
    val overlay_file: String? = null,
    val overlay_x: Int,
    val overlay_y: Int,
    val screen_width: Int,
    val screen_height: Int,
    val screen_x: Int,
    val screen_y: Int,
    val template_width: Int,
    val template_height: Int,
    val coordinate: List<Float>? = null
)
