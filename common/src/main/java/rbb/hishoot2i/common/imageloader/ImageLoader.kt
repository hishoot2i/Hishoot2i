package rbb.hishoot2i.common.imageloader

import android.graphics.Bitmap
import android.widget.ImageView
import rbb.hishoot2i.common.entity.Sizes

interface ImageLoader {
    /** Displaying [Bitmap] from [source] asynchronous to [imageView]. */
    fun display(imageView: ImageView, source: String)

    /** Return mutable [Bitmap] synchronously from [source]. */
    fun loadSync(source: String, isSave: Boolean): Bitmap?

    /** Return mutable [Bitmap] synchronously from [source] with *optional* request size */
    fun loadSync(source: String, reqSizes: Sizes?, isSave: Boolean): Bitmap?

    /** */
    fun clearMemoryCache()

    /** */
    fun clearDiskCache()

    fun totalDiskCacheSize(): Long
}