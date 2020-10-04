package org.illegaller.ratabb.hishoot2i.ui.common.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.SoundEffectConstants
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageView
import common.ext.colorFromBitmap
import common.ext.dp2px
import common.ext.graphics.halfAlpha
import common.ext.graphics.lightOrDarkContrast
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

    private fun handleEventTouch(event: MotionEvent): Boolean {
        center = SizesF(event.x, event.y) // set center from event
        return colorFromBitmap(event.x, event.y, 0)
            .takeIf { it != 0 }?.let {
                colorPipette = it
                invalidate()
                true
            } == true
    }

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
}
