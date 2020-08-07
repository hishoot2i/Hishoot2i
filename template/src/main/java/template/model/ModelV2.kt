package template.model

/*
// loc: assets/template.cfg
{
	"name":"Ipin69 Miring",
	"author":"Bsod",
	"left_top_x":394,
	"left_top_y":242,
	"right_top_x":960,
	"right_top_y":199,
	"left_bottom_x":807,
	"left_bottom_y":1149,
	"right_bottom_x":1443,
	"right_bottom_y":964,
	"template_width":1856,
	"template_height":1456
}
*/
data class ModelV2 @JvmOverloads constructor(
    var name: String = "",
    var author: String = "",
    var left_top_x: Int = -1,
    var left_top_y: Int = -1,
    var right_top_x: Int = -1,
    var right_top_y: Int = -1,
    var left_bottom_x: Int = -1,
    var left_bottom_y: Int = -1,
    var right_bottom_x: Int = -1,
    var right_bottom_y: Int = -1,
    var template_width: Int = -1,
    var template_height: Int = -1
) {
    fun isNotValid(): Boolean = when {
        name == "" -> true
        author == "" -> true
        left_top_x == -1 -> true
        left_top_y == -1 -> true
        left_bottom_x == -1 -> true
        left_bottom_y == -1 -> true
        right_top_x == -1 -> true
        right_top_y == -1 -> true
        right_bottom_x == -1 -> true
        right_bottom_y == -1 -> true
        template_width == -1 -> true
        template_height == -1 -> true
        else -> false
    }
}
