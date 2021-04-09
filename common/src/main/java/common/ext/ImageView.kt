package common.ext

import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import androidx.annotation.ColorInt
import common.ext.graphics.sizes

@ColorInt
fun ImageView.colorFromView(x: Float, y: Float, @ColorInt fallback: Int): Int {
    val (bmpX, bmpY) = (drawable as? BitmapDrawable)?.bitmap?.sizes ?: return fallback
    val mappedPoint = floatArrayOf(x, y)
    Matrix().apply {
        imageMatrix.invert(this)
        mapPoints(mappedPoint)
    }
    val (width, height) = drawable.bounds.run { width() to height() }
    val pX = ((mappedPoint[0] / width) * bmpX).toInt().coerceIn(0 until bmpX)
    val pY = ((mappedPoint[1] / height) * bmpY).toInt().coerceIn(0 until bmpY)
    return runCatching { (drawable as BitmapDrawable).bitmap.getPixel(pX, pY) }
        .getOrDefault(fallback)
}
