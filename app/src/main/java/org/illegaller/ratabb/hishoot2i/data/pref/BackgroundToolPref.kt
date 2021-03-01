package org.illegaller.ratabb.hishoot2i.data.pref

import entity.BackgroundMode
import entity.ImageOption
import kotlinx.coroutines.flow.Flow

interface BackgroundToolPref {
    var backgroundMode: BackgroundMode
    var imageOption: ImageOption
    var backgroundColorInt: Int
    var backgroundImageBlurEnable: Boolean
    var backgroundImageBlurRadius: Int
    val mainFlow: List<Flow<*>>
}
