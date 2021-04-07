package entity

data class ImageSourcePath @JvmOverloads constructor(
    var background: String? = null,
    var screen1: String? = null,
    var screen2: String? = null
) {
    override fun toString(): String =
        "ImageSourcePath(background=$background, screen1=$screen1, screen2=$screen2)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ImageSourcePath
        if (background != other.background) return false
        if (screen1 != other.screen1) return false
        if (screen2 != other.screen2) return false
        return true
    }
    override fun hashCode(): Int {
        var result = background?.hashCode() ?: 0
        result = 31 * result + (screen1?.hashCode() ?: 0)
        result = 31 * result + (screen2?.hashCode() ?: 0)
        return result
    }
}
