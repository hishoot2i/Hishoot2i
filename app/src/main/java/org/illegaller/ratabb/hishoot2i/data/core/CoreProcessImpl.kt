package org.illegaller.ratabb.hishoot2i.data.core

import android.content.Context
import android.graphics.Bitmap
import common.FileConstants
import common.custombitmap.AlphaPatternBitmap
import common.custombitmap.BadgeBitmapBuilder
import common.egl.MaxTexture
import common.ext.dp2px
import common.ext.exhaustive
import common.ext.graphics.applyCanvas
import common.ext.graphics.createBitmap
import common.ext.graphics.drawBitmapBlur
import common.ext.graphics.drawBitmapSafely
import common.ext.graphics.recycleSafely
import common.ext.graphics.resizeIfNotEqual
import common.ext.graphics.saveTo
import common.ext.graphics.scaleCenterCrop
import common.ext.graphics.sizes
import common.ext.isART
import common.ext.toDateTimeFormat
import dagger.hilt.android.qualifiers.ApplicationContext
import imageloader.ImageLoader
import io.reactivex.Single
import org.illegaller.ratabb.hishoot2i.BuildConfig.DEBUG
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.data.pref.AppPref
import template.Template
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE
import kotlin.math.roundToInt
import java.lang.System.currentTimeMillis as now

class CoreProcessImpl @Inject constructor(
    @ApplicationContext context: Context,
    val appPref: AppPref,
    imageLoader: ImageLoader,
    fileConstants: FileConstants,
    maxTexture: MaxTexture
) : CoreProcess,
    FileConstants by fileConstants,
    ImageLoader by imageLoader {
    private val alphaPatternBitmap by lazy(NONE) { AlphaPatternBitmap(context) }
    private val badgeBitmapBuilder by lazy(NONE) { BadgeBitmapBuilder(context) }
    private val backgroundMode get() = entity.BackgroundMode.fromId(appPref.backgroundModeId)
    private val badgeBitmapConfig
        get() = with(appPref) {
            BadgeBitmapBuilder.Config(badgeText, badgeTypefacePath, badgeSize, badgeColor)
        }
    private val badgeBitmapPadding by lazy(NONE) { context.dp2px(10F).roundToInt() }
    private val badgePosition get() = entity.BadgePosition.fromId(appPref.badgePositionId)
    private val isDoubleScreen get() = appPref.doubleScreenEnable

    /**
     * Device maximum texture.
     * @see [singleResizePreview]
     * NOTE: [DEBUG] hard-code value, cause not relevant value from emulator. */
    private val maxTextureSize: Int? by lazy(NONE) { if (DEBUG) 2048 else maxTexture.get() }
    private val mixTemplate by lazy(NONE) { MixTemplate(appPref, context, imageLoader) }
    override fun preview(template: Template, sourcePath: entity.ImageSourcePath): Single<Result> =
        template.core(sourcePath, isSave = false).flatMap { it.singleResizePreview() }
            .map { Result.Preview(it) }

    override fun save(template: Template, sourcePath: entity.ImageSourcePath): Single<Result> =
        template.core(sourcePath, isSave = true).flatMap { bitmap: Bitmap ->
            Single.fromCallable {
                val timeStamp = now().toDateTimeFormat("yyyyMMdd_HHmmss")
                val file = File(savedDir(), "HiShoot_$timeStamp.png")
                bitmap.saveTo(file) //
                Result.Save(bitmap, file.toUri(), file.nameWithoutExtension)
            }/*.map { Result.Save(bitmap, it) }*/
        }

    private fun Template.core(path: entity.ImageSourcePath, isSave: Boolean): Single<Bitmap> =
        Single.fromCallable {
            if (isART.not()) System.gc()
            Timber.d("isART: $isART")
            sizes.let { if (isDoubleScreen) it * entity.Sizes(2, 1) else it }
                .createBitmap(Bitmap.Config.ARGB_8888)
        }
            .flatMap { it.singleBackground(path.background, isSave) }
            .flatMap { it.singleMixing(this, path, isSave) }
            .flatMap { it.singleBadgeBitmap() }

    private fun Bitmap.singleBackground(path: String?, isSave: Boolean): Single<Bitmap> =
        when (backgroundMode) {
            is entity.BackgroundMode.Transparent -> Single.just(this)
            is entity.BackgroundMode.Color -> Single.fromCallable {
                applyCanvas { drawColor(appPref.backgroundColorInt) }
            }
            is entity.BackgroundMode.Image -> Single.fromCallable {
                applyCanvas {
                    path.backgroundImage(isSave, sizes).let {
                        if (appPref.backgroundImageBlurEnable) {
                            drawBitmapBlur(it, appPref.backgroundImageBlurRadius)
                        } else drawBitmapSafely(it)
                        it.recycleSafely()
                    }
                }
            }.exhaustive
        }

    private fun Bitmap.singleMixing(
        template: Template,
        path: entity.ImageSourcePath,
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

    private fun String?.backgroundImage(isSave: Boolean, reqSize: entity.Sizes): Bitmap =
        this?.let {
            loadSync(it, isSave, reqSize)?.apply {
                return@let when (appPref.backgroundImageOptionId) {
                    R.id.toolBackgroundImageOptionScaleFill,
                    R.id.toolBackgroundImageOptionManualCrop -> resizeIfNotEqual(reqSize)
                    R.id.toolBackgroundImageOptionCenterCrop -> scaleCenterCrop(reqSize)
                    else -> this //
                }
            }
        } ?: alphaPatternBitmap.create(sizes = reqSize)
}
