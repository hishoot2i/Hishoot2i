package core.impl

import android.graphics.Bitmap
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import common.graphics.drawBitmapSafely
import common.graphics.sizes
import common.opengl.MaxTexture
import core.BadgeBuilder
import core.CoreProcess
import core.CoreRequest
import core.MixTemplate
import core.Preview
import core.Save
import core.SaveResult
import entity.BackgroundMode.COLOR
import entity.BackgroundMode.IMAGE
import entity.BackgroundMode.TRANSPARENT
import entity.ImageSourcePath
import entity.Sizes
import imageloader.ImageLoader
import org.illegaller.ratabb.hishoot2i.BuildConfig.DEBUG
import template.Template
import javax.inject.Inject

class CoreProcessImpl @Inject constructor(
    imageLoader: ImageLoader,
    saveResult: SaveResult,
    mixTemplate: MixTemplate,
    maxTexture: MaxTexture,
    coreRequest: CoreRequest,
    badgeBuilder: BadgeBuilder
) : CoreProcess,
    CoreRequest by coreRequest,
    MixTemplate by mixTemplate,
    ImageLoader by imageLoader,
    SaveResult by saveResult,
    BadgeBuilder by badgeBuilder {

    private val maxTextureSize: Int by lazy { if (!DEBUG) maxTexture.get() ?: 2014 else 2014 }

    override suspend fun preview(template: Template, sourcePath: ImageSourcePath): Preview =
        Preview(template.core(sourcePath).resizePreview())

    override suspend fun save(template: Template, sourcePath: ImageSourcePath): Save =
        savingIt(template.core(sourcePath), compressFormat, saveQuality)

    private suspend fun Template.core(path: ImageSourcePath): Bitmap =
        sizes.doubleXIf(doubleScreenEnable).run { createBitmap(x, y) }
            .drawBackground(path.background)
            .drawMixing(this, mixTemplateConfig, path, doubleScreenEnable)
            .drawBadge(badgeEnable, badgePosition, badgeConfig)

    private suspend fun Bitmap.drawBackground(path: String?): Bitmap = applyCanvas {
        when (backgroundMode) {
            TRANSPARENT -> { // no-op
            }
            COLOR -> drawColor(backgroundColorInt)
            IMAGE -> drawBitmapSafely(
                loadBackground(
                    source = path,
                    reqSizes = sizes,
                    imageOption = imageOption,
                    blurEnable = backgroundImageBlurEnable,
                    blurRadius = backgroundImageBlurRadius
                )
            )
        }
    }

    private fun Bitmap.resizePreview(): Bitmap = if (sizes > Sizes(maxTextureSize)) {
        val (width, height) = sizes.max(maxTextureSize)
        scale(width, height)
    } else this

    private fun Sizes.doubleXIf(condition: Boolean) = if (condition) copy(x = x * 2) else this
}
