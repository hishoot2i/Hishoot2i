package core.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.os.Build
import android.text.TextPaint
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import common.content.dp2px
import common.content.textSize
import common.graphics.drawBitmapSafely
import common.graphics.halfAlpha
import common.graphics.sizes
import core.BadgeBuilder
import dagger.hilt.android.qualifiers.ApplicationContext
import entity.BadgePosition
import entity.Sizes
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt

class BadgeBuilderImpl @Inject constructor(
    @ApplicationContext context: Context
) : BadgeBuilder {
    private val rectF: RectF = RectF()
    private val padding by lazy { context.dp2px(16F) }
    private val cornerRadius by lazy { context.dp2px(8F) }
    private val textPaint: TextPaint = TextPaint().apply {
        flags = (Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG or Paint.LINEAR_TEXT_FLAG)
        if (Build.VERSION.SDK_INT >= 21) isElegantTextHeight = true //
        isFakeBoldText = true
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }
    private val backgroundPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val textSizes: (Float) -> Float = { value -> context.textSize(value) }

    //
    private val badgeBitmapPadding by lazy { context.dp2px(10F).roundToInt() }

    override suspend fun Bitmap.drawBadge(
        isEnable: Boolean,
        position: BadgePosition,
        config: BadgeBuilder.Config
    ) = applyCanvas {
        if (!isEnable) return@applyCanvas
        val badgeBitmap = buildWith(config)
        val (left, top) = position.getValue(sizes, badgeBitmap.sizes, badgeBitmapPadding)
        applyCanvas { drawBitmapSafely(badgeBitmap, left, top) }
    }

    private fun buildWith(config: BadgeBuilder.Config): Bitmap {
        val textUpperCase = config.text.toUpperCase(Locale.ROOT)
        val localBound = Rect()
        textPaint.apply {
            typeface = config.typeFace
            textSize = textSizes(config.size)
            getTextBounds(textUpperCase, 0, textUpperCase.length, localBound)
        }
        val sizes = Sizes(
            textPaint.measureText(textUpperCase).toInt(),
            localBound.height()
        ) + (padding * 2 + 0.5F).toInt()
        backgroundPaint.color = config.color.halfAlpha
        rectF.apply {
            right = sizes.x.toFloat()
            bottom = sizes.y.toFloat()
        }
        return createBitmap(sizes.x, sizes.y).applyCanvas {
            drawRoundRect(rectF, cornerRadius, cornerRadius, backgroundPaint)
            drawText(textUpperCase, padding, height - padding, textPaint)
        }
    }
}
