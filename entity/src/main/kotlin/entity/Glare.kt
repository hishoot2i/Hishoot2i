package entity

import kotlinx.serialization.Serializable

@Serializable
data class Glare(
    val name: String,
    val size: Sizes,
    val position: SizesF
) {
    override fun toString(): String = "Glare(name='$name', size=$size, position=$position)"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Glare
        if (name != other.name) return false
        if (size != other.size) return false
        if (position != other.position) return false
        return true
    }
    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + position.hashCode()
        return result
    }
}
