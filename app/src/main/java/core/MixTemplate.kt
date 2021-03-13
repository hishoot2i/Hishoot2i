package core

import android.graphics.Bitmap
import entity.ImageSourcePath
import template.Template

interface MixTemplate {
    suspend fun Bitmap.drawMixing(
        template: Template,
        config: Config,
        path: ImageSourcePath,
        isDoubleScreen: Boolean
    ): Bitmap

    data class Config(val isFrame: Boolean, val isGlare: Boolean, val isShadow: Boolean)
}
