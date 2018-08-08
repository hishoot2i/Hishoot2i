package org.illegaller.ratabb.hishoot2i.data.core

import android.content.Context
import android.graphics.Bitmap
import io.reactivex.Single
import org.illegaller.ratabb.hishoot2i.BuildConfig.DEBUG
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.data.CacheFileTypefaces
import org.illegaller.ratabb.hishoot2i.data.pref.AppPref
import rbb.hishoot2i.common.FileConstants
import rbb.hishoot2i.common.custombitmap.BadgeBitmapBuilder
import rbb.hishoot2i.common.egl.MaxTexture
import rbb.hishoot2i.common.entity.BackgroundMode
import rbb.hishoot2i.common.entity.BadgePosition
import rbb.hishoot2i.common.entity.ImageSourcePath
import rbb.hishoot2i.common.entity.Sizes
import rbb.hishoot2i.common.ext.dp2px
import rbb.hishoot2i.common.ext.exhaustive
import rbb.hishoot2i.common.ext.graphics.alphaPatternBitmap
import rbb.hishoot2i.common.ext.graphics.applyCanvas
import rbb.hishoot2i.common.ext.graphics.createBitmap
import rbb.hishoot2i.common.ext.graphics.drawBitmapBlur
import rbb.hishoot2i.common.ext.graphics.drawBitmapSafely
import rbb.hishoot2i.common.ext.graphics.recycleSafely
import rbb.hishoot2i.common.ext.graphics.resizeIfNotEqual
import rbb.hishoot2i.common.ext.graphics.saveTo
import rbb.hishoot2i.common.ext.graphics.scaleCenterCrop
import rbb.hishoot2i.common.ext.graphics.sizes
import rbb.hishoot2i.common.ext.toDateTimeFormat
import rbb.hishoot2i.common.imageloader.ImageLoader
import rbb.hishoot2i.template.Template
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE
import java.lang.System.currentTimeMillis as now

class CoreProcessImpl @Inject constructor(
    val context: Context,
    val appPref: AppPref,
    imageLoader: ImageLoader,
    fileConstants: FileConstants,
    maxTexture: MaxTexture
) : CoreProcess,
    FileConstants by fileConstants,
    ImageLoader by imageLoader {
    private val backgroundMode get() = BackgroundMode.fromId(appPref.backgroundModeId)
    private val badgeBitmapBuilder by lazy(NONE) { BadgeBitmapBuilder(context) }
    private val badgeBitmapConfig
        get() = with(appPref) {
            CacheFileTypefaces.getOrDefault(badgeTypefacePath).let {
                BadgeBitmapBuilder.Config(badgeText, it, badgeSize, badgeColor)
            }
        }
    private val badgeBitmapPadding by lazy(NONE) { Math.round(context.dp2px(10)) }
    private val badgePosition get() = BadgePosition.fromId(appPref.badgePositionId)
    private val isART by lazy(NONE) {
        System.getProperty("java.vm.version", "")
            .let { it.isNotEmpty() && it[0].toInt() >= 2 }
    }
    private val isDoubleScreen get() = appPref.doubleScreenEnable
    /**
     * Device maximum texture.
     * @see [singleResizePreview]
     * NOTE: [DEBUG] hard-code value, cause not relevant value from emulator. */
    private val maxTextureSize: Int? by lazy(NONE) { if (DEBUG) 2048 else maxTexture.get() }
    private val mixTemplate by lazy(NONE) { MixTemplate(context, appPref, imageLoader) }
    override fun preview(template: Template, sourcePath: ImageSourcePath): Single<Result> =
        template.core(sourcePath, isSave = false).flatMap { it.singleResizePreview() }
            .map { Result.Preview(it) }

    override fun save(template: Template, sourcePath: ImageSourcePath): Single<Result> =
        template.core(sourcePath, isSave = true).flatMap { bitmap: Bitmap ->
            Single.fromCallable { now().toDateTimeFormat("yyyyMMdd_HHmmss") }
                .flatMap { timeStamp ->
                    Single.fromCallable {
                        val file = File(savedDir(), "HiShoot_$timeStamp.png")
                        bitmap.saveTo(file) //
                        file
                    }
                }
                .flatMap { Single.fromCallable { it.toUri() } }
                .map { Result.Save(bitmap, it) }
        }

    private fun Template.core(path: ImageSourcePath, isSave: Boolean): Single<Bitmap> = singleBase()
        .flatMap { it.singleBackground(path.background, isSave) }
        .flatMap { it.singleMixing(this, path, isSave) }
        .flatMap { it.singleBadgeBitmap() }

    private fun Template.singleBase(): Single<Bitmap> = Single.fromCallable {
        if (!isART) System.gc()
        sizes.let { if (isDoubleScreen) it * Sizes(2, 1) else it }
            .createBitmap(Bitmap.Config.ARGB_8888)
    }

    private fun Bitmap.singleBackground(path: String?, isSave: Boolean): Single<Bitmap> =
        Single.fromCallable {
            applyCanvas {
                when (backgroundMode) {
                    is BackgroundMode.Color -> drawColor(appPref.backgroundColorInt)
                    is BackgroundMode.Image -> {
                        path.backgroundImage(isSave, sizes).let {
                            when (appPref.backgroundImageBlurEnable) {
                                true -> drawBitmapBlur(it, appPref.backgroundImageBlurRadius)
                                false -> drawBitmapSafely(it)
                            }
                            it.recycleSafely()
                        }
                    }
                    is BackgroundMode.Transparent -> {
                    } // Do nothing.
                }.exhaustive
            }
        }

    private fun Bitmap.singleMixing(
        template: Template,
        path: ImageSourcePath,
        isSave: Boolean
    ): Single<Bitmap> = Single.fromCallable {
        applyCanvas {
            var mixed = mixTemplate.mixed(template, path.screen1, isSave)
            drawBitmapSafely(mixed)
            if (isDoubleScreen) {
                mixed = mixTemplate.mixed(template, path.screen2, isSave)
                drawBitmapSafely(mixed, left = mixed.width.toFloat())
            }
            mixed.recycleSafely()
        }
    }

    private fun Bitmap.singleBadgeBitmap(): Single<Bitmap> = if (!appPref.badgeEnable) {
        Single.just(this)
    } else {
        Single.fromCallable {
            val badgeBitmap = badgeBitmapBuilder.build(badgeBitmapConfig)
            val (left, top) = badgePosition.apply { total = sizes; source = badgeBitmap.sizes }
                .getPosition(badgeBitmapPadding)
            applyCanvas {
                drawBitmapSafely(badgeBitmap, left, top)
                badgeBitmap.recycleSafely()
            }
        }
    }

    private fun Bitmap.singleResizePreview(): Single<Bitmap> = Single.fromCallable {
        maxTextureSize?.let {
            if (width > it || height > it) {
                Timber.d("maxTextureSize")
                resizeIfNotEqual(sizes.max(it))
            } else this
        } ?: this
    }

    private fun String?.backgroundImage(isSave: Boolean, reqSize: Sizes): Bitmap = this?.let {
        loadSync(it, isSave, reqSize)?.apply {
            return@let when (appPref.backgroundImageOptionId) {
                R.id.toolBackgroundImageOptionScaleFill,
                R.id.toolBackgroundImageOptionManualCrop -> resizeIfNotEqual(reqSize)
                R.id.toolBackgroundImageOptionCenterCrop -> scaleCenterCrop(reqSize)
                else -> this //
            }
        }
    } ?: context.alphaPatternBitmap(reqSize)
}