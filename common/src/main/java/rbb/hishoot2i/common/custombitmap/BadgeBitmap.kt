package rbb.hishoot2i.common.custombitmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.support.annotation.ColorInt
import android.text.TextPaint
import rbb.hishoot2i.common.entity.Sizes
import rbb.hishoot2i.common.ext.POINT_OF_FIVE
import rbb.hishoot2i.common.ext.graphics.applyCanvas
import rbb.hishoot2i.common.ext.graphics.darker
import rbb.hishoot2i.common.ext.dp2px
import rbb.hishoot2i.common.ext.graphics.halfAlpha
import rbb.hishoot2i.common.ext.textSize

class BadgeBitmap(val context: Context) {
    private val rectF: RectF = RectF()
    private val padding = context.dp2px(DEF_PADDING)
    private val shadowStroke = context.dp2px(DEF_SHADOW_STROKE)
    private val cornerRadius = context.dp2px(DEF_CORNER_RADIUS)
    private val textPaint: TextPaint = TextPaint(TEXT_PAINT_FLAG).apply {
        if (SDK_INT >= LOLLIPOP) isElegantTextHeight = true //
        isFakeBoldText = true
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }
    private val backgroundPaint: Paint = Paint(DEF_PAINT_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val shadowPaint: Paint = Paint(DEF_PAINT_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = shadowStroke
    }

    fun create(config: Config): Bitmap {
        val textUpperCase = config.text.toUpperCase()
        val localBound = Rect()
        textPaint.apply {
            typeface = config.typeFace
            textSize = context.textSize(config.size) //
            getTextBounds(textUpperCase, 0, textUpperCase.length, localBound)
        }
        val (width, height) = Sizes(
            textPaint.measureText(textUpperCase).toInt(),
            localBound.height()
        ) + (padding * 2 + POINT_OF_FIVE).toInt()
        backgroundPaint.color = config.color.halfAlpha
        shadowPaint.color = config.color.darker.halfAlpha
        rectF.apply {
            right = width.toFloat()
            bottom = height.toFloat()
        }
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            .applyCanvas {
                drawRoundRect(rectF, cornerRadius, cornerRadius, backgroundPaint)
                drawRoundRect(rectF, cornerRadius, cornerRadius, shadowPaint)
                drawText(textUpperCase, padding, height - padding, textPaint)
            }
    }

    data class Config(
        val text: String,
        val typeFace: Typeface,
        val size: Int,
        @ColorInt val color: Int
    )

    companion object {
        private const val DEF_PADDING = 16
        private const val DEF_CORNER_RADIUS = 8
        private const val DEF_SHADOW_STROKE = 2
        private const val DEF_PAINT_FLAG = Paint.ANTI_ALIAS_FLAG
        private const val TEXT_PAINT_FLAG =
            DEF_PAINT_FLAG.or(Paint.SUBPIXEL_TEXT_FLAG).or(Paint.LINEAR_TEXT_FLAG)
    }
}