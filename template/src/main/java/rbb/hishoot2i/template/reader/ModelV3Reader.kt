package rbb.hishoot2i.template.reader

import android.util.JsonReader
import rbb.hishoot2i.common.entity.Glare
import rbb.hishoot2i.common.entity.Sizes
import rbb.hishoot2i.common.ext.isNull
import rbb.hishoot2i.common.ext.nextFloat
import rbb.hishoot2i.common.ext.nextStringSafe
import rbb.hishoot2i.common.ext.readArrayAsList
import rbb.hishoot2i.template.TemplateException
import rbb.hishoot2i.template.model.ModelV3
import java.io.InputStream

/** @see [ModelV3] */
class ModelV3Reader(inputStream: InputStream) : AbsJsonModelReader<ModelV3>(inputStream) {
    @Throws(Exception::class)
    override fun model(): ModelV3 {
        val ret = ModelV3()
        with(jsonReader) {
            beginObject()
            loop@ while (hasNext()) {
                if (isNull) continue@loop
                val tag = nextName()
                when (tag) {
                    "name" -> ret.name = nextString()
                    "author" -> ret.author = nextString()
                    "desc" -> ret.desc = nextStringSafe()
                    "frame" -> ret.frame = nextString()
                    "preview" -> ret.preview = nextStringSafe()
                    "shadow" -> ret.shadow = nextStringSafe() //
                    "coordinate" -> ret.coordinate = readArrayAsList { nextFloat() }
                    "glares" -> ret.glares = readArrayAsList { readGlare() }
                    "template_size" -> ret.size = readSizes()
                    else -> skipValue()
                }
            }
            endObject()
        }
        return when {
            ret.isNotValid() -> throw TemplateException("NotValid ModelV3")
            else -> ret
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////////
    private fun JsonReader.readGlare(): Glare? {
        var name: String? = null
        var sizes: Sizes = Sizes.ZERO
        var position: Sizes = Sizes.ZERO
        beginObject()
        while (hasNext()) {
            when (nextName()) {
                "name" -> name = nextString()
                "size" -> sizes = readSizes()
                "position" -> position = readSizes()
                else -> skipValue()
            }
        }
        endObject()
        return name?.let { Glare(it, sizes, position) }
    }

    private fun JsonReader.readSizes(): Sizes {
        var (x, y) = Sizes.ZERO
        beginObject()
        while (hasNext()) {
            when (nextName()) {
                "x", "width" -> x = nextInt()
                "y", "height" -> y = nextInt()
                else -> skipValue()
            }
        }
        endObject()
        return Sizes(x, y)
    }
}