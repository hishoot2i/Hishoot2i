package org.illegaller.ratabb.hishoot2i.data.core

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.support.v4.content.FileProvider
import io.reactivex.Single
import org.illegaller.ratabb.hishoot2i.BuildConfig
import org.illegaller.ratabb.hishoot2i.BuildConfig.FILE_AUTHORITY
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.data.CacheFileTypefaces
import org.illegaller.ratabb.hishoot2i.data.pref.AppPref
import rbb.hishoot2i.common.FileConstants
import rbb.hishoot2i.common.custombitmap.BadgeBitmapBuilder
import rbb.hishoot2i.common.egl.MaxTextureCompat
import rbb.hishoot2i.common.entity.BackgroundMode
import rbb.hishoot2i.common.entity.BadgePosition
import rbb.hishoot2i.common.entity.ImageSourcePath
import rbb.hishoot2i.common.entity.Sizes
import rbb.hishoot2i.common.ext.deviceHeight
import rbb.hishoot2i.common.ext.deviceWidth
import rbb.hishoot2i.common.ext.dp2px
import rbb.hishoot2i.common.ext.exhaustive
import rbb.hishoot2i.common.ext.graphics.alphaPatternBitmap
import rbb.hishoot2i.common.ext.graphics.applyCanvas
import rbb.hishoot2i.common.ext.graphics.createBitmap
import rbb.hishoot2i.common.ext.graphics.drawBitmapBlur
import rbb.hishoot2i.common.ext.graphics.drawBitmapPerspective
import rbb.hishoot2i.common.ext.graphics.drawBitmapSafely
import rbb.hishoot2i.common.ext.graphics.drawable
import rbb.hishoot2i.common.ext.graphics.recycleSafely
import rbb.hishoot2i.common.ext.graphics.resizeIfNotEqual
import rbb.hishoot2i.common.ext.graphics.saveTo
import rbb.hishoot2i.common.ext.graphics.scaleCenterCrop
import rbb.hishoot2i.common.ext.graphics.sizes
import rbb.hishoot2i.common.ext.graphics.toBitmap
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
    fileConstants: FileConstants
) : CoreProcess,
    FileConstants by fileConstants,
    ImageLoader by imageLoader {
    private val backgroundMode get() = BackgroundMode.fromId(appPref.backgroundModeId)
    private val badgeBitmapBuilder: BadgeBitmapBuilder by lazy(NONE) { BadgeBitmapBuilder(context) }
    private val deviceSizes: Sizes by lazy(NONE) {
        Sizes(context.deviceWidth, context.deviceHeight)
    }
    private val isART: Boolean by lazy(NONE) {
        System.getProperty("java.vm.version", "")
            .let { it.isNotEmpty() && it[0].toInt() >= 2 }
    }
    private val maxTextureSize: Int? by lazy(NONE) {
        if (BuildConfig.DEBUG) 2048 // ...
        else MaxTextureCompat.get()
    }
    private val paddingBadgeBitmap: Int by lazy(NONE) { Math.round(context.dp2px(10)) }
    override fun preview(template: Template, sourcePath: ImageSourcePath): Single<Result> =
        template.core(sourcePath, false).flatMap { it.singleResizePreview() }
            .map { Result.Preview(it) }

    override fun save(template: Template, sourcePath: ImageSourcePath): Single<Result> =
        template.core(sourcePath, true).flatMap { bitmap: Bitmap ->
            Single.fromCallable {
                val timeStamp = now().toDateTimeFormat("yyyyMMdd_HHmmss")
                File(savedDir(), "HiShoot_$timeStamp.png").also { bitmap.saveTo(it) }
            }
                .map { FileProvider.getUriForFile(context, FILE_AUTHORITY, it) }
                .map { Result.Save(bitmap, it) }
        }

    private fun Template.core(path: ImageSourcePath, isSave: Boolean): Single<Bitmap> = singleBase()
        .flatMap { it.singleBackground(path.background, isSave) }
        .flatMap { it.singleMixingTemplate(this, path, isSave) }
        .flatMap { it.singleBadgeBitmap() }

    private fun Template.singleBase(): Single<Bitmap> = Single.fromCallable {
        if (!isART) System.gc()
        sizes.let {
            when (appPref.doubleScreenEnable) {
                true -> it * Sizes(2, 1)
                false -> it
            }
        }.createBitmap(Bitmap.Config.ARGB_8888)
    }

    private fun Bitmap.singleBackground(backgroundPath: String?, isSave: Boolean): Single<Bitmap> =
        Single.fromCallable {
            applyCanvas {
                when (backgroundMode) {
                    is BackgroundMode.Color -> drawColor(appPref.backgroundColorInt)
                    is BackgroundMode.Image -> {
                        backgroundPath.backgroundImage(isSave, sizes).let {
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

    private fun Bitmap.singleMixingTemplate(
        template: Template,
        path: ImageSourcePath,
        isSave: Boolean
    ): Single<Bitmap> = Single.fromCallable {
        applyCanvas {
            var mixed = template.mixIt(isSave, path.screen1)
            drawBitmapSafely(mixed)
            if (appPref.doubleScreenEnable) {
                translate(mixed.width.toFloat(), 0F)
                mixed = template.mixIt(isSave, path.screen2)
                drawBitmapSafely(mixed)
            }
            mixed.recycleSafely()
        }
    }

    private fun Bitmap.singleBadgeBitmap(): Single<Bitmap> = when (appPref.badgeEnable) {
        false -> Single.just(this)
        true -> Single.fromCallable {
            val badgeBitmap = with(appPref) {
                val typeface = CacheFileTypefaces.getOrDefault(badgeTypefacePath)
                BadgeBitmapBuilder.Config(badgeText, typeface, badgeSize, badgeColor)
            }.let { badgeBitmapBuilder.build(it) }
            val totalSizes = sizes
            val (left, top) = BadgePosition.fromId(appPref.badgePositionId)
                .apply { total = totalSizes; source = badgeBitmap.sizes }
                .getPosition(paddingBadgeBitmap)
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

    private fun String?.screenShootImage(isSave: Boolean): Bitmap = this?.let {
        loadSync(it, isSave, deviceSizes)
    } ?: context.alphaPatternBitmap(deviceSizes)

    private fun Template.mixIt(isSave: Boolean, ss: String?): Bitmap = sizes.createBitmap().let {
        val coordinate = coordinateNormalize(it.sizes)
        when (this) {
            is Template.Default -> it.drawDefault(ss, coordinate, isSave)
            is Template.Version1 -> it.drawVersion1(ss, coordinate, isSave, this)
            is Template.Version2 -> it.drawVersion2(ss, coordinate, isSave, this)
            is Template.Version3 -> it.drawVersion3(ss, coordinate, isSave, this)
            is Template.VersionHtz -> it.drawVersionHtz(ss, coordinate, isSave, this)
            is Template.Empty -> throw IllegalStateException("Unknown $id")
        }.exhaustive
    }

    private fun Template.coordinateNormalize(reqSizes: Sizes): FloatArray {
        var ret = coordinate
        if (reqSizes != sizes) {
            val (x, y) = reqSizes.toSizeF() / sizes.toSizeF()
            ret = ret.toMutableList().apply {
                forEachIndexed { i, value ->
                    val factor = if (i % 2 == 0) x else y
                    set(i, value * factor)
                }
            }
        }
        return ret.toFloatArray()
    }

    private fun Bitmap.drawDefault(ss: String?, coordinate: FloatArray, isSave: Boolean): Bitmap =
        applyCanvas {
            drawBitmapPerspective(ss.screenShootImage(isSave), coordinate)
            context.drawable(R.drawable.frame1)
                ?.toBitmap(width, height)
                ?.let { drawBitmapSafely(it); it.recycleSafely() }
        }

    private fun Bitmap.drawVersion1(
        ss: String?,
        coordinate: FloatArray,
        isSave: Boolean,
        template: Template.Version1
    ): Bitmap = applyCanvas {
        drawBitmapPerspective(ss.screenShootImage(isSave), coordinate)
        drawAssetTemplate(template.frame, isSave, sizes)
    }

    private fun Bitmap.drawVersion2(
        ss: String?,
        coordinate: FloatArray,
        isSave: Boolean,
        template: Template.Version2
    ): Bitmap = applyCanvas {
        if (appPref.templateShadowEnable) {
            drawAssetTemplate(template.shadow, isSave, sizes)
        }
        if (appPref.templateFrameEnable) drawAssetTemplate(template.frame, isSave, sizes)
        drawBitmapPerspective(ss.screenShootImage(isSave), coordinate)
        if (appPref.templateGlareEnable) {
            template.glare?.let { drawAssetTemplate(it.name, isSave, it.size, it.position) }
        }
    }

    private fun Bitmap.drawVersion3(
        ss: String?,
        coordinate: FloatArray,
        isSave: Boolean,
        template: Template.Version3
    ): Bitmap = applyCanvas {
        if (appPref.templateShadowEnable) template.shadow?.let {
            drawAssetTemplate(it, isSave, sizes)
        }
        if (appPref.templateFrameEnable) drawAssetTemplate(template.frame, isSave, sizes)
        drawBitmapPerspective(ss.screenShootImage(isSave), coordinate)
        if (appPref.templateGlareEnable) {
            template.glares?.forEach { drawAssetTemplate(it.name, isSave, it.size, it.position) }
        }
    }

    private fun Bitmap.drawVersionHtz(
        ss: String?,
        coordinate: FloatArray,
        isSave: Boolean,
        template: Template.VersionHtz
    ): Bitmap = applyCanvas {
        drawAssetTemplate(template.frame, isSave, sizes)
        drawBitmapPerspective(ss.screenShootImage(isSave), coordinate)
        template.glare?.let { drawAssetTemplate(it.name, isSave, it.size, it.position) }
    }

    private fun Canvas.drawAssetTemplate(
        source: String,
        isSave: Boolean,
        sizes: Sizes,
        position: Sizes = Sizes.ZERO
    ) {
        loadSync(source, isSave, sizes)?.let {
            val (left, top) = position.toSizeF()
            drawBitmapSafely(it.resizeIfNotEqual(sizes), left, top)
            it.recycleSafely()
        }
    }
}