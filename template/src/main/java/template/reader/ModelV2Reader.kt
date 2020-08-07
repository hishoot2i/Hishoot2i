package template.reader

import common.ext.isNull
import common.ext.readObject
import java.io.InputStream

/** @see [template.model.ModelV2] */
class ModelV2Reader(
    inputStream: InputStream
) : AbsJsonModelReader<template.model.ModelV2>(inputStream) {
    @Throws(Exception::class)
    override fun model(): template.model.ModelV2 {
        val ret = template.model.ModelV2()
        jsonReader.readObject {
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
        }
        return if (ret.isNotValid()) throw IllegalStateException("NotValid ModelV2") else ret
    }
}
