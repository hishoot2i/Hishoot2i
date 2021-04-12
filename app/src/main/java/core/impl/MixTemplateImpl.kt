package core.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.updateBounds
import androidx.core.graphics.withMatrix
import common.graphics.drawBitmapSafely
import common.graphics.sizes
import core.MixTemplate
import core.MixTemplate.Config
import dagger.hilt.android.qualifiers.ApplicationContext
import entity.ImageSourcePath
import entity.Sizes
import entity.SizesF
import imageloader.ImageLoader
import template.Template
import template.Template.Default
import template.Template.Version1
import template.Template.Version2
import template.Template.Version3
import template.Template.VersionHtz
import javax.inject.Inject

class MixTemplateImpl @Inject constructor(
    @ApplicationContext context: Context,
    imageLoader: ImageLoader
) : MixTemplate, ImageLoader by imageLoader {

    /* NOTE: frameDefault [R.drawable.frame1] is 9-patch, do not load w/ imageLoader. */
    private val frameDefault: Drawable by lazy {
        ContextCompat.getDrawable(context, template.R.drawable.frame1)
            ?: throw IllegalStateException("Can't Load frame1")
    }

    override suspend fun Bitmap.drawMixing(
        template: Template,
        config: Config,
        path: ImageSourcePath,
        isDoubleScreen: Boolean
    ): Bitmap = applyCanvas {
        drawBitmapSafely(bitmap = singleMix(template = template, cfg = config, ss = path.screen1))
        if (isDoubleScreen) {
            drawBitmapSafely(
                bitmap = singleMix(template = template, cfg = config, ss = path.screen2),
                left = template.sizes.x.toFloat()
            )
        }
    }

    private suspend fun singleMix(
        template: Template,
        cfg: Config,
        ss: String?
    ): Bitmap = createBitmap(template.sizes.x, template.sizes.y).run {
        when (template) {
            is Default -> drawDefault(ss = ss, d = template)
            is Version1 -> drawVersion1(ss = ss, v1 = template)
            is Version2 -> drawVersion2(ss = ss, v2 = template, cfg = cfg)
            is Version3 -> drawVersion3(ss = ss, v3 = template, cfg = cfg)
            is VersionHtz -> drawVersionHtz(ss = ss, htz = template, cfg = cfg)
            else -> throw IllegalStateException("...?") // IDK: sealed class Template already there.
        }
    }

    private suspend fun Bitmap.drawDefault(ss: String?, d: Default) = applyCanvas {
        drawScreenShoot(ss = ss, template = d)
        frameDefault.apply { updateBounds(right = width, bottom = height) }.draw(this)
    }

    private suspend fun Bitmap.drawVersion1(ss: String?, v1: Version1) = applyCanvas {
        drawScreenShoot(ss = ss, template = v1)
        drawAssetTemplate(source = v1.frame, sizes = sizes)
    }

    private suspend fun Bitmap.drawVersion2(ss: String?, v2: Version2, cfg: Config) = applyCanvas {
        val (isFrame, isGlare, isShadow) = cfg
        if (isShadow) drawAssetTemplate(source = v2.shadow, sizes = sizes)
        if (isFrame) drawAssetTemplate(source = v2.frame, sizes = sizes)
        drawScreenShoot(ss = ss, template = v2)
        val glare = v2.glare
        if (isGlare && glare != null) {
            drawAssetTemplate(source = glare.name, sizes = glare.size, position = glare.position)
        }
    }

    private suspend fun Bitmap.drawVersion3(ss: String?, v3: Version3, cfg: Config) = applyCanvas {
        val (isFrame, isGlare, isShadow) = cfg
        val shadow = v3.shadow
        val glares = v3.glares
        if (isShadow && shadow != null) drawAssetTemplate(source = shadow, sizes = sizes)
        if (isFrame) drawAssetTemplate(source = v3.frame, sizes = sizes)
        drawScreenShoot(ss = ss, template = v3)
        if (isGlare && glares != null) glares.forEach { glare ->
            drawAssetTemplate(source = glare.name, sizes = glare.size, position = glare.position)
        }
    }

    private suspend fun Bitmap.drawVersionHtz(ss: String?, htz: VersionHtz, cfg: Config) = applyCanvas {
        val (isFrame, isGlare, _) = cfg
        if (isFrame) drawAssetTemplate(source = htz.frame, sizes = sizes)
        drawScreenShoot(ss = ss, template = htz)
        val glare = htz.glare
        if (isGlare && glare != null) drawAssetTemplate(
            source = glare.name,
            sizes = glare.size,
            position = glare.position
        )
    }

    private suspend fun Canvas.drawAssetTemplate(
        source: String,
        sizes: Sizes,
        position: SizesF = SizesF.ZERO
    ) {
        drawBitmapSafely(
            bitmap = loadAssetsTemplate(source = source, reqSizes = sizes),
            left = position.x,
            top = position.y
        )
    }

    private suspend fun Canvas.drawScreenShoot(ss: String?, template: Template) {
        val bitmap = loadScreen(source = ss, reqSizes = Sizes(width, height))
        val coordinate = template.coordinate.toFloatArray()
        // draw perspective bitmap with coordinate as floating point [Matrix#setPolyToPoly]
        if (null != bitmap && !bitmap.isRecycled) {
            val (width, height) = bitmap.sizes.toSizeF()
            val src = floatArrayOf(0F, 0F, width, 0F, 0F, height, width, height)
            val matrix = Matrix().apply { setPolyToPoly(src, 0, coordinate, 0, 4) }
            withMatrix(matrix) { drawBitmapSafely(bitmap) }
        }
    }
}
