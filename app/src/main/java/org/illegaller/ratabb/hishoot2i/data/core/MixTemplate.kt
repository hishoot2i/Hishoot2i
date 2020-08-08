package org.illegaller.ratabb.hishoot2i.data.core

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import common.custombitmap.AlphaPatternBitmap
import common.ext.deviceHeight
import common.ext.deviceWidth
import common.ext.exhaustive
import common.ext.graphics.applyCanvas
import common.ext.graphics.createBitmap
import common.ext.graphics.drawBitmapPerspective
import common.ext.graphics.drawBitmapSafely
import common.ext.graphics.drawable
import common.ext.graphics.recycleSafely
import common.ext.graphics.resizeIfNotEqual
import common.ext.graphics.sizes
import common.ext.graphics.toBitmap
import entity.Sizes
import imageloader.ImageLoader
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.data.pref.AppPref
import template.Template
import kotlin.LazyThreadSafetyMode.NONE

internal class MixTemplate(
    val appPref: AppPref,
    context: Context,
    imageLoader: ImageLoader
) : ImageLoader by imageLoader {
    private val alphaPatternBitmap by lazy(NONE) { AlphaPatternBitmap(context) }
    private val deviceSizes by lazy(NONE) { with(context) { Sizes(deviceWidth, deviceHeight) } }
    private val frameDefault by lazy { context.drawable(R.drawable.frame1) }
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
                is Template.Empty -> throw IllegalStateException("Unknown $id")
            }.exhaustive
        }
    }

    private fun String?.screenShootImage(isSave: Boolean): Bitmap = this?.let {
        loadSync(it, isSave, deviceSizes, /* isOrientationAware */true)
    } ?: alphaPatternBitmap.create(deviceSizes)

    private fun Bitmap.drawDefault(ss: String?, coordinate: FloatArray, isSave: Boolean): Bitmap =
        applyCanvas {
            drawBitmapPerspective(ss.screenShootImage(isSave), coordinate)
            val (width, height) = sizes
            frameDefault?.toBitmap(width, height)?.let { drawBitmapSafely(it); it.recycleSafely() }
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
