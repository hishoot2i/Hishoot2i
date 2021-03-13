package imageloader.coil.transform

import android.graphics.Bitmap
import coil.bitmap.BitmapPool
import coil.size.Size
import coil.transform.Transformation
import common.ext.graphics.isLandScape
import common.ext.graphics.rotate

object OrientationAwareTransformation : Transformation {
    override fun key() = "OrientationAwareTransformation"
    override suspend fun transform(pool: BitmapPool, input: Bitmap, size: Size): Bitmap =
        input.takeIf(Bitmap::isLandScape)?.rotate() ?: input
}
