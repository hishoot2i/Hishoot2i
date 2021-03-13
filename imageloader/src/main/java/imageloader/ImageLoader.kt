package imageloader

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import entity.ImageOption
import entity.Sizes

interface ImageLoader {
    fun displayCrop(
        view: ImageView,
        lifecycleOwner: LifecycleOwner,
        source: String
    )

    fun display(
        view: ImageView,
        lifecycleOwner: LifecycleOwner?,
        source: String
    )

    suspend fun loadScreen(
        source: String?,
        reqSizes: Sizes
    ): Bitmap?

    suspend fun loadBackground(
        source: String?,
        reqSizes: Sizes,
        imageOption: ImageOption,
        blurEnable: Boolean,
        blurRadius: Int
    ): Bitmap?

    suspend fun loadAssetsTemplate(
        source: String,
        reqSizes: Sizes
    ): Bitmap?

    fun clearMemoryCache()
    fun clearDiskCache()
    fun totalDiskCacheSize(): Long
}
