package common.custombitmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Build.VERSION.SDK_INT
import android.text.TextPaint
import androidx.annotation.ColorInt
import common.ext.POINT_OF_FIVE
import common.ext.dp2px
import common.ext.graphics.applyCanvas
import common.ext.graphics.halfAlpha
import common.ext.textSize
import entity.Sizes
import java.util.Locale

class BadgeBitmapBuilder(context: Context) {
    private val rectF: RectF = RectF()

    private val padding by lazy { context.dp2px(16F) }
    private val cornerRadius by lazy { context.dp2px(8F) }
    private val textPaint: TextPaint = TextPaint().apply {
        flags = (Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG or Paint.LINEAR_TEXT_FLAG)
        if (SDK_INT >= 21) isElegantTextHeight = true //
        isFakeBoldText = true
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }
    private val backgroundPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val textSizes = { value: Float -> context.textSize(value) }

    fun build(config: Config): Bitmap {
        val textUpperCase = config.text.toUpperCase(Locale.ROOT)
        val localBound = Rect()
        textPaint.apply {
            typeface = config.typeFace
            textSize = textSizes(config.size)
            getTextBounds(textUpperCase, 0, textUpperCase.length, localBound)
        }
        val (width, height) = Sizes(
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
        val typeFace: Typeface,
        val size: Float,
        @ColorInt val color: Int
    )
}
