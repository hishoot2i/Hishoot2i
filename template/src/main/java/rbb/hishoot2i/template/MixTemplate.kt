package rbb.hishoot2i.template

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import rbb.hishoot2i.common.entity.Glare
import rbb.hishoot2i.common.entity.Sizes
import rbb.hishoot2i.common.ext.deviceHeight
import rbb.hishoot2i.common.ext.deviceWidth
import rbb.hishoot2i.common.ext.exhaustive
import rbb.hishoot2i.common.ext.graphics.alphaPatternBitmap
import rbb.hishoot2i.common.ext.graphics.applyCanvas
import rbb.hishoot2i.common.ext.graphics.createBitmap
import rbb.hishoot2i.common.ext.graphics.drawBitmapPerspective
import rbb.hishoot2i.common.ext.graphics.drawBitmapSafely
import rbb.hishoot2i.common.ext.graphics.drawable
import rbb.hishoot2i.common.ext.graphics.toBitmap
import rbb.hishoot2i.common.imageloader.ImageLoader
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

class MixTemplate @Inject constructor(
    context: Context,
    imageLoader: ImageLoader
) : ImageLoader by imageLoader {
    /**/
    private val alphaPatternBitmap: Bitmap by lazy(NONE) { context.alphaPatternBitmap() }
    private val deviceWidth: Int by lazy(NONE) { context.deviceWidth }
    private val deviceHeight: Int by lazy(NONE) { context.deviceHeight }
    private val defaultFrame: Drawable? by lazy(NONE) { context.drawable(R.drawable.frame1) }
    /**/
    fun mix(template: Template, options: Options, pathSS: String?, isSave: Boolean): Bitmap {
        val result: Bitmap = try {
            template.sizes.createBitmap(Bitmap.Config.ARGB_8888)
        } catch (e: Exception) {
            throw TemplateException("Can not create bitmap with (${template.sizes},ARGB_8888)", e)
        }
        val screenShoot: Bitmap = pathSS?.let {
            loadSync(it, Sizes(deviceWidth, deviceHeight), isSave)
        } ?: alphaPatternBitmap
        with(result) {
            when (template) {
                is Template.Default -> drawingDefault(screenShoot, template)
                is Template.Version1 -> drawingVersion1(screenShoot, template, isSave)
                is Template.Version2 -> drawingVersion2(screenShoot, template, isSave, options)
                is Template.Version3 -> drawingVersion3(screenShoot, template, isSave, options)
                is Template.VersionHtz -> drawingVersionHtz(screenShoot, template, isSave)
                is Template.Empty -> throw TemplateException("Unknown template ${template.id}")
            }.exhaustive
        }
        return result
    }

    private fun Bitmap.drawingDefault(screenShoot: Bitmap, template: Template.Default) {
        applyCanvas {
            drawScreenShoot(screenShoot, template.coordinate)
            val (width, height) = template.sizes
            defaultFrame?.toBitmap(width, height)?.let {
                drawBitmapSafely(it)
            }
        }
    }

    private fun Bitmap.drawingVersion1(
        screenShoot: Bitmap,
        template: Template.Version1,
        isSave: Boolean
    ) {
        applyCanvas {
            drawScreenShoot(screenShoot, template.coordinate)
            drawFrame(template, isSave)
        }
    }

    private fun Bitmap.drawingVersion2(
        screenShoot: Bitmap,
        template: Template.Version2,
        isSave: Boolean,
        options: Options
    ) {
        applyCanvas {
            if (options.shadowEnable) {
                loadSync(template.shadow, template.sizes, isSave)?.let {
                    drawBitmapSafely(it)
                }
            }
            if (options.frameEnable) drawFrame(template, isSave)
            drawScreenShoot(screenShoot, template.coordinate)
            if (options.glareEnable) {
                template.glare?.let {
                    drawGlare(it, isSave)
                }
            }
        }
    }

    private fun Bitmap.drawingVersion3(
        screenShoot: Bitmap,
        template: Template.Version3,
        isSave: Boolean,
        options: Options
    ) {
        applyCanvas {
            if (options.shadowEnable) {
                template.shadow?.let {
                    loadSync(it, template.sizes, isSave)?.let {
                        drawBitmapSafely(it)
                    }
                }
            }
            if (options.frameEnable) drawFrame(template, isSave)
            drawScreenShoot(screenShoot, template.coordinate)
            if (options.glareEnable) {
                template.glares?.forEach {
                    drawGlare(it, isSave)
                }
            }
        }
    }

    private fun Bitmap.drawingVersionHtz(
        screenShoot: Bitmap,
        template: Template.VersionHtz,
        isSave: Boolean
    ) {
        applyCanvas {
            drawFrame(template, isSave)
            drawScreenShoot(screenShoot, template.coordinate)
            template.glare?.let { drawGlare(it, isSave) }
        }
    }

    // /////////////
    private fun Canvas.drawFrame(template: Template, isSave: Boolean) {
        loadSync(template.frame, template.sizes, isSave).let {
            drawBitmapSafely(it)
        }
    }

    private fun Canvas.drawGlare(glare: Glare, isSave: Boolean) {
        val (left, top) = glare.position.toSizeF()
        loadSync(glare.name, glare.size, isSave)?.let { drawBitmapSafely(it, left, top) }
    }

    private fun Canvas.drawScreenShoot(screenShoot: Bitmap, coordinate: List<Float>) {
        drawBitmapPerspective(screenShoot, coordinate.toFloatArray())
    }

    data class Options(
        val frameEnable: Boolean,
        val shadowEnable: Boolean,
        val glareEnable: Boolean
    )
}