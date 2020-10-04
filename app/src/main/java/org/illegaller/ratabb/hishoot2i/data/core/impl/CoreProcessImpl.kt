package org.illegaller.ratabb.hishoot2i.data.core.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Typeface
import common.custombitmap.AlphaPatternBitmap
import common.custombitmap.BadgeBitmapBuilder
import common.egl.MaxTexture
import common.ext.dp2px
import common.ext.exhaustive
import common.ext.graphics.applyCanvas
import common.ext.graphics.createBitmap
import common.ext.graphics.createFromFileOrDefault
import common.ext.graphics.drawBitmapBlur
import common.ext.graphics.drawBitmapSafely
import common.ext.graphics.resizeIfNotEqual
import common.ext.graphics.scaleCenterCrop
import common.ext.graphics.sizes
import dagger.hilt.android.qualifiers.ApplicationContext
import entity.BackgroundMode.COLOR
import entity.BackgroundMode.IMAGE
import entity.BackgroundMode.TRANSPARENT
import entity.ImageOption.CENTER_CROP
import entity.ImageOption.MANUAL_CROP
import entity.ImageOption.SCALE_FILL
import entity.ImageSourcePath
import entity.Sizes
import imageloader.ImageLoader
import io.reactivex.Single
import org.illegaller.ratabb.hishoot2i.BuildConfig.DEBUG
import org.illegaller.ratabb.hishoot2i.data.core.CoreProcess
import org.illegaller.ratabb.hishoot2i.data.core.MixTemplate
import org.illegaller.ratabb.hishoot2i.data.core.MixTemplate.Config
import org.illegaller.ratabb.hishoot2i.data.core.Result
import org.illegaller.ratabb.hishoot2i.data.core.SaveResult
import org.illegaller.ratabb.hishoot2i.data.pref.BackgroundToolPref
import org.illegaller.ratabb.hishoot2i.data.pref.BadgeToolPref
import org.illegaller.ratabb.hishoot2i.data.pref.ScreenToolPref
import org.illegaller.ratabb.hishoot2i.data.pref.SettingPref
import org.illegaller.ratabb.hishoot2i.data.pref.TemplateToolPref
import template.Template
import javax.inject.Inject
import kotlin.math.roundToInt

class CoreProcessImpl @Inject constructor(
    @ApplicationContext context: Context,
    imageLoader: ImageLoader,
    saveResult: SaveResult,
    mixTemplate: MixTemplate,
    maxTexture: MaxTexture,
    private val alphaPatternBitmap: AlphaPatternBitmap,
    private val badgeBitmapBuilder: BadgeBitmapBuilder,
    private val backgroundToolPref: BackgroundToolPref,
    private val badgeToolPref: BadgeToolPref,
    private val screenToolPref: ScreenToolPref,
    private val templateToolPref: TemplateToolPref,
    private val settingPref: SettingPref
) : CoreProcess {

    private val badgeBitmapConfig: BadgeBitmapBuilder.Config
        get() = with(badgeToolPref) {
            val typeface = when (val path = badgeTypefacePath) {
                null, "DEFAULT" -> Typeface.DEFAULT
                else -> path.createFromFileOrDefault()
            }
            return BadgeBitmapBuilder.Config(badgeText, typeface, badgeSize, badgeColor)
        }
    private val badgeBitmapPadding by lazy { context.dp2px(10F).roundToInt() }
    private val savingIt: (Bitmap, CompressFormat, Int) -> Single<Result.Save> =
        (saveResult::save)

    private val loadBackground: (String, Boolean, Sizes, Boolean) -> Bitmap? =
        (imageLoader::loadSync)

    private val maxTextureSize: Int by lazy { if (!DEBUG) maxTexture.get() ?: 2014 else 2014 }

    private val templateMixed: (Template, String?, Boolean) -> Bitmap =
        { template: Template, path: String?, isSave: Boolean ->
            val config = with(templateToolPref) {
                Config(templateFrameEnable, templateGlareEnable, templateShadowEnable)
            }
            mixTemplate.mixed(template, config, path, isSave)
        }

    override fun preview(template: Template, sourcePath: ImageSourcePath): Single<Result> =
        template.core(sourcePath, isSave = false).flatMap { it.singleResizePreview() }
            .map { Result.Preview(it) }

    override fun save(template: Template, sourcePath: ImageSourcePath): Single<Result> =
        template.core(sourcePath, isSave = true).flatMap {
            with(settingPref) { savingIt(it, compressFormat, saveQuality) }
        }

    private fun Template.core(path: ImageSourcePath, isSave: Boolean): Single<Bitmap> =
        Single.fromCallable {
            sizes.doubleWidthIf(screenToolPref.doubleScreenEnable).createBitmap(ARGB_8888)
        }
            .flatMap { it.singleBackground(path.background, isSave) }
            .flatMap { it.singleMixing(this, path, isSave) }
            .flatMap { it.singleBadgeBitmap() }

    private fun Bitmap.singleBackground(path: String?, isSave: Boolean): Single<Bitmap> =
        with(backgroundToolPref) {
            when (backgroundMode) {
                TRANSPARENT -> Single.just(this@singleBackground)
                COLOR -> Single.fromCallable { applyCanvas { drawColor(backgroundColorInt) } }
                IMAGE -> Single.fromCallable {
                    applyCanvas {
                        path.backgroundOrAlphaPattern(isSave, sizes).let {
                            if (backgroundImageBlurEnable) {
                                drawBitmapBlur(it, backgroundImageBlurRadius)
                            } else drawBitmapSafely(it)
                        }
                    }
                }
            }.exhaustive
        }

    private fun Bitmap.singleMixing(
        template: Template,
        path: ImageSourcePath,
        isSave: Boolean
    ): Single<Bitmap> = Single.fromCallable {
        applyCanvas {
            var mixed = templateMixed(template, path.screen1, isSave)
            drawBitmapSafely(mixed)
            if (screenToolPref.doubleScreenEnable) {
                mixed = templateMixed(template, path.screen2, isSave)
                drawBitmapSafely(mixed, left = mixed.width.toFloat())
            }
        }
    }

    private fun Bitmap.singleBadgeBitmap(): Single<Bitmap> = with(badgeToolPref) {
        if (!badgeEnable) {
            Single.just(this@singleBadgeBitmap)
        } else {
            Single.fromCallable {
                val badgeBitmap = badgeBitmapBuilder.build(badgeBitmapConfig)
                val (left, top) = badgePosition.getValue(
                    this@singleBadgeBitmap.sizes,
                    badgeBitmap.sizes,
                    badgeBitmapPadding
                )
                applyCanvas { drawBitmapSafely(badgeBitmap, left, top) }
            }
        }
    }

    private fun Bitmap.singleResizePreview(): Single<Bitmap> = Single.fromCallable {
        if (sizes > Sizes(maxTextureSize)) resizeIfNotEqual(sizes.max(maxTextureSize)) else this
    }

    private fun Sizes.doubleWidthIf(condition: Boolean): Sizes =
        if (condition) this.copy(x = this.x * 2) else this

    private fun String?.backgroundOrAlphaPattern(
        save: Boolean,
        size: Sizes
    ): Bitmap = this?.let {
        loadBackground(it, save, size, false)?.apply {
            return@let when (backgroundToolPref.imageOption) {
                SCALE_FILL, MANUAL_CROP -> resizeIfNotEqual(size)
                CENTER_CROP -> scaleCenterCrop(size)
            }.exhaustive
        }
    } ?: alphaPatternBitmap.create(size)
}
