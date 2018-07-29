package org.illegaller.ratabb.hishoot2i.data.core

import android.content.Context
import android.graphics.Bitmap
import android.support.v4.content.FileProvider
import io.reactivex.Single
import org.illegaller.ratabb.hishoot2i.BuildConfig.FILE_AUTHORITY
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.data.CacheFileTypefaces
import org.illegaller.ratabb.hishoot2i.data.pref.AppPref
import rbb.hishoot2i.common.FileConstants
import rbb.hishoot2i.common.custombitmap.BadgeBitmap
import rbb.hishoot2i.common.egl.MaxTextureCompat
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
import rbb.hishoot2i.common.ext.graphics.resizeIfNotEqual
import rbb.hishoot2i.common.ext.graphics.saveTo
import rbb.hishoot2i.common.ext.graphics.scaleCenterCrop
import rbb.hishoot2i.common.ext.graphics.sizes
import rbb.hishoot2i.common.ext.toDateTimeFormat
import rbb.hishoot2i.common.imageloader.ImageLoader
import rbb.hishoot2i.template.MixTemplate
import rbb.hishoot2i.template.Template
import java.io.File
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

class CoreProcessImpl @Inject constructor(
    val context: Context,
    val appPref: AppPref,
    val mixTemplate: MixTemplate,
    imageLoader: ImageLoader,
    fileConstants: FileConstants
) : CoreProcess,
    FileConstants by fileConstants,
    ImageLoader by imageLoader {
    /**
     * NOTE: scaled based on device can handle maximum texture.
     * @see [singleBase] and [singleMixingTemplate]
     */
    private var isScaled = false
    private val maxTextureSize: Int? by lazy(NONE) {
        /* if (DEBUG) 2048 // ...
         else*/ MaxTextureCompat.get()
    }
    private val badgeBitmap: BadgeBitmap by lazy(NONE) { BadgeBitmap(context) }
    //  private val mixTemplate: MixTemplate by lazy(NONE) { MixTemplate(context, this) }
    private val optMixTemplate: MixTemplate.Options
        get() = with(appPref) {
            MixTemplate.Options(templateFrameEnable, templateShadowEnable, templateGlareEnable)
        }

    /**/
    override fun preview(template: Template, sourcePath: ImageSourcePath): Single<Result.Preview> =
        core(template, sourcePath, isSave = false).flatMap { bitmap: Bitmap ->
            Single.fromCallable { Result.Preview(bitmap) }
        }

    /**/
    override fun save(template: Template, sourcePath: ImageSourcePath): Single<Result.Save> =
        core(template, sourcePath, isSave = true).flatMap { bitmap: Bitmap ->
            Single.fromCallable {
                val file = System.currentTimeMillis().toDateTimeFormat(SAVE_FILE_TIMESTAMP_PATTERN)
                    .let { timestamp: String -> File(savedDir(), "HiShoot_$timestamp.png") }
                bitmap.saveTo(file)
                val uri = FileProvider.getUriForFile(context, FILE_AUTHORITY, file)
                Result.Save(bitmap, uri)
            }
        }

    private fun core(
        template: Template,
        sourcePath: ImageSourcePath,
        isSave: Boolean
    ): Single<Bitmap> = singleBase(template)
        .flatMap { singleBackground(it, sourcePath.background, isSave) }
        .flatMap { singleMixingTemplate(it, template, sourcePath, isSave) }
        .flatMap(::singleBadgeBitmap)

    private fun singleBase(template: Template): Single<Bitmap> = Single.fromCallable {
        var baseSize: Sizes = template.sizes.let {
            if (appPref.doubleScreenEnable) it * Sizes(2, 1)
            else it
        }
        isScaled = false
        maxTextureSize?.let { max: Int ->
            if (baseSize.x > max || baseSize.y > max) {
                isScaled = true
                baseSize = baseSize.max(max)
            }
        }
        baseSize.createBitmap(Bitmap.Config.ARGB_8888)
    }

    private fun singleBackground(
        bitmap: Bitmap,
        backgroundPath: String?,
        isSave: Boolean
    ): Single<Bitmap> = Single.fromCallable {
        val backgroundMode = BackgroundMode.fromId(appPref.backgroundModeId)
        bitmap.applyCanvas {
            when (backgroundMode) {
                is BackgroundMode.Color -> drawColor(appPref.backgroundColorInt)
                is BackgroundMode.Image -> {
                    val background: Bitmap = backgroundPath.backgroundImage(isSave, bitmap.sizes)
                    when (appPref.backgroundImageBlurEnable) {
                        true -> drawBitmapBlur(background, appPref.backgroundImageBlurRadius)
                        false -> drawBitmapSafely(background)
                    }
                }
                is BackgroundMode.Transparent -> {
                } // Do nothing.
            }.exhaustive
        }
    }

    private fun singleMixingTemplate(
        bitmap: Bitmap,
        template: Template,
        sourcePath: ImageSourcePath,
        isSave: Boolean
    ): Single<Bitmap> = Single.fromCallable {
        val localOptions = optMixTemplate
        bitmap.applyCanvas {
            mixTemplate.mix(template, localOptions, sourcePath.screen1, isSave)
                .let { mixed: Bitmap ->
                    if (isScaled) mixed.resizeIfNotEqual(mixed.sizes.max(bitmap.sizes))
                    else mixed
                }
                .also { drawBitmapSafely(it) }
            //
            if (appPref.doubleScreenEnable) {
                mixTemplate.mix(template, localOptions, sourcePath.screen2, isSave)
                    .let { mixed: Bitmap ->
                        if (isScaled) mixed.resizeIfNotEqual(mixed.sizes.max(bitmap.sizes))
                        else mixed
                    }
                    .also {
                        translate(it.sizes.toSizeF().x, 0f) //
                        drawBitmapSafely(it)
                    }
            }
        }
    }

    private fun singleBadgeBitmap(bitmap: Bitmap): Single<Bitmap> = when (appPref.badgeEnable) {
        false -> Single.just(bitmap)
        true -> Single.fromCallable {
            val bb = with(appPref) {
                val typeface = CacheFileTypefaces.getOrDefault(badgeTypefacePath)
                BadgeBitmap.Config(badgeText, typeface, badgeSize, badgeColor)
            }.let { badgeBitmap.create(it) }
            val padding = Math.round(context.dp2px(10))
            val (left, top) = BadgePosition.fromId(appPref.badgePositionId)
                .apply {
                    total = bitmap.sizes
                    source = bb.sizes
                }
                .getPosition(padding)
            bitmap.applyCanvas { drawBitmapSafely(bb, left, top) }
        }
    }

    private fun String?.backgroundImage(isSave: Boolean, sizes: Sizes): Bitmap = this?.let {
        loadSync(it, isSave)?.apply {
            return@let when (appPref.backgroundImageOptionId) {
                R.id.toolBackgroundImageOptionScaleFill,
                R.id.toolBackgroundImageOptionManualCrop -> resizeIfNotEqual(sizes)
                R.id.toolBackgroundImageOptionCenterCrop -> scaleCenterCrop(sizes)
                else -> this //
            }
        }
    } ?: context.alphaPatternBitmap(sizes)

    companion object {
        private const val SAVE_FILE_TIMESTAMP_PATTERN = "yyyyMMdd_HHmmss"
    }
}