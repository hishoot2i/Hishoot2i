package rbb.hishoot2i.common.imageloader.uil

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
import com.nostra13.universalimageloader.utils.StorageUtils
import rbb.hishoot2i.common.FileConstants
import rbb.hishoot2i.common.PathBuilder
import rbb.hishoot2i.common.entity.Sizes
import rbb.hishoot2i.common.imageloader.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoader as UilImageLoader

// TODO:
class UilImageLoaderImpl(appContext: Context, isLogging: Boolean) : ImageLoader {
    private val uilImageLoader: UilImageLoader by lazy(this) {
        val instance = UilImageLoader.getInstance()
        if (!instance.isInited) {
            val diskCache = initializeDiskCache(appContext)
            val configBuilder = ImageLoaderConfiguration.Builder(appContext)
                .imageDownloader(TemplateImageDownloader(appContext))
                .imageDecoder(BaseImageDecoder(isLogging))
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .threadPriority(Thread.NORM_PRIORITY)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                // MemoryCache
                .denyCacheImageMultipleSizesInMemory()
                .memoryCacheSizePercentage(MEMORY_CACHE_PERCENT)
            // DiskCache
            diskCache?.let { configBuilder.diskCache(it) }
            if (isLogging) configBuilder.writeDebugLogs()
            instance.init(configBuilder.build())
        }
        instance
    }

    override fun display(imageView: ImageView, source: String, reqSizes: Sizes?) {
        val imageSize = reqSizes?.let { ImageSize(it.x, it.y) }
        val options = displayImageOptions(
            source,
            false,
            ImageScaleType.IN_SAMPLE_POWER_OF_2,
            Bitmap.Config.RGB_565
        )
        uilImageLoader.displayImage(
            source,
            ImageViewAware(imageView),
            options,
            imageSize,
            null,
            null
        )
    }

    override fun loadSync(source: String, isSave: Boolean, reqSizes: Sizes): Bitmap? {
        val imageSize = with(reqSizes) { ImageSize(x, y) }
        val imageScaleType = if (isSave) ImageScaleType.NONE else ImageScaleType.NONE_SAFE
        val options = displayImageOptions(source, isSave, imageScaleType)
        return uilImageLoader.loadImageSync(source, imageSize, options)
    }

    override fun clearMemoryCache() {
        uilImageLoader.clearMemoryCache()
    }

    override fun clearDiskCache() {
        uilImageLoader.diskCache?.clear()
    }

    /** @use
     *  [android.text.format.Formatter.formatFileSize] or
     *  [android.text.format.Formatter.formatShortFileSize]
     * */
    override fun totalDiskCacheSize(): Long {
        var ret = uilImageLoader.diskCache?.directory?.length() ?: 0L
        try {
            uilImageLoader.diskCache?.directory?.listFiles()
                ?.forEach { ret += it.length() }
        } catch (ignore: Exception) { //
        }
        return ret
    }

    private fun displayImageOptions(
        source: String,
        isSave: Boolean,
        imageScaleType: ImageScaleType,
        config: Bitmap.Config = Bitmap.Config.ARGB_8888
    ): DisplayImageOptions = DisplayImageOptions.Builder()
        .considerExifParams(false)
        .imageScaleType(imageScaleType)
        .decodingOptions(decodingOptions(isSave, config, source))
        .cacheInMemory(isCacheOnMemory(isSave, source))
        .cacheOnDisk(isCacheOnDisk(isSave, source))
        .build()

    /*cache to memory if not save and not background crop*/
    private fun isCacheOnMemory(isSave: Boolean, source: String): Boolean =
        !isSave && !source.endsWith(suffix = FileConstants.BG_CROP)

    /*cache to disk if isCacheOnMemory and template asset only*/
    private fun isCacheOnDisk(isSave: Boolean, source: String): Boolean =
        isCacheOnMemory(isSave, source) && source.startsWith(PathBuilder.TEMPLATE_APP)

    private fun decodingOptions(
        isSave: Boolean,
        config: Bitmap.Config,
        source: String
    ): BitmapFactory.Options {
        return BitmapFactory.Options().apply {
            inScaled = !isSave
            inPreferredConfig = config
            uilImageLoader.memoryCache?.get(source)?.let { inBitmap = it }
        }
    }

    private fun initializeDiskCache(context: Context): LruDiskCache? = try {
        StorageUtils.getCacheDirectory(context)
            .let { LruDiskCache(it, DISK_CACHE_FILENAME_GEN, DISK_CACHE_SIZE) }
    } catch (ignore: Exception) {
        null
    }

    companion object {
        private const val MEMORY_CACHE_PERCENT = 70
        private const val DISK_CACHE_SIZE: Long = 50 * 1024 * 1024 // 50MB
        private val DISK_CACHE_FILENAME_GEN = HashCodeFileNameGenerator()
    }
}