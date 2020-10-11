package imageloader.uil

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.core.assist.ImageSize
import com.nostra13.universalimageloader.core.assist.QueueProcessingType
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware
import com.nostra13.universalimageloader.utils.StorageUtils.getCacheDirectory
import common.FileConstants.Companion.BG_CROP
import common.PathBuilder.TEMPLATE_APP
import common.ext.graphics.isLandScape
import common.ext.graphics.rotate
import entity.Sizes
import imageloader.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoader as UilImageLoader

class UilImageLoaderImpl constructor(context: Context) : ImageLoader {
    private var isLogging: Boolean = false //
    private val diskCache: LruDiskCache? by lazy {
        try {
            val diskCacheSize: Long = 50 * 1024 * 1024 // 50MB
            LruDiskCache(getCacheDirectory(context), HashCodeFileNameGenerator(), diskCacheSize)
        } catch (ignore: Exception) {
            null
        }
    }
    private val uilImageLoader: UilImageLoader by lazy {
        val instance = UilImageLoader.getInstance()
        if (!instance.isInited) {
            val builder = ImageLoaderConfiguration.Builder(context)
                .imageDownloader(TemplateImageDownloader(context))
                .imageDecoder(BaseImageDecoder(isLogging))
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .threadPriority(Thread.NORM_PRIORITY)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                // MemoryCache
                .denyCacheImageMultipleSizesInMemory()
                .memoryCacheSizePercentage(85) // 85% maxMemory app
            // DiskCache
            diskCache?.let { builder.diskCache(it) }
            if (isLogging) builder.writeDebugLogs()
            instance.init(builder.build())
        }
        instance
    }

    override fun display(imageView: ImageView, source: String, reqSizes: Sizes) {
        source.displayImageOptions(
            isSave = false,
            scaleType = ImageScaleType.IN_SAMPLE_POWER_OF_2,
            config = Bitmap.Config.RGB_565
        ).let {
            uilImageLoader.displayImage(
                source,
                imageView.wrapAware(),
                it,
                reqSizes.toImageSize(),
                null,
                null
            )
        }
    }

    override fun loadSync(
        source: String,
        isSave: Boolean,
        reqSizes: Sizes,
        isOrientationAware: Boolean
    ): Bitmap? = source.displayImageOptions(isSave, ImageScaleType.NONE, Bitmap.Config.ARGB_8888)
        .let { options ->
            uilImageLoader.loadImageSync(source, reqSizes.toImageSize(), options)?.let { bitmap ->
                if (isOrientationAware && bitmap.isLandScape) bitmap.rotate() else bitmap
            }
        }

    override fun clearMemoryCache() {
        uilImageLoader.clearMemoryCache()
    }

    override fun clearDiskCache() {
        diskCache?.clear()
    }

    /** @use
     *  [android.text.format.Formatter.formatFileSize] or
     *  [android.text.format.Formatter.formatShortFileSize]
     * */
    override fun totalDiskCacheSize(): Long {
        var ret = 0L
        try {
            diskCache?.directory?.listFiles()
                ?.forEach { ret += it.length() }
        } catch (ignore: Exception) { //
        }
        return ret
    }

    private fun String.displayImageOptions(
        isSave: Boolean,
        scaleType: ImageScaleType,
        config: Bitmap.Config
    ): DisplayImageOptions = DisplayImageOptions.Builder()
        .considerExifParams(false)
        .imageScaleType(scaleType)
        .decodingOptions(decodingOptions(isSave, config))
        .cacheInMemory(isCacheOnMemory(isSave))
        .cacheOnDisk(isCacheOnDisk(isSave))
        .build()

    /* cache to memory if not save and not background crop */
    private fun String.isCacheOnMemory(isSave: Boolean): Boolean =
        !isSave && !endsWith(suffix = BG_CROP)

    /* cache to disk if isCacheOnMemory and template asset only */
    private fun String.isCacheOnDisk(isSave: Boolean): Boolean = diskCache != null &&
        isCacheOnMemory(isSave) && startsWith(TEMPLATE_APP)

    private fun String.decodingOptions(
        isSave: Boolean,
        config: Bitmap.Config
    ): BitmapFactory.Options = BitmapFactory.Options().apply {
        inScaled = !isSave
        inPreferredConfig = config
        uilImageLoader.memoryCache?.get(this@decodingOptions)?.let { inBitmap = it }
    }

    private fun Sizes.toImageSize(): ImageSize = this.run { ImageSize(x, y) }
    private fun ImageView.wrapAware(): ImageViewAware = ImageViewAware(this)
}
