package rbb.hishoot2i.common.imageloader

import android.graphics.Bitmap
import android.widget.ImageView
import rbb.hishoot2i.common.entity.Sizes

interface ImageLoader {
    /** Displaying [Bitmap] from [source] asynchronous to [imageView]. */
    fun display(imageView: ImageView, source: String, reqSizes: Sizes? = null)

    /** Return mutable [Bitmap] synchronously from [source] with *optional* request size */
    fun loadSync(source: String, isSave: Boolean, reqSizes: Sizes): Bitmap?

    /** */
    fun clearMemoryCache()

    /** */
    fun clearDiskCache()

    fun totalDiskCacheSize(): Long
}