package org.illegaller.ratabb.hishoot2i.ui.common.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.SoundEffectConstants
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.get
import common.content.dp2px
import common.graphics.halfAlpha
import common.graphics.lightOrDarkContrast
import common.graphics.sizes
import entity.Sizes
import entity.SizesF

class CoreImagePreview @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatImageView(context, attributeSet, defStyle) {

    private var isPipetteActive: Boolean = false
    private val paintPipette = Paint(Paint.ANTI_ALIAS_FLAG)

    @ColorInt
    private var colorPipette: Int = 2147418112 // 0x7FFF0000 // Red half alpha | Fallback
    private val colorPipetteStroke: Int
        get() = colorPipette.lightOrDarkContrast.halfAlpha
    private var center = SizesF.ZERO
    private val radius by lazy { context.dp2px(16F) }
    private val stroke by lazy { context.dp2px(2F) }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        performClick()
        return isPipetteActive && when (event.action) {
            ACTION_DOWN, ACTION_MOVE -> handleEventTouch(event)
            else -> false
        }
    }

    override fun performClick(): Boolean {
        val handle = super.performClick()
        if (!handle) playSoundEffect(SoundEffectConstants.CLICK)
        return handle
    }

    fun startPipette(@ColorInt color: Int) {
        isPipetteActive = true
        if (color != 0) colorPipette = color
        center = Sizes(measuredWidth, measuredHeight).toSizeF() / 2F
        invalidate()
    }

    @JvmOverloads
    fun stopPipette(callback: (Int) -> Unit = {}) {
        isPipetteActive = false
        callback(colorPipette) //
        invalidate()
    }

    private fun handleEventTouch(event: MotionEvent): Boolean = colorPixel(event.x, event.y)?.let {
        colorPipette = it
        center = SizesF(event.x, event.y) // set center from event
        invalidate()
    } != null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isPipetteActive && colorPipette != 0) {
            paintPipette.apply {
                style = Paint.Style.FILL
                color = colorPipette
            }
            canvas.drawCircle(center.x, center.y, radius, paintPipette)
            paintPipette.apply {
                style = Paint.Style.STROKE
                strokeWidth = stroke
                color = colorPipetteStroke
            }
            canvas.drawCircle(center.x, center.y, radius, paintPipette)
        }
    }

    @ColorInt
    private fun colorPixel(x: Float, y: Float): Int? {
        val (bmpX, bmpY) = (drawable as BitmapDrawable).bitmap.sizes
        val mappedPoint = floatArrayOf(x, y)
        Matrix().apply {
            imageMatrix.invert(this)
            mapPoints(mappedPoint)
        }
        val (width, height) = drawable.bounds.run { width() to height() }
        val pX = ((mappedPoint[0] / width) * bmpX).toInt()
        val pY = ((mappedPoint[1] / height) * bmpY).toInt()
        return if (pX in 0 until bmpX && pY in 0 until bmpY) {
            (drawable as BitmapDrawable).bitmap[pX, pY]
        } else null
    }
}
