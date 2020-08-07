package template.reader

import android.util.JsonReader
import common.ext.isNull
import common.ext.nextFloat
import common.ext.nextStringSafe
import common.ext.readArrayAsList
import common.ext.readObject
import java.io.InputStream

/** @see [template.model.ModelV3] */
class ModelV3Reader(
    inputStream: InputStream
) : AbsJsonModelReader<template.model.ModelV3>(inputStream) {
    @Throws(Exception::class)
    override fun model(): template.model.ModelV3 {
        val ret = template.model.ModelV3()
        jsonReader.readObject {
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
        }
        return if (ret.isNotValid()) throw IllegalStateException("NotValid ModelV3") else ret
    }

    // ////////////////////////////////////////////////////////////////////////////////////////
    private fun JsonReader.readGlare(): entity.Glare? {
        var name: String? = null
        var sizes: entity.Sizes = entity.Sizes.ZERO
        var position: entity.Sizes = entity.Sizes.ZERO
        readObject {
            while (hasNext()) {
                when (nextName()) {
                    "name" -> name = nextString()
                    "size" -> sizes = readSizes()
                    "position" -> position = readSizes()
                    else -> skipValue()
                }
            }
        }
        return name?.let { entity.Glare(it, sizes, position) }
    }

    private fun JsonReader.readSizes(): entity.Sizes {
        var (x, y) = entity.Sizes.ZERO
        readObject {
            while (hasNext()) {
                when (nextName()) {
                    "x", "width" -> x = nextInt()
                    "y", "height" -> y = nextInt()
                    else -> skipValue()
                }
            }
        }
        return entity.Sizes(x, y)
    }
}
