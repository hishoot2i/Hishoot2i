package imageloader.coil.transform

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import coil.bitmap.BitmapPool
import coil.size.Size
import coil.transform.Transformation
import common.ext.graphics.scaleCenterCrop
import entity.Sizes

class ScaleCenterTransformation(
    private val reqSizes: Sizes,
) : Transformation {
    override fun key(): String = "ScaleCenterTransformations-$reqSizes"

    override suspend fun transform(pool: BitmapPool, input: Bitmap, size: Size): Bitmap =
        input.scaleCenterCrop(reqSizes, input.config ?: ARGB_8888)
}
