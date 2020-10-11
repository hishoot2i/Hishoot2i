package entity

data class Sizes(val x: Int, val y: Int) {
    companion object {
        @JvmStatic
        val ZERO: Sizes = Sizes(0, 0)
    }

    constructor(xy: Int) : this(xy, xy)

    override fun toString(): String = "Sizes[x:$x y:$y]"

    fun toSizeF(): SizesF = SizesF(x.toFloat(), y.toFloat())
    operator fun plus(xy: Int): Sizes = this + Sizes(xy)
    operator fun plus(other: Sizes): Sizes = Sizes(x + other.x, y + other.y)
    operator fun minus(xy: Int): Sizes = this - Sizes(xy)
    operator fun minus(other: Sizes): Sizes = Sizes(x - other.x, y - other.y)
    operator fun div(xy: Int): Sizes = this / Sizes(xy)
    operator fun div(other: Sizes): Sizes = Sizes(x / other.x, y / other.y)
    operator fun times(xy: Int): Sizes = this * Sizes(xy)
    operator fun times(other: Sizes): Sizes = Sizes(x * other.x, y * other.y)
    operator fun compareTo(other: Sizes): Int {
        val (x2, y2) = other
        return (x + y).compareTo(x2 + y2)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun max(maximumSizes: Sizes): Sizes {
        val (ratioWidth, ratioHeight) = maximumSizes.toSizeF() / this.toSizeF()
        val ratio = ratioWidth.coerceAtMost(ratioHeight)
        return (this.toSizeF() * ratio).toSize()
    }

    fun max(maximumValue: Int): Sizes = max(Sizes(maximumValue))
    fun shortSide() = Sizes(x.coerceAtMost(y))
}
