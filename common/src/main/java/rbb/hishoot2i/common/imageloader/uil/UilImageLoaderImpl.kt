package rbb.hishoot2i.common.imageloader.uil

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.nostra13.universalimageloader.cache.disc.DiskCache
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator
import com.nostra13.universalimageloader.cache.memory.MemoryCache
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.core.assist.QueueProcessingType
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder
import com.nostra13.universalimageloader.utils.StorageUtils
import rbb.hishoot2i.common.FileConstants
import rbb.hishoot2i.common.PathBuilder
import rbb.hishoot2i.common.entity.Sizes
import rbb.hishoot2i.common.ext.deviceHeight
import rbb.hishoot2i.common.ext.deviceWidth
import rbb.hishoot2i.common.ext.graphics.resizeIfNotEqual
import rbb.hishoot2i.common.imageloader.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoader as UilImageLoader

// TODO:
class UilImageLoaderImpl(context: Context, isLogging: Boolean) : ImageLoader {
    private val memoryCache: MemoryCache
    private var diskCache: DiskCache? = null

    init {
        memoryCache = LruMemoryCache(memoryCacheSize)
        diskCache = try {
            LruDiskCache(
                StorageUtils.getCacheDirectory(context),
                HashCodeFileNameGenerator(),
                DISK_CACHE_SIZE
            )
        } catch (ignore: Exception) {
            null
        }
        val (width, height) = Sizes(context.deviceWidth, context.deviceHeight)
        val config = ImageLoaderConfiguration.Builder(context)
            .imageDownloader(TemplateImageDownloader(context))
            .imageDecoder(BaseImageDecoder(isLogging))
            .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
            .threadPriority(Thread.NORM_PRIORITY)
            .tasksProcessingOrder(QueueProcessingType.FIFO)
            // / MemoryCache
            .memoryCache(memoryCache)
            .denyCacheImageMultipleSizesInMemory()
            .memoryCacheExtraOptions(width, height)
        // / DiskCache
        diskCache?.let {
            config.diskCache(it)
                .diskCacheExtraOptions(width, height, null)
        }
        if (isLogging) config.writeDebugLogs()
        UilImageLoader.getInstance().init(config.build())
    }

    override fun display(imageView: ImageView, source: String) {
        UilImageLoader.getInstance()
            .displayImage(
                source,
                imageView,
                displayImageOptions(source, false)
            )
    }

    override fun loadSync(source: String, isSave: Boolean): Bitmap? =
        loadSync(source, null, isSave)

    override fun loadSync(source: String, reqSizes: Sizes?, isSave: Boolean): Bitmap? {
        var bitmap: Bitmap? = UilImageLoader.getInstance()
            .loadImageSync(source, displayImageOptions(source, isSave))
        reqSizes?.let { sizes: Sizes ->
            // FIXME: how to handle this ?
            bitmap = bitmap?.resizeIfNotEqual(sizes)
        }
        return bitmap
    }

    override fun clearMemoryCache() {
        memoryCache.clear()
    }

    override fun clearDiskCache() {
        diskCache?.clear()
    }

    /** @use
     *  [android.text.format.Formatter.formatFileSize] or
     *  [android.text.format.Formatter.formatShortFileSize]
     * */
    override fun totalDiskCacheSize(): Long {
        var ret = diskCache?.directory?.length() ?: 0L
        try {
            diskCache?.directory?.listFiles()?.forEach {
                ret += it.length()
            }
        } catch (ignore: Exception) { //
        }
        return ret
    }

    private fun displayImageOptions(source: String, isSave: Boolean): DisplayImageOptions {
        val cacheBitmap: Bitmap? = memoryCache[source]
        val options = BitmapFactory.Options().apply {
            inScaled = !isSave
            cacheBitmap?.let { inBitmap = it }
        }
        val bitmapConfig: Bitmap.Config = if (isSave) Bitmap.Config.ARGB_8888
        else Bitmap.Config.RGB_565
        val imageScaleType = if (isSave) ImageScaleType.NONE_SAFE
        else ImageScaleType.IN_SAMPLE_POWER_OF_2
        /*cache to memory if not save and not background crop*/
        val isCacheInMemory = !isSave && !source.endsWith(suffix = FileConstants.BG_CROP)
        /*cache to disk if isCacheMemory and template asset only*/
        val isCacheOnDisk = isCacheInMemory && source.startsWith(PathBuilder.TEMPLATE_APP)
        return DisplayImageOptions.Builder()
            .cloneFrom(DisplayImageOptions.createSimple())
            .considerExifParams(false)
            .decodingOptions(options)
            .bitmapConfig(bitmapConfig)
            .imageScaleType(imageScaleType)
            .cacheInMemory(isCacheInMemory)
            .cacheOnDisk(isCacheOnDisk)
            .build()
    }

    companion object {
        private const val MEMORY_CACHE_FACTOR = .75F
        private const val DISK_CACHE_SIZE: Long = 50 * 1024 * 1024 // 50MB
        private val memoryCacheSize = Math.round(
            MEMORY_CACHE_FACTOR * Runtime.getRuntime().maxMemory() / 1024
        )
    }
}