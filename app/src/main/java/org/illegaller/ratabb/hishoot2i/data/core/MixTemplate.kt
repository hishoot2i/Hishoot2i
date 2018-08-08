package org.illegaller.ratabb.hishoot2i.data.core

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.data.pref.AppPref
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
import rbb.hishoot2i.common.ext.graphics.recycleSafely
import rbb.hishoot2i.common.ext.graphics.resizeIfNotEqual
import rbb.hishoot2i.common.ext.graphics.sizes
import rbb.hishoot2i.common.ext.graphics.toBitmap
import rbb.hishoot2i.common.imageloader.ImageLoader
import rbb.hishoot2i.template.Template
import kotlin.LazyThreadSafetyMode.NONE

internal class MixTemplate(
    val context: Context,
    val appPref: AppPref,
    imageLoader: ImageLoader
) : ImageLoader by imageLoader {
    private val deviceSizes by lazy(NONE) { with(context) { Sizes(deviceWidth, deviceHeight) } }
    private val isFrameEnable get() = appPref.templateFrameEnable
    private val isGlareEnable get() = appPref.templateGlareEnable
    private val isShadowEnable get() = appPref.templateShadowEnable
    /*  */
    fun mixed(template: Template, ss: String?, isSave: Boolean): Bitmap = with(template) {
        val coordinate = coordinate.toFloatArray()
        sizes.createBitmap().let {
            when (this) {
                is Template.Default -> it.drawDefault(ss, coordinate, isSave)
                is Template.Version1 -> it.drawVersion1(ss, coordinate, isSave, this)
                is Template.Version2 -> it.drawVersion2(ss, coordinate, isSave, this)
                is Template.Version3 -> it.drawVersion3(ss, coordinate, isSave, this)
                is Template.VersionHtz -> it.drawVersionHtz(ss, coordinate, isSave, this)
                is Template.Empty -> throw IllegalArgumentException("Unknown ${template.id}")
            }.exhaustive
        }
    }

    private fun String?.screenShootImage(isSave: Boolean): Bitmap =
        this?.let { loadSync(it, isSave, deviceSizes) } ?: context.alphaPatternBitmap(deviceSizes)

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
        if (isShadowEnable) drawAssetTemplate(template.shadow, isSave, sizes)
        if (isFrameEnable) drawAssetTemplate(template.frame, isSave, sizes)
        drawBitmapPerspective(ss.screenShootImage(isSave), coordinate)
        if (isGlareEnable) {
            template.glare?.let { drawAssetTemplate(it.name, isSave, it.size, it.position) }
        }
    }

    private fun Bitmap.drawVersion3(
        ss: String?,
        coordinate: FloatArray,
        isSave: Boolean,
        template: Template.Version3
    ): Bitmap = applyCanvas {
        if (isShadowEnable) template.shadow?.let { drawAssetTemplate(it, isSave, sizes) }
        if (isFrameEnable) drawAssetTemplate(template.frame, isSave, sizes)
        drawBitmapPerspective(ss.screenShootImage(isSave), coordinate)
        if (isGlareEnable) {
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
        val (left, top) = position.toSizeF()
        loadSync(source, isSave, sizes)?.let {
            drawBitmapSafely(it.resizeIfNotEqual(sizes), left, top)
            it.recycleSafely()
        }
    }
}