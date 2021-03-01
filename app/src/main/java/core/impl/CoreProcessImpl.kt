package core.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Bitmap.Config.ARGB_8888
import common.custombitmap.AlphaPatternBitmap
import common.custombitmap.BadgeBitmapBuilder
import common.egl.MaxTexture
import common.ext.dp2px
import common.ext.exhaustive
import common.ext.graphics.applyCanvas
import common.ext.graphics.createBitmap
import common.ext.graphics.drawBitmapBlur
import common.ext.graphics.drawBitmapSafely
import common.ext.graphics.resizeIfNotEqual
import common.ext.graphics.scaleCenterCrop
import common.ext.graphics.sizes
import core.CoreProcess
import core.CoreRequest
import core.MixTemplate
import core.Preview
import core.Save
import core.SaveResult
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
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.withContext
import org.illegaller.ratabb.hishoot2i.BuildConfig.DEBUG
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
    private val coreRequest: CoreRequest
) : CoreProcess {

    private val badgeBitmapPadding by lazy { context.dp2px(10F).roundToInt() }
    private val savingIt: suspend (Bitmap, CompressFormat, Int) -> Save =
        (saveResult::save)

    private val loadBackground: (String, Boolean, Sizes, Boolean) -> Bitmap? =
        (imageLoader::loadSync)

    private val maxTextureSize: Int by lazy { if (!DEBUG) maxTexture.get() ?: 2014 else 2014 }

    private val templateMixed: suspend (
        Template,
        String?,
        Boolean
    ) -> Bitmap = { template: Template, path: String?, isSave: Boolean ->
        mixTemplate.mixed(template, coreRequest.mixTemplateConfig, path, isSave)
    }

    override suspend fun preview(
        template: Template,
        sourcePath: ImageSourcePath
    ): Preview = withContext(Default) {
        val core = core(template, sourcePath, isSave = false)
        Preview(resizePreview(core))
    }

    override suspend fun save(
        template: Template,
        sourcePath: ImageSourcePath
    ): Save = withContext(Default) {
        val core = core(template, sourcePath, isSave = true)
        savingIt(core, coreRequest.compressFormat, coreRequest.saveQuality)
    }

    private suspend fun core(
        template: Template,
        path: ImageSourcePath,
        isSave: Boolean
    ): Bitmap = withContext(Default) {
        val sizes = template.sizes.run {
            when (coreRequest.doubleScreenEnable) {
                true -> copy(x = x * 2)
                false -> this
            }
        }
        val bitmap = sizes.createBitmap(ARGB_8888)
        val drawBackground = drawBackground(bitmap, path.background, isSave)
        val mixing = mixing(drawBackground, template, path, isSave)
        drawBadgeBitmap(mixing)
    }

    private suspend fun drawBackground(
        bitmap: Bitmap,
        path: String?,
        isSave: Boolean
    ): Bitmap = withContext(Default) {
        when (coreRequest.backgroundMode) {
            TRANSPARENT -> bitmap
            COLOR -> bitmap.applyCanvas { drawColor(coreRequest.backgroundColorInt) }
            IMAGE -> bitmap.applyCanvas {
                backgroundOrAlphaPattern(path, isSave, bitmap.sizes).run {
                    when (coreRequest.backgroundImageBlurEnable) {
                        true -> drawBitmapBlur(this, coreRequest.backgroundImageBlurRadius)
                        false -> drawBitmapSafely(this)
                    }
                }
            }
        }.exhaustive
    }

    private suspend fun mixing(
        bitmap: Bitmap,
        template: Template,
        path: ImageSourcePath,
        isSave: Boolean
    ): Bitmap = withContext(Default) {
        var mixed = templateMixed(template, path.screen1, isSave)
        bitmap.applyCanvas {
            drawBitmapSafely(mixed)
            if (coreRequest.doubleScreenEnable) {
                mixed = templateMixed(template, path.screen2, isSave)
                drawBitmapSafely(mixed, left = mixed.width.toFloat())
            }
        }
    }

    private suspend fun drawBadgeBitmap(bitmap: Bitmap): Bitmap = when (coreRequest.badgeEnable) {
        false -> bitmap
        true -> withContext(Default) {
            val badgeBitmapConfig = coreRequest.badgeConfig
            val badgeBitmap = badgeBitmapBuilder.build(badgeBitmapConfig)
            val (left, top) = coreRequest.badgePosition.getValue(
                bitmap.sizes,
                badgeBitmap.sizes,
                badgeBitmapPadding
            )
            bitmap.applyCanvas { drawBitmapSafely(badgeBitmap, left, top) }
        }
    }

    private suspend fun resizePreview(bitmap: Bitmap): Bitmap = when {
        bitmap.sizes > Sizes(maxTextureSize) -> withContext(Default) {
            bitmap.resizeIfNotEqual(bitmap.sizes.max(maxTextureSize))
        }
        else -> bitmap
    }

    private fun backgroundOrAlphaPattern(
        path: String?,
        save: Boolean,
        size: Sizes
    ): Bitmap = path?.let {
        loadBackground(it, save, size, false)?.apply {
            return@let when (coreRequest.imageOption) {
                SCALE_FILL, MANUAL_CROP -> resizeIfNotEqual(size)
                CENTER_CROP -> scaleCenterCrop(size)
            }.exhaustive
        }
    } ?: alphaPatternBitmap.create(size)
}
