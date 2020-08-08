@file:Suppress("NOTHING_TO_INLINE")

package entity

data class Sizes(val x: Int, val y: Int) {
    companion object {
        @JvmStatic
        val ZERO: Sizes = Sizes(0, 0)
    }

    fun toSizeF(): SizesF = SizesF(x.toFloat(), y.toFloat())
    operator fun plus(xy: Int): Sizes = Sizes(x + xy, y + xy)
    operator fun plus(other: Sizes): Sizes = Sizes(x + other.x, y + other.y)
    operator fun minus(xy: Int): Sizes = Sizes(x - xy, y - xy)
    operator fun minus(other: Sizes): Sizes = Sizes(x - other.x, y - other.y)
    operator fun div(xy: Int): Sizes = Sizes(x / xy, y / xy)
    operator fun div(other: Sizes): Sizes = Sizes(x / other.x, y / other.y)
    operator fun times(xy: Int): Sizes = Sizes(x * xy, y * xy)
    operator fun times(other: Sizes): Sizes = Sizes(x * other.x, y * other.y)
    fun max(maximumSizes: Sizes): Sizes {
        val (ratioWidth, ratioHeight) = maximumSizes.toSizeF() / this.toSizeF()
        val ratio = ratioWidth.coerceAtMost(ratioHeight)
        return (this.toSizeF() * ratio).toSize()
    }

    fun max(maximumValue: Int): Sizes = max(Sizes(maximumValue, maximumValue))
}
