package entity

import androidx.annotation.IdRes
import common.ext.exhaustive
import entity.ImageOption.CENTER_CROP
import entity.ImageOption.MANUAL_CROP
import entity.ImageOption.SCALE_FILL
import org.illegaller.ratabb.hishoot2i.R.id.imageOption_CenterCrop
import org.illegaller.ratabb.hishoot2i.R.id.imageOption_ManualCrop
import org.illegaller.ratabb.hishoot2i.R.id.imageOption_ScaleFill

@get:IdRes
inline val ImageOption.resId: Int
    get() = when (this) {
        SCALE_FILL -> imageOption_ScaleFill
        CENTER_CROP -> imageOption_CenterCrop
        MANUAL_CROP -> imageOption_ManualCrop
    }.exhaustive

fun ImageOption.Companion.fromIdRes(@IdRes idRes: Int) = when (idRes) {
    imageOption_ScaleFill -> SCALE_FILL
    imageOption_CenterCrop -> CENTER_CROP
    imageOption_ManualCrop -> MANUAL_CROP
    else -> SCALE_FILL // fallback
}
