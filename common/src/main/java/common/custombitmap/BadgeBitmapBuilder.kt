package common.custombitmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.support.annotation.ColorInt
import android.text.TextPaint
import common.ext.POINT_OF_FIVE
import common.ext.density
import common.ext.graphics.applyCanvas
import common.ext.graphics.createFromFileOrDefault
import common.ext.graphics.halfAlpha
import common.ext.scaledDensity
import kotlin.LazyThreadSafetyMode.NONE

class BadgeBitmapBuilder(context: Context) {
    private val rectF: RectF = RectF()
    private val density by lazy(NONE) { context.density }
    private val scaledDensity by lazy(NONE) { context.scaledDensity }
    private val padding get() = DEF_PADDING * density + POINT_OF_FIVE
    private val cornerRadius get() = DEF_CORNER_RADIUS * density + POINT_OF_FIVE
    private val textPaint: TextPaint = TextPaint(TEXT_PAINT_FLAG).apply {
        if (SDK_INT >= LOLLIPOP) isElegantTextHeight = true //
        isFakeBoldText = true
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }
    private val backgroundPaint: Paint = Paint(DEF_PAINT_FLAG).apply {
        style = Paint.Style.FILL
    }

    fun build(config: Config): Bitmap {
        val textUpperCase = config.text.toUpperCase()
        val localBound = Rect()
        textPaint.apply {
            typeface = config.typeFacePath.createFromFileOrDefault()
            textSize = config.size * scaledDensity + POINT_OF_FIVE
            getTextBounds(textUpperCase, 0, textUpperCase.length, localBound)
        }
        val (width, height) = entity.Sizes(
            textPaint.measureText(textUpperCase).toInt(),
            localBound.height()
        ) + (padding * 2 + POINT_OF_FIVE).toInt()
        backgroundPaint.color = config.color.halfAlpha
        rectF.apply {
            right = width.toFloat()
            bottom = height.toFloat()
        }
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            .applyCanvas {
                drawRoundRect(rectF, cornerRadius, cornerRadius, backgroundPaint)
                drawText(textUpperCase, padding, height - padding, textPaint)
            }
    }

    data class Config(
        val text: String,
        val typeFacePath: String,
        val size: Int,
        @ColorInt val color: Int
    )

    companion object {
        private const val DEF_PADDING = 16
        private const val DEF_CORNER_RADIUS = 8
        private const val DEF_PAINT_FLAG = Paint.ANTI_ALIAS_FLAG
        private const val TEXT_PAINT_FLAG =
            DEF_PAINT_FLAG.or(Paint.SUBPIXEL_TEXT_FLAG).or(Paint.LINEAR_TEXT_FLAG)
    }
}