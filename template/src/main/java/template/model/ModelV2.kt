@file:Suppress("SpellCheckingInspection")

package template.model

import kotlinx.serialization.Serializable

/**
 * // loc: assets/template.cfg
 *```
 * {
 *   "name"           :"Ipin69 Miring",
 *   "author"         :"Bsod",
 *   "left_top_x"     :394,
 *   "left_top_y"     :242,
 *   "right_top_x"    :960,
 *   "right_top_y"    :199,
 *   "left_bottom_x"  :807,
 *   "left_bottom_y"  :1149,
 *   "right_bottom_x" :1443,
 *   "right_bottom_y" :964,
 *   "template_width" :1856,
 *   "template_height":1456
 * }
 *```
 **/
@Serializable
data class ModelV2(
    val name: String,
    val author: String,
    val left_top_x: Int,
    val left_top_y: Int,
    val right_top_x: Int,
    val right_top_y: Int,
    val left_bottom_x: Int,
    val left_bottom_y: Int,
    val right_bottom_x: Int,
    val right_bottom_y: Int,
    val template_width: Int,
    val template_height: Int
)
