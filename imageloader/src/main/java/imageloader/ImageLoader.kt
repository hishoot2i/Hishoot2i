package imageloader

import android.graphics.Bitmap
import android.widget.ImageView

interface ImageLoader {
    fun display(
        imageView: ImageView,
        source: String,
        reqSizes: entity.Sizes
    )

    fun loadSync(
        source: String,
        isSave: Boolean,
        reqSizes: entity.Sizes,
        isOrientationAware: Boolean
    ): Bitmap?

    fun clearMemoryCache()
    fun clearDiskCache()
    fun totalDiskCacheSize(): Long
}
