package imageloader.coil

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import coil.bitmap.BitmapPool
import coil.request.ImageRequest
import coil.size.Size
import coil.transform.Transformation
import coil.util.CoilUtils
import coil.util.DebugLogger
import common.ext.graphics.isLandScape
import common.ext.graphics.rotate
import entity.Sizes
import imageloader.ImageLoader
import kotlinx.coroutines.runBlocking
import okhttp3.Cache
import okhttp3.OkHttpClient
import coil.ImageLoader as coilImageLoader

class CoilImageLoaderImpl(
    private val context: Context,
    isDebugLog: Boolean
) : ImageLoader {
    private val diskCache: Cache? = try {
        CoilUtils.createDefaultCache(context)
    } catch (_: Exception) {
        null
    }
    private val impl: coilImageLoader by lazy {
        val builder = coilImageLoader.Builder(context)
            .componentRegistry {
                add(InAppResMapper)
                add(TemplateAppMapper(context))
            }
        if (isDebugLog) builder.logger(DebugLogger())
        if (diskCache != null) {
            builder.okHttpClient {
                OkHttpClient.Builder()
                    .cache(diskCache)
                    .build()
            }
        }
        builder.build()
    }

    override fun display(
        imageView: ImageView,
        source: String,
        reqSizes: Sizes
    ) {
        val request = ImageRequest.Builder(context)
            .data(source)
            .target(imageView)
            .build()
        impl.enqueue(request)
    }

    // FIXME: refactor a lot here and there [ MixTemplate and CoreProcess ]!!
    override fun loadSync(
        source: String,
        isSave: Boolean,
        reqSizes: Sizes,
        isOrientationAware: Boolean
    ): Bitmap? {
        var ret: Bitmap? = null // ?
        val orientationTrans = object : Transformation {
            override fun key(): String = "orientationTrans-$isOrientationAware"
            override suspend fun transform(pool: BitmapPool, input: Bitmap, size: Size): Bitmap =
                if (isOrientationAware && input.isLandScape) input.rotate() else input
        }
        val request = ImageRequest.Builder(context)
            .allowHardware(!isOrientationAware) // ?
            .data(source)
            .transformations(orientationTrans)
            .target {
                val (width, height) = reqSizes
                ret = it.toBitmap(width, height)
            }
            .build()
        runBlocking { impl.execute(request) } // <--
        return ret
    }

    override fun clearMemoryCache() {
        impl.memoryCache.clear()
    }

    override fun clearDiskCache() {
        diskCache?.delete()
    }

    override fun totalDiskCacheSize(): Long = diskCache?.size() ?: 0L
}
