package template.reader

import common.ext.isNull
import common.ext.readObject
import template.model.ModelV2
import java.io.InputStream

/** @see [template.model.ModelV2] */
class ModelV2Reader(
    inputStream: InputStream
) : AbsJsonModelReader<ModelV2>(inputStream) {
    @Throws(Exception::class)
    override fun model(): ModelV2 {
        val ret = ModelV2()
        jsonReader.readObject {
            loop@ while (hasNext()) {
                if (isNull) continue@loop
                when (nextName()) {
                    "name" -> ret.name = nextString()
                    "author" -> ret.author = nextString()
                    "left_top_x" -> ret.left_top_x = nextInt()
                    "left_top_y" -> ret.left_top_y = nextInt()
                    "right_top_x" -> ret.right_top_x = nextInt()
                    "right_top_y" -> ret.right_top_y = nextInt()
                    "left_bottom_x" -> ret.left_bottom_x = nextInt()
                    "left_bottom_y" -> ret.left_bottom_y = nextInt()
                    "right_bottom_x" -> ret.right_bottom_x = nextInt()
                    "right_bottom_y" -> ret.right_bottom_y = nextInt()
                    "template_width" -> ret.template_width = nextInt()
                    "template_height" -> ret.template_height = nextInt()
                    else -> skipValue()
                }
            }
        }
        return ret.takeUnless { it.isNotValid() }
            ?: throw IllegalStateException("NotValid Model $ret")
    }
}
