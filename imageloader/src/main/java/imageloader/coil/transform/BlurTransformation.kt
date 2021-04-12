package imageloader.coil.transform

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Canvas
import androidx.annotation.IntRange
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.scale
import coil.bitmap.BitmapPool
import coil.size.Size
import coil.transform.Transformation
import com.enrique.stackblur.JavaBlurProcess
import common.graphics.drawBitmapSafely
import common.graphics.sizes

/**
 * [BlurTransformation]
 *
 *  NOTE: replace this with [coil.transform.BlurTransformation]
 *   blocking by Requires API 18 [RenderScript][android.renderscript.RenderScript]
 **/
class BlurTransformation(
    @IntRange(from = 0L, to = 100L) private val blurRadius: Int
) : Transformation {
    init {
        require(blurRadius in 0..100) { "blurRadius must be in [0, 100]." }
    }

    override fun key() = "BlurTransformation-$blurRadius"
    override suspend fun transform(pool: BitmapPool, input: Bitmap, size: Size): Bitmap {
        val (width, height) = input.sizes
        return pool.get(width, height, input.config ?: ARGB_8888).applyCanvas {
            drawBitmapBlur(input, blurRadius)
        }
    }

    private fun Canvas.drawBitmapBlur(
        bitmap: Bitmap?,
        @IntRange(from = 0L, to = 100L) radius: Int
    ) {
        if (null != bitmap && !bitmap.isRecycled) {
            val sizes = bitmap.sizes
            val halfSizes = sizes / 2
            JavaBlurProcess().blur(
                bitmap.scale(halfSizes.x, halfSizes.y),
                radius.coerceIn(0..100)
            )?.let { drawBitmapSafely(it.scale(sizes.x, sizes.y)) }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as BlurTransformation
        if (blurRadius != other.blurRadius) return false
        return true
    }

    override fun hashCode(): Int = blurRadius
    override fun toString(): String = "BlurTransformation(blurRadius=$blurRadius)"
}
