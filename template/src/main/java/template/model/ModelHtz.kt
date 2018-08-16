package template.model

/*
// loc: template.cfg
{
	"name":"Sample Htz",
	"author":"fb.com/ratabb",
	"template_file":"frame_sample.png",
	"preview":"preview_sample.jpg",
	"overlay_file":"overlay_sample.png",
	"overlay_x":148,
	"overlay_y":206,
	"screen_width":720,
	"screen_height":1280,
	"screen_x":200,
	"screen_y":300,
	"template_width":1120,
	"template_height":2080
}
*/
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
    fun isNotValid(): Boolean = when {
        name == "" -> true
        author == "" -> true
        template_file == "" -> true
        preview == "" -> true
        overlay_file == "" -> true
        overlay_x == -1 -> true
        overlay_y == -1 -> true
        screen_width == -1 -> true
        screen_height == -1 -> true
        screen_x == -1 -> true
        screen_y == -1 -> true
        template_width == -1 -> true
        template_height == -1 -> true
        else -> false
    }
}