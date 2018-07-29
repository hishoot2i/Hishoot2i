package org.illegaller.ratabb.hishoot2i.ui.common.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.support.annotation.ColorInt
import android.util.AttributeSet
import android.view.View
import rbb.hishoot2i.common.custombitmap.AlphaPatternBitmap
import kotlin.LazyThreadSafetyMode.NONE

class ColorPreview : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(
        context: Context,
        attributeSet: AttributeSet,
        defStyle: Int
    ) : super(context, attributeSet, defStyle)

    @ColorInt
    var srcColor: Int = DEFAULT_SRC_COLOR
        set(value) {
            if (field != value) {
                field = value
                srcPaint.color = field
                invalidate(srcRect)
            }
        }
    var dstColor: Int = DEFAULT_DST_COLOR
        set(value) {
            if (field != value) {
                field = value
                dstPaint.color = field
                invalidate(dstRect)
            }
        }
    /*TODO: ?*/
    private val alphaTiledDrawable by lazy(NONE) {
        BitmapDrawable(
            context.resources,
            AlphaPatternBitmap(context).create(
                width = measuredWidth,
                height = measuredHeight
            )
        )
    }
    private val srcPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = srcColor
    }
    private val dstPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = dstColor
    }
    private val srcRect: Rect
        get() = Rect(0, 0, measuredWidth shr 1, measuredHeight)
    private val dstRect: Rect
        get() = Rect(measuredWidth shr 1, 0, measuredWidth, measuredHeight)

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        alphaTiledDrawable.setBounds(0, 0, measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        alphaTiledDrawable.draw(canvas)
        canvas.drawRect(srcRect, srcPaint)
        canvas.drawRect(dstRect, dstPaint)
    }

    companion object {
        private const val DEFAULT_SRC_COLOR: Int = 0x350000FF
        private const val DEFAULT_DST_COLOR: Int = 0x35FF00FF
    }
}