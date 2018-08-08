package rbb.hishoot2i.common.imageloader

import android.graphics.Bitmap
import android.widget.ImageView
import rbb.hishoot2i.common.entity.Sizes

interface ImageLoader {
    fun display(imageView: ImageView, source: String, reqSizes: Sizes? = null)
    fun loadSync(source: String, isSave: Boolean, reqSizes: Sizes): Bitmap?
    fun clearMemoryCache()
    fun clearDiskCache()
    fun totalDiskCacheSize(): Long
}