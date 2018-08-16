package entity

data class SizesF(val x: Float, val y: Float) {
    companion object {
        @JvmStatic
        val ZERO: SizesF = SizesF(0F, 0F)
    }

    fun toSize(): Sizes = Sizes(x.toInt(), y.toInt())
    operator fun plus(xy: Float): SizesF = SizesF(x + xy, y + xy)
    operator fun plus(other: SizesF): SizesF = SizesF(x + other.x, y + other.y)
    operator fun minus(xy: Float): SizesF = SizesF(x - xy, y - xy)
    operator fun minus(other: SizesF): SizesF =
        SizesF(x - other.x, y - other.y)

    operator fun div(xy: Float): SizesF = SizesF(x / xy, y / xy)
    operator fun div(other: SizesF): SizesF = SizesF(x / other.x, y / other.y)
    operator fun times(xy: Float): SizesF = SizesF(x * xy, y * xy)
    operator fun times(other: SizesF): SizesF =
        SizesF(x * other.x, y * other.y)
}