package rbb.hishoot2i.common.entity

import android.support.annotation.Px
import rbb.hishoot2i.common.ext.exhaustive

sealed class BadgePosition(val id: Int) {
    object LeftTop : BadgePosition(LEFT_TOP)
    object CenterTop : BadgePosition(CENTER_TOP)
    object RightTop : BadgePosition(RIGHT_TOP)
    object LeftMiddle : BadgePosition(LEFT_MIDDLE)
    object CenterMiddle : BadgePosition(CENTER_MIDDLE)
    object RightMiddle : BadgePosition(RIGHT_MIDDLE)
    object LeftBottom : BadgePosition(LEFT_BOTTOM)
    object CenterBottom : BadgePosition(CENTER_BOTTOM)
    object RightBottom : BadgePosition(RIGHT_BOTTOM)

    var total: Sizes = Sizes.ZERO
    var source: Sizes = Sizes.ZERO
    fun getPosition(@Px padding: Int): SizesF {
        if (total == Sizes.ZERO || source == Sizes.ZERO) {
            throw IllegalStateException("total or source sizes == ZERO")
        }
        return when (this) {
            LeftTop -> Sizes(padding, padding)
            CenterTop -> Sizes(((total - source) / 2).x, padding)
            RightTop -> Sizes(((total - source) - padding).x, padding)
            LeftMiddle -> Sizes(padding, ((total - source) / 2).y)
            CenterMiddle -> (total - source) / 2
            RightMiddle -> Sizes(((total - source) - padding).x, ((total - source) / 2).y)
            LeftBottom -> Sizes(padding, ((total - source) - padding).y)
            CenterBottom -> Sizes(((total - source) / 2).x, ((total - source) - padding).y)
            RightBottom -> (total - source) - padding
        }.exhaustive.toSizeF()
    }

    companion object {
        @JvmStatic
        fun fromId(id: Int): BadgePosition = when (id) {
            LEFT_TOP -> LeftTop
            CENTER_TOP -> CenterTop
            RIGHT_TOP -> RightTop
            LEFT_MIDDLE -> LeftMiddle
            CENTER_MIDDLE -> CenterMiddle
            RIGHT_MIDDLE -> RightMiddle
            LEFT_BOTTOM -> LeftBottom
            CENTER_BOTTOM -> CenterBottom
            RIGHT_BOTTOM -> RightBottom
            else -> CenterBottom // fallback
        }.exhaustive

        const val LEFT_TOP: Int = 0
        const val CENTER_TOP: Int = 1
        const val RIGHT_TOP: Int = 2
        const val LEFT_MIDDLE: Int = 3
        const val CENTER_MIDDLE: Int = 4
        const val RIGHT_MIDDLE: Int = 5
        const val LEFT_BOTTOM: Int = 6
        const val CENTER_BOTTOM: Int = 7
        const val RIGHT_BOTTOM: Int = 8
    }
}