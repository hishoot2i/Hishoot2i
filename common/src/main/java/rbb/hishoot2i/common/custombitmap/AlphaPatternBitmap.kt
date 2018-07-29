package rbb.hishoot2i.common.custombitmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Paint
import android.graphics.Rect
import android.support.annotation.Dimension
import android.support.annotation.Px
import rbb.hishoot2i.common.entity.Sizes
import rbb.hishoot2i.common.ext.graphics.applyCanvas
import rbb.hishoot2i.common.ext.graphics.createBitmap
import rbb.hishoot2i.common.ext.deviceHeight
import rbb.hishoot2i.common.ext.deviceWidth
import rbb.hishoot2i.common.ext.dp2px

class AlphaPatternBitmap(val context: Context) {
    @JvmOverloads
    fun create(
        @Dimension(unit = Dimension.DP) rectangleSize: Int = DEFAULT_RECTANGLE_SIZE_DP,
        @Px width: Int = context.deviceWidth,
        @Px height: Int = context.deviceHeight
    ): Bitmap = create(rectangleSize, Sizes(width, height))

    fun create(@Dimension(unit = Dimension.DP) rectangleSize: Int, sizes: Sizes): Bitmap {
        val rectangleSizePx = context.dp2px(rectangleSize).toInt()
        val (numRectanglesHorizontal, numRectanglesVertical) = sizes / rectangleSizePx
        val rect = Rect()
        return sizes.createBitmap(config = RGB_565).applyCanvas {
            var verticalStartWhite = true
            for (vertical in 0..numRectanglesVertical) {
                var isWhite = verticalStartWhite
                for (horizontal in 0..numRectanglesHorizontal) {
                    drawRect(
                        rect.calculateRect(vertical, horizontal, rectangleSizePx),
                        if (isWhite) paintWhite else paintGray
                    )
                    isWhite = !isWhite
                }
                verticalStartWhite = !verticalStartWhite
            }
        }
    }

    private fun Rect.calculateRect(vertical: Int, horizontal: Int, size: Int): Rect {
        val left = horizontal * size
        val top = vertical * size
        val right = left + size
        val bottom = top + size
        this.set(left, top, right, bottom)
        return this
    }

    companion object {
        private const val DEFAULT_RECTANGLE_SIZE_DP = 10
        private val paintWhite = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFFFFFF.toInt()
            style = Paint.Style.FILL
        }
        private val paintGray = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFCBCBCB.toInt()
            style = Paint.Style.FILL
        }
    }
}