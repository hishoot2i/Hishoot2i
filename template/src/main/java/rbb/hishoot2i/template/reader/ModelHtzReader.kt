package rbb.hishoot2i.template.reader

import rbb.hishoot2i.common.ext.isNull
import rbb.hishoot2i.common.ext.readObject
import rbb.hishoot2i.template.TemplateException
import rbb.hishoot2i.template.model.ModelHtz
import java.io.InputStream

/** @see [ModelHtz] */
class ModelHtzReader(inputStream: InputStream) : AbsJsonModelReader<ModelHtz>(inputStream) {
    @Throws(Exception::class)
    override fun model(): ModelHtz {
        val ret = ModelHtz()
        jsonReader.readObject {
            loop@ while (hasNext()) {
                if (isNull) continue@loop
                val tag = nextName()
                when (tag) {
                    "name" -> ret.name = nextString()
                    "author" -> ret.author = nextString()
                    "template_file" -> ret.template_file = nextString()
                    "preview" -> ret.preview = nextString()
                    "overlay_file" -> ret.overlay_file = nextString()
                    "overlay_x" -> ret.overlay_x = nextInt()
                    "overlay_y" -> ret.overlay_y = nextInt()
                    "screen_width" -> ret.screen_width = nextInt()
                    "screen_height" -> ret.screen_height = nextInt()
                    "screen_x" -> ret.screen_x = nextInt()
                    "screen_y" -> ret.screen_y = nextInt()
                    "template_width" -> ret.template_width = nextInt()
                    "template_height" -> ret.template_height = nextInt()
                    else -> skipValue()
                }
            }
        }
        if (ret.isNotValid()) throw TemplateException("NotValid ModelV3")
        return ret
    }
}