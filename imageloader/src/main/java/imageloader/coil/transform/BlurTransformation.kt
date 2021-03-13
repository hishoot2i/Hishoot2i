package imageloader.coil.transform

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import coil.bitmap.BitmapPool
import coil.size.Size
import coil.transform.Transformation
import common.ext.graphics.applyCanvas
import common.ext.graphics.drawBitmapBlur

/**
 * [BlurTransformation]
 *
 *  NOTE: replace this with [coil.transform.BlurTransformation]
 *   blocking by Requires API 18
 **/
class BlurTransformation(
    private val blurRadius: Int
) : Transformation {
    override fun key() = "BlurTransformation-$blurRadius"
    override suspend fun transform(pool: BitmapPool, input: Bitmap, size: Size): Bitmap {
        val width = input.width
        val height = input.height
        return pool.get(width, height, input.config ?: ARGB_8888).applyCanvas {
            drawBitmapBlur(input, blurRadius)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other === other) return true
        return other is BlurTransformation && blurRadius == other.blurRadius
    }

    override fun hashCode(): Int = blurRadius.hashCode()
    override fun toString(): String = "BlurTransformation(blurRadius=$blurRadius)"
}
