package org.illegaller.ratabb.hishoot2i.data

import android.graphics.Typeface
import android.support.v4.util.LruCache
import rbb.hishoot2i.common.ext.lruCache
import kotlin.LazyThreadSafetyMode.NONE

object CacheFileTypefaces {
    private const val MAX_CACHE = 20
    private val DEFAULT = Typeface.DEFAULT
    private val cache: LruCache<String, Typeface> by lazy(NONE) {
        lruCache(
            maxSize = MAX_CACHE,
            create = { absolutePath: String ->
                try {
                    Typeface.createFromFile(absolutePath)
                } catch (ignore: Exception) {
                    DEFAULT
                }
            })
    }

    @JvmStatic
    fun put(absolutePath: String): Boolean = try {
        cache.put(absolutePath, Typeface.createFromFile(absolutePath))
        true
    } catch (ignore: Exception) {
        false
    }

    @JvmStatic
    fun getOrDefault(absolutePath: String): Typeface = cache[absolutePath] ?: DEFAULT
}