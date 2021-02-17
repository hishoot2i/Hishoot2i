package template.reader

import android.util.JsonReader
import androidx.annotation.WorkerThread
import common.ext.isNull
import common.ext.readObject
import template.model.ModelHtz
import java.io.InputStream

/** @see [ModelHtz] */
@WorkerThread
class ModelHtzReader(inputStream: InputStream) : BaseModelReader<ModelHtz>(inputStream) {
    private val jsonReader by lazy { JsonReader(this) }

    override fun model(): ModelHtz {
        val ret = ModelHtz()
        jsonReader.readObject {
            loop@ while (hasNext()) {
                if (isNull) continue@loop
                when (nextName()) {
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
        check(!ret.isNotValid()) { "Not valid model: $ret" }
        return ret
    }
}
