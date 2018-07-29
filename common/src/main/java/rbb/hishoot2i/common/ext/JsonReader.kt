@file:Suppress("NOTHING_TO_INLINE")

package rbb.hishoot2i.common.ext

import android.util.JsonReader
import android.util.JsonToken

inline val JsonReader.isNull
    get() = peek() == JsonToken.NULL

inline fun <T> JsonReader.readArrayAsList(block: JsonReader.() -> T?): List<T> {
    val list = mutableListOf<T>()
    beginArray()
    while (hasNext()) {
        block()?.let { list.add(it) }
    }
    endArray()
    return list
}

inline fun JsonReader.nextFloat(): Float = nextInt().toFloat()
inline fun JsonReader.nextStringSafe(): String? = try {
    nextString()
} catch (ignore: Exception) {
    skipValue()
    null
}