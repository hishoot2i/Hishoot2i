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
import rbb.hishoot2i.common.custombitmap.BadgeBitmap
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
import rbb.hishoot2i.common.ext.graphics.resizeIfNotEqual
import rbb.hishoot2i.common.ext.graphics.saveTo
import rbb.hishoot2i.common.ext.graphics.scaleCenterCrop
import rbb.hishoot2i.common.ext.graphics.sizes
import rbb.hishoot2i.common.ext.graphics.toBitmap
import rbb.hishoot2i.common.ext.toDateTimeFormat
import rbb.hishoot2i.common.imageloader.ImageLoader
import rbb.hishoot2i.template.Template
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
    private val deviceSizes by lazy { with(context) { Sizes(deviceWidth, deviceHeight) } }
    private val mapSizes = hashMapOf<String, Sizes>()
    //
    private val maxTextureSize: Int? by lazy(NONE) {
        if (BuildConfig.DEBUG) 2048 // ...
        else MaxTextureCompat.get()
    }
    private val badgeBitmap: BadgeBitmap by lazy(NONE) { BadgeBitmap(context) }
    override fun preview(template: Template, sourcePath: ImageSourcePath): Single<Result> =
        template.core(sourcePath, false)
            .map { it.resizePreview() }
            .map { Result.Preview(it) }

    /**/
    override fun save(template: Template, sourcePath: ImageSourcePath): Single<Result> =
        template.core(sourcePath, true)
            .flatMap { bitmap: Bitmap ->
                Single.fromCallable {
                    val file = now().toDateTimeFormat("yyyyMMdd_HHmmss")
                        .let { File(savedDir(), "HiShoot_$it.png") }
                    bitmap.saveTo(file)
                    file
                }
                    .map { FileProvider.getUriForFile(context, FILE_AUTHORITY, it) }
                    .map { Result.Save(bitmap, it) }
            }

    private fun Template.core(path: ImageSourcePath, isSave: Boolean): Single<Bitmap> = singleBase()
        .flatMap { it.singleBackground(path.background, isSave) }
        .flatMap { it.singleMixingTemplate(this, path, isSave) }
        .flatMap { it.singleBadgeBitmap() }

    private fun Template.singleBase(): Single<Bitmap> = Single.fromCallable {
        if (!isArt()) System.gc()
        checkFrameSize(this)
        (mapSizes[id] ?: sizes).let {
            when (appPref.doubleScreenEnable) {
                true -> it * Sizes(2, 1)
                false -> it
            }
        }.createBitmap(Bitmap.Config.ARGB_8888)
    }

    private fun Bitmap.singleBackground(backgroundPath: String?, isSave: Boolean): Single<Bitmap> =
        Single.fromCallable {
            val backgroundMode = BackgroundMode.fromId(appPref.backgroundModeId)
            applyCanvas {
                when (backgroundMode) {
                    is BackgroundMode.Color -> drawColor(appPref.backgroundColorInt)
                    is BackgroundMode.Image -> {
                        val background: Bitmap = backgroundPath.backgroundImage(isSave, sizes)
                        when (appPref.backgroundImageBlurEnable) {
                            true -> drawBitmapBlur(background, appPref.backgroundImageBlurRadius)
                            false -> drawBitmapSafely(background)
                        }
                        background.recycle()
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
            var ss = path.screen1.screenShootImage(isSave)
            var mixed = mixIt(ss, template, isSave)
            drawBitmapSafely(mixed)
            if (appPref.doubleScreenEnable) {
                translate(mixed.width.toFloat(), 0F)
                ss = path.screen2.screenShootImage(isSave)
                mixed = mixIt(ss, template, isSave)
                drawBitmapSafely(mixed)
            }
            ss.recycle()
            mixed.recycle()
        }
    }

    private fun Bitmap.singleBadgeBitmap(): Single<Bitmap> = when (appPref.badgeEnable) {
        false -> Single.just(this)
        true -> Single.fromCallable {
            val bb = with(appPref) {
                val typeface = CacheFileTypefaces.getOrDefault(badgeTypefacePath)
                BadgeBitmap.Config(badgeText, typeface, badgeSize, badgeColor)
            }.let { badgeBitmap.create(it) }
            val padding = Math.round(context.dp2px(10))
            val (left, top) = BadgePosition.fromId(appPref.badgePositionId)
                .apply {
                    total = sizes
                    source = bb.sizes
                }
                .getPosition(padding)
            applyCanvas { drawBitmapSafely(bb, left, top) }
        }
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

    private fun Bitmap.resizePreview(): Bitmap = maxTextureSize?.let {
        if (sizes.x > it || sizes.y > it) resizeIfNotEqual(sizes.max(it)) else this
    } ?: this

    private fun checkFrameSize(template: Template) {
        if (mapSizes.containsKey(template.id)) return
        when (template) {
            is Template.Empty,
            is Template.Default,
            is Template.Version1 -> mapSizes[template.id] = template.sizes
            is Template.Version2 -> {
                template.frame.getBitmapSizesOnly(template.sizes) {
                    var s = it
                    if (s.x <= 1 || s.y <= 1) {
                        template.glare?.let {
                            it.name.getBitmapSizesOnly(it.size) {
                                s = it
                            }
                        }
                    }
                    mapSizes[template.id] = s
                }
            }
            is Template.Version3,
            is Template.VersionHtz -> {
                template.frame.getBitmapSizesOnly(template.sizes) {
                    mapSizes[template.id] = it
                }
            }
        }.exhaustive
    }

    private fun String?.getBitmapSizesOnly(reqSize: Sizes, consume: (Sizes) -> Unit) {
        this?.let {
            loadSync(it, false, reqSize)?.let {
                consume(it.sizes)
                it.recycle()
            }
        }
    }

    private fun mixIt(ss: Bitmap, template: Template, isSave: Boolean): Bitmap {
        val mixedSize = with(template) { mapSizes[id] ?: sizes }
        val mixed = mixedSize.createBitmap()
        val coordinate = mixedSize.coordinateNormalize(template)
        with(mixed) {
            when (template) {
                is Template.Default -> drawDefault(ss, coordinate)
                is Template.Version1 -> drawVersion1(ss, coordinate, isSave, template)
                is Template.Version2 -> drawVersion2(ss, coordinate, isSave, template)
                is Template.Version3 -> drawVersion3(ss, coordinate, isSave, template)
                is Template.VersionHtz -> drawVersionHtz(ss, coordinate, isSave, template)
                is Template.Empty -> throw IllegalStateException("Unknown ${template.id}")
            }.exhaustive
        }
        return mixed
    }

    //
    private fun Sizes.coordinateNormalize(template: Template): FloatArray {
        val ret: MutableList<Float> = template.coordinate.toMutableList()
        if (this != template.sizes) {
            val (x, y) = (this.toSizeF() / template.sizes.toSizeF())
            ret[0] = x * ret[0]
            ret[1] = y * ret[1]
            ret[2] = x * ret[2]
            ret[3] = y * ret[3]
            ret[4] = x * ret[4]
            ret[5] = y * ret[5]
            ret[6] = x * ret[6]
            ret[7] = y * ret[7]
        }
        return ret.toFloatArray()
    }

    //
    private fun Bitmap.drawDefault(
        screenShoot: Bitmap,
        coordinate: FloatArray
    ): Bitmap = applyCanvas {
        drawBitmapPerspective(screenShoot, coordinate)
        context.drawable(R.drawable.frame1)
            ?.toBitmap(width, height)
            ?.let { drawBitmapSafely(it) }
    }

    private fun Bitmap.drawVersion1(
        screenShoot: Bitmap,
        coordinate: FloatArray,
        isSave: Boolean,
        template: Template.Version1
    ): Bitmap = applyCanvas {
        drawBitmapPerspective(screenShoot, coordinate)
        assetTemplate(template.frame, isSave, sizes)
    }

    private fun Bitmap.drawVersion2(
        screenShoot: Bitmap,
        coordinate: FloatArray,
        isSave: Boolean,
        template: Template.Version2
    ): Bitmap = applyCanvas {
        if (appPref.templateShadowEnable) assetTemplate(template.shadow, isSave, sizes)
        if (appPref.templateFrameEnable) assetTemplate(template.frame, isSave, sizes)
        drawBitmapPerspective(screenShoot, coordinate)
        if (appPref.templateGlareEnable) {
            template.glare?.let { assetTemplate(it.name, isSave, it.size, it.position) }
        }
    }

    private fun Bitmap.drawVersion3(
        screenShoot: Bitmap,
        coordinate: FloatArray,
        isSave: Boolean,
        template: Template.Version3
    ): Bitmap = applyCanvas {
        if (appPref.templateShadowEnable) template.shadow?.let { assetTemplate(it, isSave, sizes) }
        if (appPref.templateFrameEnable) assetTemplate(template.frame, isSave, sizes)
        drawBitmapPerspective(screenShoot, coordinate)
        if (appPref.templateGlareEnable) {
            template.glares?.forEach { assetTemplate(it.name, isSave, it.size, it.position) }
        }
    }

    private fun Bitmap.drawVersionHtz(
        screenShoot: Bitmap,
        coordinate: FloatArray,
        isSave: Boolean,
        template: Template.VersionHtz
    ): Bitmap = applyCanvas {
        assetTemplate(template.frame, isSave, sizes)
        drawBitmapPerspective(screenShoot, coordinate)
        template.glare?.let { assetTemplate(it.name, isSave, it.size, it.position) }
    }

    private fun Canvas.assetTemplate(
        source: String,
        isSave: Boolean,
        sizes: Sizes,
        position: Sizes = Sizes.ZERO
    ) {
        loadSync(source, isSave, sizes)?.let {
            val (left, top) = position.toSizeF()
            drawBitmapSafely(it, left, top)
        }
    }

    private fun isArt(): Boolean { //
        val property = System.getProperty("java.vm.version", "")
        return property.isNotEmpty() && property[0].toInt() >= 2
    }
}