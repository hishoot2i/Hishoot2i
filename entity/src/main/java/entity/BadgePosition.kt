package entity

sealed class BadgePosition(val id: Int) {
    object LeftTop : BadgePosition(LEFT_TOP) {
        override fun position(padding: Int): Sizes =
            Sizes(padding, padding)
    }

    object CenterTop : BadgePosition(CENTER_TOP) {
        override fun position(padding: Int): Sizes =
            ((total - source) / 2).copy(y = padding)
    }

    object RightTop : BadgePosition(RIGHT_TOP) {
        override fun position(padding: Int): Sizes =
            ((total - source) - padding).copy(y = padding)
    }

    object LeftMiddle : BadgePosition(LEFT_MIDDLE) {
        override fun position(padding: Int): Sizes =
            ((total - source) / 2).copy(x = padding)
    }

    object CenterMiddle : BadgePosition(CENTER_MIDDLE) {
        override fun position(padding: Int): Sizes =
            ((total - source) / 2)
    }

    object RightMiddle : BadgePosition(RIGHT_MIDDLE) {
        override fun position(padding: Int): Sizes =
            ((total - source) / 2).copy(x = ((total - source) - padding).x)
    }

    object LeftBottom : BadgePosition(LEFT_BOTTOM) {
        override fun position(padding: Int): Sizes =
            ((total - source) - padding).copy(x = padding)
    }

    object CenterBottom : BadgePosition(CENTER_BOTTOM) {
        override fun position(padding: Int): Sizes =
            ((total - source) / 2).copy(y = ((total - source) - padding).y)
    }

    object RightBottom : BadgePosition(RIGHT_BOTTOM) {
        override fun position(padding: Int): Sizes =
            (total - source) - padding
    }

    var total: Sizes = Sizes.ZERO
    var source: Sizes = Sizes.ZERO
    internal abstract fun position(padding: Int): Sizes
    fun getPosition(padding: Int): SizesF {
        if (total == Sizes.ZERO || source == Sizes.ZERO) {
            throw IllegalStateException("total or source sizes == ZERO")
        }
        return position(padding).toSizeF()
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
        }

        private const val LEFT_TOP: Int = 0
        private const val CENTER_TOP: Int = 1
        private const val RIGHT_TOP: Int = 2
        private const val LEFT_MIDDLE: Int = 3
        private const val CENTER_MIDDLE: Int = 4
        private const val RIGHT_MIDDLE: Int = 5
        private const val LEFT_BOTTOM: Int = 6
        private const val CENTER_BOTTOM: Int = 7
        private const val RIGHT_BOTTOM: Int = 8
    }
}
