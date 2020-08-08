package org.illegaller.ratabb.hishoot2i.data.pref

import com.chibatching.kotpref.KotprefModel
import org.illegaller.ratabb.hishoot2i.R

class AppPref : KotprefModel() {
    // Default App Preferences file name.
    override val kotprefName: String = "${context.packageName}_preferences"
    // BackgroundTool
    var backgroundModeId by intPref(default = entity.BackgroundMode.Color.id)
    var backgroundColorInt by intPref(default = BG_COLOR)
    var backgroundImageBlurEnable by booleanPref(default = false)
    var backgroundImageBlurRadius by intPref(default = BG_BLUR_RADIUS)
    var backgroundImageOptionId by intPref(default = R.id.toolBackgroundImageOptionScaleFill)
    // ScreenTool
    var doubleScreenEnable by booleanPref(default = false)
    // TemplateTool
    var templateCurrentId by stringPref(default = template.TemplateConstants.DEFAULT_TEMPLATE_ID)
    var templateFrameEnable by booleanPref(default = true)
    var templateGlareEnable by booleanPref(default = true)
    var templateShadowEnable by booleanPref(default = true)
    // BadgeTool
    var badgeColor by intPref(default = BADGE_COLOR)
    var badgeEnable by booleanPref(default = true)
    var badgeSize by intPref(default = BADGE_SIZE)
    var badgePositionId by intPref(default = entity.BadgePosition.CenterBottom.id)
    var badgeText by stringPref(default = DEF_BADGE_TEXT)
    /* path plus quotation `"`  */
    var badgeTypeface by stringPref()
        private set
    var badgeTypefacePath
        get() = badgeTypeface.removeSurrounding(QUOTE)
        set(value) {
            val valuePlusQuote = "$QUOTE$value$QUOTE"
            if (badgeTypeface != valuePlusQuote) {
                badgeTypeface = valuePlusQuote
            }
        }
    var customFontPath by nullableStringPref()
    var systemFontEnable by booleanPref(default = false)
    // Template Manager
    val templateFavSet by stringSetPref { setOf(template.TemplateConstants.DEFAULT_TEMPLATE_ID) }
    var templateSortId by intPref(default = template.TemplateComparator.NAME_ASC_ID)
    //
    var appRunningCount by intPref()
    var appVersionLast by nullableStringPref()
    // Setting
    var appThemesDarkEnable by booleanPref(default = true)

    companion object {
        private const val BG_BLUR_RADIUS = 25
        private const val BG_COLOR = 0xFF00FFFF.toInt() // Color.CYAN
        private const val BADGE_COLOR = 0xFFFF0000.toInt() // Color.RED
        private const val BADGE_SIZE = 24
        private const val QUOTE = "\""
        const val DEF_BADGE_TEXT = "HISHOOT"
    }
}
