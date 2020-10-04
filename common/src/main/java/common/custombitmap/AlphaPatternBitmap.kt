package common.custombitmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Color
import android.graphics.Color.GRAY
import android.graphics.Color.WHITE
import android.graphics.Paint
import common.ext.POINT_OF_FIVE
import common.ext.displayMetrics
import common.ext.graphics.applyCanvas
import common.ext.graphics.createBitmap
import entity.Sizes
import entity.SizesF
import kotlin.math.roundToInt

class AlphaPatternBitmap(context: Context) {
    private val density: Float by lazy { context.displayMetrics.density }
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    fun create(sizes: Sizes): Bitmap {
        val rectSizePx = (density * 10F + POINT_OF_FIVE).roundToInt()
        val (nRectH, nRectV) = sizes / rectSizePx
        return sizes.createBitmap(config = RGB_565).applyCanvas {
            var vStartWhite = true
            for (vertical in 0..nRectV) {
                var isWhite = vStartWhite
                for (horizontal in 0..nRectH) {
                    paint.apply { color = if (isWhite) WHITE else GRAY }
                    val (left, top) = (Sizes(horizontal, vertical) * rectSizePx).toSizeF()
                    val (right, bottom) = SizesF(left, top) + rectSizePx.toFloat()
                    drawRect(left, top, right, bottom, paint)
                    isWhite = !isWhite
                }
                vStartWhite = !vStartWhite
            }
        }
    }
}

