package core

import android.graphics.Bitmap.CompressFormat
import entity.BackgroundMode
import entity.BadgePosition
import entity.ImageOption

interface CoreRequest {
    val backgroundMode: BackgroundMode
    val imageOption: ImageOption
    val backgroundColorInt: Int
    val backgroundImageBlurEnable: Boolean
    val backgroundImageBlurRadius: Int

    val badgePosition: BadgePosition
    val badgeEnable: Boolean
    val badgeConfig: BadgeBuilder.Config

    val doubleScreenEnable: Boolean

    val mixTemplateConfig: MixTemplate.Config

    val compressFormat: CompressFormat
    val saveQuality: Int
}
