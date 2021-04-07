package entity

import kotlinx.serialization.Serializable

@Serializable
data class SizesF(val x: Float, val y: Float) : Comparable<SizesF> {
    companion object {
        @JvmStatic
        val ZERO: SizesF = SizesF(0F)

        @JvmStatic
        @JvmName("create")
        fun Pair<Float, Float>.toSizesF() = SizesF(first, second)

        @JvmStatic
        val comparator = compareBy(SizesF::x, SizesF::y)
    }

    constructor(xy: Float) : this(xy, xy)

    override fun toString(): String = "SizesF(x=$x y=$y)"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as SizesF
        if (x != other.x) return false
        if (y != other.y) return false
        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }

    fun toSize(): Sizes = Sizes(x.toInt(), y.toInt())
    operator fun plus(xy: Float): SizesF = SizesF(x + xy, y + xy)
    operator fun plus(other: SizesF): SizesF = SizesF(x + other.x, y + other.y)
    operator fun minus(other: SizesF): SizesF = SizesF(x - other.x, y - other.y)
    operator fun div(xy: Float): SizesF = SizesF(x / xy, y / xy)
    operator fun div(other: SizesF): SizesF = SizesF(x / other.x, y / other.y)
    operator fun times(xy: Float): SizesF = SizesF(x * xy, y * xy)
    override operator fun compareTo(other: SizesF): Int = comparator.compare(this, other)
}
