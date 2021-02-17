package template.reader

import android.util.JsonReader
import androidx.annotation.WorkerThread
import common.ext.isNull
import common.ext.nextFloat
import common.ext.nextStringSafe
import common.ext.readArrayAsList
import common.ext.readObject
import entity.Glare
import entity.Sizes
import entity.SizesF
import template.model.ModelV3
import java.io.InputStream

/** @see [ModelV3] */
@WorkerThread
class ModelV3Reader(inputStream: InputStream) : BaseModelReader<ModelV3>(inputStream) {
    private val jsonReader by lazy { JsonReader(this) }

    override fun model(): ModelV3 {
        val ret = ModelV3()
        jsonReader.readObject {
            loop@ while (hasNext()) {
                if (isNull) continue@loop
                when (nextName()) {
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
        }
        check(!ret.isNotValid()) { "Not valid model: $ret" }
        return ret
    }

    // ////////////////////////////////////////////////////////////////////////////////////////
    private fun JsonReader.readGlare(): Glare? {
        var name: String? = null
        var sizes: Sizes = Sizes.ZERO
        var position: SizesF = SizesF.ZERO
        readObject {
            while (hasNext()) {
                when (nextName()) {
                    "name" -> name = nextString()
                    "size" -> sizes = readSizes()
                    "position" -> position = readSizes().toSizeF()
                    else -> skipValue()
                }
            }
        }
        return name?.let { Glare(it, sizes, position) }
    }

    private fun JsonReader.readSizes(): Sizes {
        var (x, y) = Sizes.ZERO
        readObject {
            while (hasNext()) {
                when (nextName()) {
                    "x", "width" -> x = nextInt()
                    "y", "height" -> y = nextInt()
                    else -> skipValue()
                }
            }
        }
        return Sizes(x, y)
    }
}
