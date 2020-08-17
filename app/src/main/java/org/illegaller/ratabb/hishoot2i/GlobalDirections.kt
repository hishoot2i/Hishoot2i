package org.illegaller.ratabb.hishoot2i

import androidx.navigation.NavDirections
import org.illegaller.ratabb.hishoot2i.NavigationDirections.Companion.actionGlobalCrop
import org.illegaller.ratabb.hishoot2i.NavigationDirections.Companion.actionGlobalSetting
import org.illegaller.ratabb.hishoot2i.NavigationDirections.Companion.actionGlobalTemplate

object GlobalDirections {
    @JvmStatic
    fun setting(): NavDirections = actionGlobalSetting()

    @JvmStatic
    fun template(): NavDirections = actionGlobalTemplate()

    @JvmOverloads
    @JvmStatic
    fun crop(path: String? = null, ratioX: Int = 0, ratioY: Int = 0): NavDirections =
        actionGlobalCrop(path, ratioX, ratioY)
}
