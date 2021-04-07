package entity

enum class BadgePosition {
    LEFT_TOP, CENTER_TOP, RIGHT_TOP,
    LEFT_MIDDLE, CENTER_MIDDLE, RIGHT_MIDDLE,
    LEFT_BOTTOM, CENTER_BOTTOM, RIGHT_BOTTOM;

    fun getValue(total: Sizes, source: Sizes, padding: Int): SizesF {
        require(total > Sizes.ZERO && source > Sizes.ZERO && padding > 0) {
            "padding, total and source must be positive," +
                "\npadding:$padding total:$total source:$source"
        }
        val tsp = total - source - padding
        val tsh = (total - source) / 2
        val sizes = when (this) {
            LEFT_TOP -> Sizes(padding)
            CENTER_TOP -> tsh.copy(y = padding)
            RIGHT_TOP -> tsp.copy(y = padding)
            LEFT_MIDDLE -> tsh.copy(x = padding)
            CENTER_MIDDLE -> tsh
            RIGHT_MIDDLE -> tsh.copy(x = tsp.x)
            LEFT_BOTTOM -> tsp.copy(x = padding)
            CENTER_BOTTOM -> tsh.copy(y = tsp.y)
            RIGHT_BOTTOM -> tsp
        }
        return sizes.toSizeF()
    }
}
