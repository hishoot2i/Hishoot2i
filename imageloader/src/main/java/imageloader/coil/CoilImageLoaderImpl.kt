package imageloader.coil

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LifecycleOwner
import coil.request.ImageRequest.Builder
import coil.size.PixelSize
import coil.size.Precision.EXACT
import coil.size.Precision.INEXACT
import coil.transform.Transformation
import coil.util.CoilUtils
import coil.util.DebugLogger
import common.custombitmap.CheckerBoardDrawable
import common.ext.dp2px
import entity.ImageOption
import entity.ImageOption.CENTER_CROP
import entity.ImageOption.MANUAL_CROP
import entity.ImageOption.SCALE_FILL
import entity.Sizes
import imageloader.ImageLoader
import imageloader.coil.fetch.Checker
import imageloader.coil.fetch.CheckerFetcher
import imageloader.coil.map.InAppResMapper
import imageloader.coil.map.TemplateAppMapper
import imageloader.coil.transform.BlurTransformation
import imageloader.coil.transform.OrientationAwareTransformation
import imageloader.coil.transform.ScaleCenterTransformation
import okhttp3.Cache
import okhttp3.OkHttpClient
import kotlin.math.roundToInt
import coil.ImageLoader as CoilImageLoader

class CoilImageLoaderImpl(
    private val context: Context,
    isDebugLog: Boolean
) : ImageLoader {
    private val diskCache: Cache? = try {
        CoilUtils.createDefaultCache(context)
    } catch (_: Exception) {
        null
    }
    private val impl: CoilImageLoader by lazy {
        val builder = CoilImageLoader.Builder(context)
            .componentRegistry {
                add(InAppResMapper)
                add(TemplateAppMapper(context))
                add(CheckerFetcher())
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

    private val placeholderForDisplay by lazy {
        CheckerBoardDrawable(context.dp2px(12F).roundToInt())
    }

    override fun displayCrop(view: ImageView, lifecycleOwner: LifecycleOwner, source: String) {
        impl.enqueue(
            Builder(context).data(source)
                .allowHardware(false)
                .lifecycle(lifecycleOwner)
                .target(view)
                .build()
        )
    }

    override fun display(view: ImageView, lifecycleOwner: LifecycleOwner?, source: String) {
        impl.enqueue(
            Builder(context).data(source)
                .allowHardware(true)
                .crossfade(true)
                .placeholder(placeholderForDisplay)
                .lifecycle(lifecycleOwner)
                .target(view)
                .build()
        )
    }

    override suspend fun loadScreen(source: String?, reqSizes: Sizes): Bitmap? {
        var ret: Bitmap? = null
        val (width, height) = reqSizes
        impl.execute(
            Builder(context).data(source ?: Checker.SCREEN)
                .allowHardware(false)
                .target { ret = it.toBitmap(width, height) }
                .size(PixelSize(width, height))
                .precision(EXACT)
                .transformations(OrientationAwareTransformation)
                .build()
        )
        return ret
    }

    override suspend fun loadBackground(
        source: String?,
        reqSizes: Sizes,
        imageOption: ImageOption,
        blurEnable: Boolean,
        blurRadius: Int
    ): Bitmap? {
        var ret: Bitmap? = null
        val (width, height) = reqSizes
        val precision = when (imageOption) {
            SCALE_FILL, MANUAL_CROP -> EXACT
            CENTER_CROP -> INEXACT
        }
        var transf = emptyArray<Transformation>()
        if (imageOption == CENTER_CROP) transf += ScaleCenterTransformation(reqSizes)
        if (blurEnable) transf += BlurTransformation(blurRadius)
        impl.execute(
            Builder(context).data(source ?: Checker.BACKGROUND)
                .allowHardware(false)
                .target { ret = it.toBitmap(width, height) }
                .size(PixelSize(width, height))
                .precision(precision)
                .transformations(*transf)
                .build()
        )
        return ret
    }

    override suspend fun loadAssetsTemplate(source: String, reqSizes: Sizes): Bitmap? {
        var ret: Bitmap? = null // ?
        val (width, height) = reqSizes
        impl.execute(
            Builder(context).data(source)
                .allowHardware(false)
                .target { ret = it.toBitmap(width, height) }
                .size(PixelSize(width, height))
                .precision(EXACT)
                .build()
        )
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
