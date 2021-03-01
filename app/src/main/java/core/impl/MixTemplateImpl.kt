package core.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import common.custombitmap.AlphaPatternBitmap
import common.ext.deviceSizes
import common.ext.exhaustive
import common.ext.graphics.applyCanvas
import common.ext.graphics.createBitmap
import common.ext.graphics.drawBitmapPerspective
import common.ext.graphics.drawBitmapSafely
import common.ext.graphics.resizeIfNotEqual
import common.ext.graphics.sizes
import core.MixTemplate
import core.MixTemplate.Config
import dagger.hilt.android.qualifiers.ApplicationContext
import entity.Sizes
import entity.SizesF
import imageloader.ImageLoader
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.withContext
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
    imageLoader: ImageLoader,
    private val alphaPatternBitmap: AlphaPatternBitmap
) : MixTemplate {

    private val deviceSizes by lazy { context.deviceSizes }

    /* NOTE: frameDefault [R.drawable.frame1] is 9-patch, do not load w/ imageLoader. */
    private val frameDefault by lazy {
        AppCompatResources.getDrawable(context, R.drawable.frame1)
    }

    private val loadSync: (String, Boolean, Sizes, Boolean) -> Bitmap? =
        (imageLoader::loadSync)

    override suspend fun mixed(
        template: Template,
        config: Config,
        ss: String?,
        isSave: Boolean
    ): Bitmap = withContext(Default) {
        val coordinate = template.coordinate.toFloatArray()
        template.sizes.createBitmap().let { bitmap ->
            when (template) {
                is Default -> bitmap.drawDefault(ss, coordinate, isSave)
                is Version1 -> bitmap.drawVersion1(ss, coordinate, isSave, template)
                is Version2 -> bitmap.drawVersion2(ss, coordinate, isSave, template, config)
                is Version3 -> bitmap.drawVersion3(ss, coordinate, isSave, template, config)
                is VersionHtz -> bitmap.drawVersionHtz(ss, coordinate, isSave, template)
            }.exhaustive
        }
    }

    private fun String?.screenShootImage(isSave: Boolean): Bitmap = this?.let {
        loadSync(it, isSave, deviceSizes, true)
    } ?: alphaPatternBitmap.create(deviceSizes)

    private fun Bitmap.drawDefault(
        ss: String?,
        coordinate: FloatArray,
        isSave: Boolean
    ): Bitmap = applyCanvas {
        drawBitmapPerspective(ss.screenShootImage(isSave), coordinate)
        this@MixTemplateImpl.frameDefault?.toBitmap(width, height, config)?.let {
            drawBitmapSafely(it)
        }
    }

    private fun Bitmap.drawVersion1(
        ss: String?,
        coordinate: FloatArray,
        isSave: Boolean,
        template: Version1
    ): Bitmap = applyCanvas {
        drawBitmapPerspective(ss.screenShootImage(isSave), coordinate)
        drawAssetTemplate(template.frame, isSave, sizes)
    }

    private fun Bitmap.drawVersion2(
        ss: String?,
        coordinate: FloatArray,
        isSave: Boolean,
        template: Version2,
        config: Config
    ): Bitmap = applyCanvas {
        if (config.isShadow) drawAssetTemplate(template.shadow, isSave, sizes)
        if (config.isFrame) drawAssetTemplate(template.frame, isSave, sizes)
        drawBitmapPerspective(ss.screenShootImage(isSave), coordinate)
        if (config.isGlare) {
            template.glare?.let { glare ->
                drawAssetTemplate(glare.name, isSave, glare.size, glare.position)
            }
        }
    }

    private fun Bitmap.drawVersion3(
        ss: String?,
        coordinate: FloatArray,
        isSave: Boolean,
        template: Version3,
        config: Config
    ): Bitmap = applyCanvas {
        if (config.isShadow) template.shadow?.let { drawAssetTemplate(it, isSave, sizes) }
        if (config.isFrame) drawAssetTemplate(template.frame, isSave, sizes)
        drawBitmapPerspective(ss.screenShootImage(isSave), coordinate)
        if (config.isGlare) {
            template.glares?.forEach { glare ->
                drawAssetTemplate(glare.name, isSave, glare.size, glare.position)
            }
        }
    }

    private fun Bitmap.drawVersionHtz(
        ss: String?,
        coordinate: FloatArray,
        isSave: Boolean,
        template: VersionHtz
    ): Bitmap = applyCanvas {
        drawAssetTemplate(template.frame, isSave, sizes)
        drawBitmapPerspective(ss.screenShootImage(isSave), coordinate)
        template.glare?.let { glare ->
            drawAssetTemplate(glare.name, isSave, glare.size, glare.position)
        }
    }

    private fun Canvas.drawAssetTemplate(
        source: String,
        isSave: Boolean,
        sizes: Sizes,
        position: SizesF = SizesF.ZERO
    ) {
        loadSync(source, isSave, sizes, false)?.let { bitmap ->
            val (left, top) = position
            drawBitmapSafely(bitmap.resizeIfNotEqual(sizes), left, top)
        }
    }
}
