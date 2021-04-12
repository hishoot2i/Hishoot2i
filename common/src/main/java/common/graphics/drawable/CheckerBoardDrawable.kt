package common.graphics.drawable

import android.content.Context
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.Shader.TileMode.REPEAT
import android.graphics.drawable.Drawable
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.graphics.plus
import common.content.dp2px
import common.graphics.toBitmapShader
import kotlin.math.roundToInt

class CheckerBoardDrawable private constructor(
    private val paint: Paint
) : Drawable() {
    override fun draw(canvas: Canvas) {
        canvas.drawPaint(paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE

    companion object {
        fun createWith(context: Context, dp: Float) = CheckerBoardDrawable(
            Paint().apply {
                shader = checkerBoard(context.dp2px(dp).roundToInt())
            }
        )

        private fun checkerBoard(size: Int) = (size * 2).let { doubleSize ->
            createBitmap(doubleSize, doubleSize, RGB_565).applyCanvas {
                drawColor(-3947581) // 0xFFC3C3C3
                val rect = Rect(0, 0, size, size)
                val localPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.FILL
                    color = -986896 // 0xFFF0F0F0
                }
                drawRect(rect, localPaint)
                drawRect(rect + size, localPaint)
            }
        }.toBitmapShader(REPEAT, REPEAT)
    }
}
