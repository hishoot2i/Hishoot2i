package core

import android.graphics.Bitmap
import android.graphics.Typeface
import androidx.annotation.ColorInt
import entity.BadgePosition

interface BadgeBuilder {
    suspend fun Bitmap.drawBadge(
        isEnable: Boolean,
        position: BadgePosition,
        config: Config
    ): Bitmap

    class Config(
        val text: String,
        val typeFace: Typeface,
        val size: Float,
        @ColorInt val color: Int
    )
}
