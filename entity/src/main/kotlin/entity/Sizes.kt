package entity

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class Sizes(
    @JsonNames("width", "x") val x: Int,
    @JsonNames("height", "y") val y: Int
) : Comparable<Sizes> {
    companion object {
        @JvmStatic
        val ZERO: Sizes = Sizes(0)

        @JvmStatic
        @JvmName("create")
        fun Pair<Int, Int>.toSizes() = Sizes(first, second)

        @JvmStatic // TODO:
        val comparator = compareBy(Sizes::x, Sizes::y)
    }

    constructor(xy: Int) : this(xy, xy)

    override fun toString(): String = "Sizes(x=$x y=$y)"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Sizes
        if (x != other.x) return false
        if (y != other.y) return false
        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }

    fun toSizeF(): SizesF = SizesF(x.toFloat(), y.toFloat())
    operator fun plus(xy: Int): Sizes = Sizes(x + xy, y + xy)
    operator fun plus(other: Sizes): Sizes = Sizes(x + other.x, y + other.y)
    operator fun minus(xy: Int): Sizes = Sizes(x - xy, y - xy)
    operator fun minus(other: Sizes): Sizes = Sizes(x - other.x, y - other.y)
    operator fun div(xy: Int): Sizes = Sizes(x / xy, y / xy)
    override operator fun compareTo(other: Sizes): Int = comparator.compare(this, other)
    fun max(maximumSizes: Sizes): Sizes {
        val (ratioWidth, ratioHeight) = maximumSizes.toSizeF() / this.toSizeF()
        val ratio = ratioWidth.coerceAtMost(ratioHeight)
        return (this.toSizeF() * ratio).toSize()
    }

    fun max(maximumValue: Int): Sizes = max(Sizes(maximumValue))
    fun shortSide() = Sizes(x.coerceAtMost(y))
}
