package template.model

import entity.Glare
import entity.Sizes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * // loc: assets/template.cfg
 *```
 *{
 *  "author"       : "template-author-name",
 *  "name"         : "template-name",
 *  "desc"         : "template-description",
 *  "coordinate"   : [ 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999 ],
 *  "template_size": { "width": 9999, "height": 9999 },
 *  "preview"      : "preview-filename-without-extension",
 *  "frame"        : "frame-filename-without-extension",
 *  "shadow"       : "shadow-filename-without-extension",
 *  "glares"       :
 *  [
 *     {
 *        "name"     : "glare-1-filename-without-extension",
 *        "size"     : { "width": 9999, "height": 9999 },
 *        "position" : { "x": 9999, "y": 9999 }
 *     },
 *     {
 *        "name"     : "glare-2-filename-without-extension",
 *        "size"     : { "width": 9999, "height": 9999 },
 *        "position" : { "x": 9999, "y": 9999 }
 *     },
 *     {
 *        "name"     : "glare-N-filename-without-extension",
 *        "size"     : { "width": 9999, "height": 9999 },
 *        "position" : { "x": 9999, "y": 9999 }
 *     }
 *  ]
 *}
 *```
 **/
@Serializable
data class ModelV3(
    val name: String,
    val author: String,
    val desc: String?,
    val frame: String,
    val preview: String?,
    val shadow: String?,
    val coordinate: List<Float>,
    @SerialName("template_size") val size: Sizes,
    val glares: List<Glare>?
)
