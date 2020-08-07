package imageloader

import android.graphics.Bitmap
import android.widget.ImageView

interface ImageLoader {
    fun display(
        imageView: ImageView,
        source: String,
        reqSizes: entity.Sizes? = null
    )

    fun loadSync(
        source: String,
        isSave: Boolean,
        reqSizes: entity.Sizes
    ): Bitmap?

    fun clearMemoryCache()
    fun clearDiskCache()
    fun totalDiskCacheSize(): Long
}
