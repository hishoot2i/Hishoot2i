package common.custombitmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Paint
import android.graphics.Rect
import common.ext.POINT_OF_FIVE
import common.ext.density
import common.ext.graphics.applyCanvas
import common.ext.graphics.createBitmap
import kotlin.LazyThreadSafetyMode.NONE
import kotlin.math.roundToInt

class AlphaPatternBitmap(context: Context) {
    private val density by lazy(NONE) { context.density }
    fun create(sizes: entity.Sizes): Bitmap {
        val rectSizePx = (density * DEFAULT_RECTANGLE_SIZE_DP + POINT_OF_FIVE).roundToInt()
        val (nRectH, nRectV) = sizes / rectSizePx
        return sizes.createBitmap(config = RGB_565).applyCanvas {
            var vStartWhite = true
            for (vertical in 0..nRectV) {
                var isWhite = vStartWhite
                for (horizontal in 0..nRectH) {
                    paint.apply { color = if (isWhite) WHITE else GRAY }
                    drawRect(rect.calculateRect(vertical, horizontal, rectSizePx), paint)
                    isWhite = !isWhite
                }
                vStartWhite = !vStartWhite
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

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val rect = Rect()

    companion object {
        private const val DEFAULT_RECTANGLE_SIZE_DP = 10
        private const val WHITE = 0xFFFFFFFF.toInt()
        private const val GRAY = 0xFFCBCBCB.toInt()
    }
}
