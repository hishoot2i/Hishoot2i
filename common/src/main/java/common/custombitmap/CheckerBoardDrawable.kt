package common.custombitmap

import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Bitmap.createBitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.Shader.TileMode.REPEAT
import android.graphics.drawable.Drawable
import androidx.annotation.Px
import common.ext.graphics.applyCanvas

class CheckerBoardDrawable(@Px size: Int) : Drawable() {
    private val paint = Paint().apply { shader = initTiles(size) }
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

    private companion object {
        const val COLOR_DARK: Int = 0xFFC3C3C3.toInt()
        const val COLOR_LIGHT: Int = 0xFFF0F0F0.toInt()

        fun initTiles(size: Int): BitmapShader {
            require(size > 0) { "Size should be positive integer, size=$size" }
            val tileSize = size * 2
            val tileRect = Rect(0, 0, size, size)
            val tilePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
            val tileBitmap = createBitmap(tileSize, tileSize, RGB_565).applyCanvas {
                drawColor(COLOR_DARK)
                tilePaint.color = COLOR_LIGHT
                drawRect(tileRect, tilePaint)
                tileRect.offset(size, size)
                drawRect(tileRect, tilePaint)
            }
            return BitmapShader(tileBitmap, REPEAT, REPEAT)
        }
    }
}
