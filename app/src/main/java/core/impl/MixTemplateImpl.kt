package core.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.drawable.updateBounds
import common.ext.graphics.createBitmap
import common.ext.graphics.drawBitmapPerspective
import common.ext.graphics.drawBitmapSafely
import common.ext.graphics.sizes
import core.MixTemplate
import core.MixTemplate.Config
import dagger.hilt.android.qualifiers.ApplicationContext
import entity.ImageSourcePath
import entity.Sizes
import entity.SizesF
import imageloader.ImageLoader
import org.illegaller.ratabb.hishoot2i.R
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
    private val frameDefault: (Int, Int) -> Drawable by lazy {
        { width, height ->
            ContextCompat.getDrawable(context, R.drawable.frame1)?.apply {
                updateBounds(right = width, bottom = height)
            } ?: throw IllegalStateException("Can't Load frame1")
        }
    }

    override suspend fun Bitmap.drawMixing(
        template: Template,
        config: Config,
        path: ImageSourcePath,
        isDoubleScreen: Boolean
    ): Bitmap = applyCanvas {
        drawBitmapSafely(singleMix(template, config, path.screen1))
        if (isDoubleScreen) {
            drawBitmapSafely(
                bitmap = singleMix(template, config, path.screen2),
                left = template.sizes.x.toFloat()
            )
        }
    }

    private suspend fun singleMix(
        template: Template,
        configMix: Config,
        ss: String?
    ): Bitmap = with(template.sizes.createBitmap()) {
        when (template) {
            is Default -> drawDefault(ss, template)
            is Version1 -> drawVersion1(ss, template)
            is Version2 -> drawVersion2(ss, template, configMix)
            is Version3 -> drawVersion3(ss, template, configMix)
            is VersionHtz -> drawVersionHtz(ss, template)
        }
    }

    private suspend fun Bitmap.drawDefault(ss: String?, d: Default) = applyCanvas {
        drawScreenShoot(ss, d)
        frameDefault(width, height).draw(this)
    }

    private suspend fun Bitmap.drawVersion1(ss: String?, v1: Version1) = applyCanvas {
        drawScreenShoot(ss, v1)
        drawAssetTemplate(v1.frame, sizes)
    }

    private suspend fun Bitmap.drawVersion2(ss: String?, v2: Version2, cfg: Config) = applyCanvas {
        val (isFrame, isGlare, isShadow) = cfg
        if (isShadow) drawAssetTemplate(v2.shadow, sizes)
        if (isFrame) drawAssetTemplate(v2.frame, sizes)
        drawScreenShoot(ss, v2)
        if (isGlare) v2.glare?.let { drawAssetTemplate(it.name, it.size, it.position) }
    }

    private suspend fun Bitmap.drawVersion3(ss: String?, v3: Version3, cfg: Config) = applyCanvas {
        val (isFrame, isGlare, isShadow) = cfg
        if (isShadow) v3.shadow?.let { drawAssetTemplate(it, sizes) }
        if (isFrame) drawAssetTemplate(v3.frame, sizes)
        drawScreenShoot(ss, v3)
        if (isGlare) v3.glares?.forEach { drawAssetTemplate(it.name, it.size, it.position) }
    }

    private suspend fun Bitmap.drawVersionHtz(ss: String?, htz: VersionHtz) = applyCanvas {
        drawAssetTemplate(htz.frame, sizes)
        drawScreenShoot(ss, htz)
        htz.glare?.let { drawAssetTemplate(it.name, it.size, it.position) }
    }

    private suspend fun Canvas.drawAssetTemplate(
        source: String,
        sizes: Sizes,
        position: SizesF = SizesF.ZERO
    ) {
        val (left, top) = position
        drawBitmapSafely(loadAssetsTemplate(source, sizes), left, top)
    }

    private suspend fun Canvas.drawScreenShoot(ss: String?, template: Template) {
        val sizes = Sizes(width, height)
        val coordinate = template.coordinate.toFloatArray()
        drawBitmapPerspective(loadScreen(ss, sizes), coordinate)
    }
}
