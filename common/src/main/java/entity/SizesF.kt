package entity

import kotlinx.serialization.Serializable

@Serializable
data class SizesF(val x: Float, val y: Float) {
    companion object {
        @JvmStatic
        val ZERO: SizesF = SizesF(0F, 0F)
    }

    constructor(xy: Float) : this(xy, xy)

    override fun toString(): String = "SizesF[x:$x y:$y]"

    fun toSize(): Sizes = Sizes(x.toInt(), y.toInt())
    operator fun plus(xy: Float): SizesF = this + SizesF(xy)
    operator fun plus(other: SizesF): SizesF = SizesF(x + other.x, y + other.y)
    operator fun minus(xy: Float): SizesF = this - SizesF(xy)
    operator fun minus(other: SizesF): SizesF = SizesF(x - other.x, y - other.y)
    operator fun div(xy: Float): SizesF = this / SizesF(xy)
    operator fun div(other: SizesF): SizesF = SizesF(x / other.x, y / other.y)
    operator fun times(xy: Float): SizesF = this * SizesF(xy)
    operator fun times(other: SizesF): SizesF = SizesF(x * other.x, y * other.y)
    operator fun compareTo(other: SizesF): Int {
        val (x2, y2) = other
        return (x + y).compareTo(x2 + y2)
    }
}
