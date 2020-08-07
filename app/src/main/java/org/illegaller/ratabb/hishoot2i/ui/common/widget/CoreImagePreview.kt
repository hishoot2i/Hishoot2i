@file:Suppress("DEPRECATION")

package org.illegaller.ratabb.hishoot2i.ui.common.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageView
import common.ext.dp2px
import common.ext.graphics.darker
import kotlin.LazyThreadSafetyMode.NONE

class CoreImagePreview : AppCompatImageView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(
        context: Context,
        attributeSet: AttributeSet,
        defStyle: Int
    ) : super(context, attributeSet, defStyle)

    @Suppress("DEPRECATION")
    private var isPipetteActive: Boolean = isDrawingCacheEnabled // false
        set(value) {
            if (field != value) {
                field = value
                isDrawingCacheEnabled = field
                if (field) buildDrawingCache(true) else destroyDrawingCache()
            }
        }
    @ColorInt
    private var colorPipette: Int = DEFAULT_COLOR_PIPETTE
        set(value) {
            field = value
            paintFill.color = value
            paintStroke.color = value.darker
        }
    private val paintFill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = colorPipette
    }
    private val paintStroke: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = context.dp2px(STROKE_DP)
        color = colorPipette.darker
    }
    private var center = entity.SizesF.ZERO
    private val radius: Float by lazy(NONE) { context.dp2px(RADIUS_DP) }
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return isPipetteActive && when (event.action) {
            MotionEvent.ACTION_DOWN -> handleEventTouch(event)
            MotionEvent.ACTION_MOVE -> handleEventTouch(event)
            else -> false
        }
    }

    fun startPipette(@ColorInt color: Int) {
        isPipetteActive = true
        colorPipette = color
        center = (entity.Sizes(width, height) / 2).toSizeF() // initialize center
        invalidatePipette()
    }

    @JvmOverloads
    fun stopPipette(callback: (color: Int) -> Unit = {}) {
        isPipetteActive = false
        callback(colorPipette) //
        invalidatePipette()
    }

    // TODO: limit position or avoid color transparent ?
    private fun handleEventTouch(event: MotionEvent): Boolean {
        center = entity.SizesF(event.x, event.y)
        val (x, y) = center.toSize()
        colorPipette = getDrawingCache(true).getPixel(x, y)
        invalidatePipette()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas) //
        if (isPipetteActive && colorPipette != 0 /* 0 = transparent | out of bound*/) {
            canvas.drawCircle(center.x, center.y, radius, paintFill)
            canvas.drawCircle(center.x, center.y, radius, paintStroke)
        }
    }

    private fun invalidatePipette() {
        val (left, top) = (center - radius).toSize()
        val (right, bottom) = (center + radius).toSize()
        invalidate(left, top, right, bottom)
    }

    companion object {
        private const val RADIUS_DP = 16
        private const val STROKE_DP = 2
        private const val DEFAULT_COLOR_PIPETTE: Int = 0x7FFF0000 // Red half alpha | Fallback
    }
}