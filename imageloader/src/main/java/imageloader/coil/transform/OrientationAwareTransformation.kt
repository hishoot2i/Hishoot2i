package imageloader.coil.transform

import android.graphics.Bitmap
import android.graphics.Matrix
import coil.bitmap.BitmapPool
import coil.size.Size
import coil.transform.Transformation

object OrientationAwareTransformation : Transformation {
    override fun key() = "OrientationAwareTransformation"
    override suspend fun transform(pool: BitmapPool, input: Bitmap, size: Size): Bitmap =
        input.takeIf { it.width > it.height }?.run {
            val matrix = Matrix().apply { postRotate(90F) }
            Bitmap.createBitmap(this, 0, 0, width, height, matrix, false)
        } ?: input
}
