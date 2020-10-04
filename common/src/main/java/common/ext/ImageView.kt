package common.ext

import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import androidx.annotation.ColorInt
import common.ext.graphics.sizes
import entity.Sizes
import entity.SizesF
import timber.log.Timber

@ColorInt
fun ImageView.colorFromBitmap(x: Float, y: Float, @ColorInt fallback: Int): Int {
    val bmpSizes: Sizes = (drawable as? BitmapDrawable)?.bitmap?.sizes ?: return fallback
    val mappedPoint = floatArrayOf(x, y)
    Matrix().apply {
        imageMatrix.invert(this)
        mapPoints(mappedPoint)
    }
    return drawable.bounds.run {
        // scale
        val (sX, sY) = (mappedPoint[0] / width() to mappedPoint[1] / height())
        // point
        var (pX, pY) = SizesF(sX * bmpSizes.x, sY * bmpSizes.y).toSize()
        // range
        pX = pX.coerceIn(0..bmpSizes.x)
        pY = pY.coerceIn(0..bmpSizes.y)
        try {
            (drawable as BitmapDrawable).bitmap.getPixel(pX, pY)
        } catch (e: Exception) {
            Timber.e(e, "point : $pX $pY bitmap size: $bmpSizes")
            fallback
        }
    }
}
