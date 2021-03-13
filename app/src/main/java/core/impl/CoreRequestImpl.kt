package core.impl

import android.graphics.Bitmap
import android.graphics.Typeface
import common.ext.graphics.createFromFileOrDefault
import core.BadgeBuilder
import core.CoreRequest
import core.MixTemplate
import entity.BackgroundMode
import entity.BadgePosition
import entity.ImageOption
import org.illegaller.ratabb.hishoot2i.data.pref.BackgroundToolPref
import org.illegaller.ratabb.hishoot2i.data.pref.BadgeToolPref
import org.illegaller.ratabb.hishoot2i.data.pref.ScreenToolPref
import org.illegaller.ratabb.hishoot2i.data.pref.SettingPref
import org.illegaller.ratabb.hishoot2i.data.pref.TemplateToolPref
import javax.inject.Inject

class CoreRequestImpl @Inject constructor(
    private val backgroundToolPref: BackgroundToolPref,
    private val badgeToolPref: BadgeToolPref,
    private val screenToolPref: ScreenToolPref,
    private val templateToolPref: TemplateToolPref,
    private val settingPref: SettingPref
) : CoreRequest {
    override val backgroundMode: BackgroundMode
        get() = backgroundToolPref.backgroundMode
    override val imageOption: ImageOption
        get() = backgroundToolPref.imageOption
    override val backgroundColorInt: Int
        get() = backgroundToolPref.backgroundColorInt
    override val backgroundImageBlurEnable: Boolean
        get() = backgroundToolPref.backgroundImageBlurEnable
    override val backgroundImageBlurRadius: Int
        get() = backgroundToolPref.backgroundImageBlurRadius

    private val badgeTypeface: Typeface
        get() = when (val path = badgeToolPref.badgeTypefacePath) {
            null, "DEFAULT" -> Typeface.DEFAULT
            else -> path.createFromFileOrDefault()
        }
    override val badgePosition: BadgePosition
        get() = badgeToolPref.badgePosition
    override val badgeEnable: Boolean
        get() = badgeToolPref.badgeEnable
    override val badgeConfig: BadgeBuilder.Config
        get() = BadgeBuilder.Config(
            badgeToolPref.badgeText,
            badgeTypeface,
            badgeToolPref.badgeSize,
            badgeToolPref.badgeColor
        )

    override val doubleScreenEnable: Boolean
        get() = screenToolPref.doubleScreenEnable

    override val mixTemplateConfig: MixTemplate.Config
        get() = MixTemplate.Config(
            templateToolPref.templateFrameEnable,
            templateToolPref.templateGlareEnable,
            templateToolPref.templateShadowEnable
        )

    override val compressFormat: Bitmap.CompressFormat
        get() = settingPref.compressFormat
    override val saveQuality: Int
        get() = settingPref.saveQuality
}
