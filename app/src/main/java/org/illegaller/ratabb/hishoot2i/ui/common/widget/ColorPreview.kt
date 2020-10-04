package org.illegaller.ratabb.hishoot2i.ui.common.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import common.custombitmap.AlphaPatternBitmap

class ColorPreview @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attr, defStyle) {

    fun initColor(color: Int) {
        srcColor = color
        dstColor = color
    }

    fun changeColor(color: Int) {
        dstColor = color
    }

    @ColorInt
    private var srcColor: Int = 889192703 // 0x350000FF
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    @ColorInt
    private var dstColor: Int = 905904383 // 0x35FF00FF
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    private val alphaTiledDrawable by lazy {
        BitmapDrawable(
            context.resources,
            AlphaPatternBitmap(context).create(sizes = entity.Sizes(measuredWidth, measuredHeight))
        )
    }
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
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
        paint.color = srcColor
        canvas.drawRect(srcRect, paint)
        paint.color = dstColor
        canvas.drawRect(dstRect, paint)
    }
}
