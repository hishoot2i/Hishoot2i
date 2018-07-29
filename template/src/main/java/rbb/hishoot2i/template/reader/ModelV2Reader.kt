package rbb.hishoot2i.template.reader

import rbb.hishoot2i.common.ext.isNull
import rbb.hishoot2i.template.TemplateException
import rbb.hishoot2i.template.model.ModelV2
import java.io.InputStream

/** @see [ModelV2] */
class ModelV2Reader(inputStream: InputStream) : AbsJsonModelReader<ModelV2>(inputStream) {
    @Throws(Exception::class)
    override fun model(): ModelV2 {
        val ret = ModelV2()
        with(jsonReader) {
            beginObject()
            loop@ while (hasNext()) {
                if (isNull) continue@loop
                val tag = nextName()
                when (tag) {
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
            endObject()
        }
        return when {
            ret.isNotValid() -> throw TemplateException("NotValid ModelV2")
            else -> ret
        }
    }
}