package rbb.hishoot2i.common.ext

import android.support.v4.util.LruCache

inline fun <K : Any, V : Any> lruCache(
    maxSize: Int,
    crossinline sizeOf: (key: K, value: V) -> Int = { _: K, _: V -> 1 },
    @Suppress("USELESS_CAST")
    crossinline create: (key: K) -> V? = { null as V? },
    crossinline onEntryRemoved: (evicted: Boolean, key: K, oldValue: V, newValue: V?) -> Unit =
        { _: Boolean, _: K, _: V, _: V? -> }
): LruCache<K, V> = object : LruCache<K, V>(maxSize) {
    override fun sizeOf(key: K, value: V): Int = sizeOf(key, value)
    override fun create(key: K): V? = create(key)
    override fun entryRemoved(evicted: Boolean, key: K, oldValue: V, newValue: V) {
        onEntryRemoved(evicted, key, oldValue, newValue)
    }
}