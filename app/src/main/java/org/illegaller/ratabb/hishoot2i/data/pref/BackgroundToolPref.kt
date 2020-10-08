package org.illegaller.ratabb.hishoot2i.data.pref

import entity.BackgroundMode
import entity.ImageOption
import io.reactivex.rxjava3.core.Flowable

interface BackgroundToolPref {
    var backgroundMode: BackgroundMode
    var imageOption: ImageOption
    var backgroundColorInt: Int
    var backgroundImageBlurEnable: Boolean
    var backgroundImageBlurRadius: Int
    val mainFlow: List<Flowable<*>>
}
